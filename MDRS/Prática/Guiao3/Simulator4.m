function [PLd, PLv, APDd, APDv, MPDd, MPDv, TT] = Simulator4(lambda,C,f,P,n)
% INPUT PARAMETERS:
%  lambda - packet rate (packets/sec)
%  C      - link bandwidth (Mbps)
%  f      - queue size (Bytes)
%  P      - number of packets (stopping criterium)
%  n      - number of additional VoIP packets
% OUTPUT PARAMETERS:
% PLd     - Packet Loss of data packets (%)
% PLv     - Packet Loss of VoIP packets (%)
% APDd    - Average Delay of data packets (milliseconds)
% APDv    - Average Delay of VoIP packets (milliseconds)
% MPDd    - Maximum Delay of data packets (milliseconds)
% MPDv    - Maximum Delay of VoIP packets (milliseconds)
% TT      - Transmitted Throughput (data + VoIP) (Mbps) 

%Events:
ARRIVAL= 0;       % Arrival of a packet            
DEPARTURE= 1;     % Departure of a packet

%Packet type
DATA = 0;
VOIP = 1;

%State variables:
STATE = 0;          % 0 - connection free; 1 - connection bysy
QUEUEOCCUPATION= 0; % Occupation of the queue (in Bytes)
QUEUE= [];          % Size and arriving time instant of each packet in the queue

%Statistical Counters:
TOTALPACKETSD = 0;       % No. of data packets arrived to the system
TOTALPACKETSV = 0;       % No. of voip packets arrived to the system
LOSTPACKETSD = 0;        % No. of data packets dropped due to buffer overflow
LOSTPACKETSV = 0;        % No. of voip packets dropped due to buffer overflow
TRANSMITTEDPACKETSD = 0; % No. of transmitted data packets
TRANSMITTEDPACKETSV = 0; % No. of transmitted voip packets
TRANSMITTEDBYTESD = 0;   % Sum of the Bytes of transmitted data packets
TRANSMITTEDBYTESV = 0;   % Sum of the Bytes of transmitted voip packets
DELAYSD = 0;             % Sum of the delays of transmitted data packets
DELAYSV = 0;             % Sum of the delays of transmitted voip packets
MAXDELAYD = 0;           % Maximum delay among all transmitted data packets
MAXDELAYV = 0;           % Maximum delay among all transmitted voip packets

% Initializing the simulation clock:
Clock= 0;

% Initializing the List of Events with the first ARRIVAL:
tmp= Clock + exprnd(1/lambda);
EventList = [ARRIVAL, tmp, GeneratePacketSize(), tmp, DATA];

% Initializing VoIP packets
for i = 1:n
    tmp = unifrnd(0, 0.02);     %packet arrivals is unif distrib between 0 ms and 20 ms
    EventList = [EventList; ARRIVAL, tmp, randi([110, 130]), tmp, VOIP];
end

%Similation loop:
while (TRANSMITTEDPACKETSD + TRANSMITTEDPACKETSV) < P               % Stopping criterium
    EventList= sortrows(EventList,2);    % Order EventList by time
    Event= EventList(1,1);               % Get first event and 
    Clock= EventList(1,2);               %   and
    PacketSize= EventList(1,3);          %   associated
    ArrivalInstant= EventList(1,4);      %   parameters.
    PacketType= EventList(1,5);          %   get the packet type
    EventList(1,:)= [];                  % Eliminate first event
    switch Event
        case ARRIVAL                     % If first event is an ARRIVAL
            if (PacketType == DATA)      % Data Packet
                TOTALPACKETSD= TOTALPACKETSD+1;
                tmp= Clock + exprnd(1/lambda);
                EventList = [EventList; ARRIVAL, tmp, GeneratePacketSize(), tmp, DATA];
                if STATE==0
                    STATE= 1;
                    EventList = [EventList; DEPARTURE, Clock + 8*PacketSize/(C*10^6), PacketSize, Clock, DATA];
                else
                    if QUEUEOCCUPATION + PacketSize <= f
                        QUEUE= [QUEUE;PacketSize , Clock, DATA];
                        QUEUEOCCUPATION= QUEUEOCCUPATION + PacketSize;
                    else
                        LOSTPACKETSD= LOSTPACKETSD + 1;
                    end
                end
            else                                    % VoIP Packet
                TOTALPACKETSV = TOTALPACKETSV+1;
                tmp = Clock + unifrnd(0.016, 0.024);
                EventList = [EventList; ARRIVAL, tmp, randi([110, 130]), tmp, VOIP];
                if STATE == 0
                    STATE = 1;
                    EventList = [EventList; DEPARTURE, Clock + 8*PacketSize/(C*10^6), PacketSize, Clock, VOIP];
                else
                    if QUEUEOCCUPATION + PacketSize <= f
                        QUEUE = [QUEUE;PacketSize , Clock, VOIP];
                        QUEUEOCCUPATION = QUEUEOCCUPATION + PacketSize;
                    else
                        LOSTPACKETSV= LOSTPACKETSV + 1;
                    end
                end
            end            
        case DEPARTURE                     % If first event is a DEPARTURE
            if (PacketType == DATA)        % Data Packet
                TRANSMITTEDBYTESD= TRANSMITTEDBYTESD + PacketSize;
                DELAYSD= DELAYSD + (Clock - ArrivalInstant);
                if Clock - ArrivalInstant > MAXDELAYD
                    MAXDELAYD= Clock - ArrivalInstant;
                end
                TRANSMITTEDPACKETSD= TRANSMITTEDPACKETSD + 1;
            else                            % VoIP Packet
                TRANSMITTEDBYTESV= TRANSMITTEDBYTESV + PacketSize;
                DELAYSV= DELAYSV + (Clock - ArrivalInstant);
                if Clock - ArrivalInstant > MAXDELAYV
                    MAXDELAYV= Clock - ArrivalInstant;
                end
                TRANSMITTEDPACKETSV= TRANSMITTEDPACKETSV + 1;
            end
            
            % common queue ocupation handling
            if QUEUEOCCUPATION > 0
                QUEUE = sortrows(QUEUE, 3, "descend");         % Sort queue to give in descending order by column PacketType (VoIP has more priority)
                EventList = [EventList; DEPARTURE, Clock + 8*QUEUE(1,1)/(C*10^6), QUEUE(1,1), QUEUE(1,2), QUEUE(1,3)]; % Add PacketType
                QUEUEOCCUPATION= QUEUEOCCUPATION - QUEUE(1,1);
                QUEUE(1,:)= [];
            else
                STATE= 0;
            end
    end
end

%Performance parameters determination:
PLd= 100*LOSTPACKETSD/TOTALPACKETSD;                        % in %
PLv= 100*LOSTPACKETSV/TOTALPACKETSV;                        % in %
APDd= 1000*DELAYSD/TRANSMITTEDPACKETSD;                     % in milliseconds
APDv= 1000*DELAYSV/TRANSMITTEDPACKETSV;                     % in milliseconds
MPDd= 1000*MAXDELAYD;                                       % in milliseconds
MPDv= 1000*MAXDELAYV;                                       % in milliseconds
TT= 10^(-6)*(TRANSMITTEDBYTESD+TRANSMITTEDBYTESV)*8/Clock;   % in Mbps

end

function out= GeneratePacketSize()
    aux= rand();
    aux2= [65:109 111:1517];
    if aux <= 0.19
        out= 64;
    elseif aux <= 0.19 + 0.23
        out= 110;
    elseif aux <= 0.19 + 0.23 + 0.17
        out= 1518;
    else
        out = aux2(randi(length(aux2)));
    end
end