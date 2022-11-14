% Exercicio 2

%% 2.b)
rate = 1500;                % rate of arrival to the queue in pps
P = 100000;                 % Packets to be transmitted (stoping criteria)
C = 10;                     % Capacity of connectio with 10Mbps
f = 10000;                  % Queue Size in Bytes
N = 20;                     % Times to run the simulation
nVoips = [10 20 30 40];     % nr voip packets flows
b = 10^-5;                  % Bit error rate
alfa = 0.1;                 % 90% confidence interval for results

% Variables to store bar graph data
PLd_values = zeros(1, length(nVoips));
PLd_terms = zeros(1, length(nVoips));
PLv_values = zeros(1, length(nVoips));
PLv_terms = zeros(1, length(nVoips));
APDd_values = zeros(1, length(nVoips));
APDd_terms = zeros(1, length(nVoips));
APDv_values = zeros(1, length(nVoips));
APDv_terms = zeros(1, length(nVoips));

% Variables to store the simulation results
PLd = zeros(1, N);
PLv = zeros(1, N);
APDd = zeros(1, N);
APDv = zeros(1, N);
MPDd = zeros(1, N);
MPDv = zeros(1, N);
TT = zeros(1, N);

% Test all number of VoIP flows
for i = 1 : length(nVoips)
    % Run the simulator N times
    for it = 1:N
        [PLd(it), PLv(it), APDd(it), APDv(it), MPDd(it), MPDv(it), TT(it)] = Simulator3A(rate, C, f, P, nVoips(i), b);
    end

    % Calculate Avg. Packet Loss for Data Packets
    media = mean(PLd);
    term = norminv(1-alfa/2)*sqrt(var(PLd)/N);
    PLd_values(i) = media;
    PLd_terms(i) = term;
    
    % Calculate Avg. Packet Loss for VoIP Packets
    media = mean(PLv);
    term = norminv(1-alfa/2)*sqrt(var(PLv)/N);
    PLv_values(i) = media;
    PLv_terms(i) = term;
    
    % Calculate Avg. Packet Delay for Data Packets
    media = mean(APDd);
    term = norminv(1-alfa/2)*sqrt(var(APDd)/N);
    APDd_values(i) = media;
    APDd_terms(i) = term;
    
    % Calculate Avg. Packet Delay for VoIP Packets
    media = mean(APDv);
    term = norminv(1-alfa/2)*sqrt(var(APDv)/N);
    APDv_values(i) = media;
    APDv_terms(i) = term;
end

% Figure to show the asked results for 2.b) i)
figure(1);
hold on;
grid on;
bar(nVoips, APDd_values');                                  % Bar Graph
ylim([0 4]);                                                % Set y axis values between 0 and 4
er = errorbar(nVoips, APDd_values', APDd_terms);            % Set the error bar
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Avg. Data Packet Delay (ms)')
title('Average Data Packet Delay vs Number of VoIP Flows');
hold off;

% Figure to show the asked results for 2.b) ii)
figure(2);
hold on;
grid on;
bar(nVoips, PLd_values');                                   % Bar Graph
er = errorbar(nVoips, PLd_values', PLd_terms);              % Set the error bar
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Avg. Data Packet Loss (%)')
title('Avg. Data Packet Loss vs Number of VoIP Flows');
hold off;

% Figure to show the asked results for 2.b) iii)
figure(3);
hold on;
grid on;
bar(nVoips, APDv_values');                                  % Bar Graph
ylim([0 4]);                                                % Set y axis values between 0 and 4
er = errorbar(nVoips, APDv_values', APDv_terms);            % Set the error bar
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Avg. VoIP Packet Delay (ms)')
title('Average VoIP Packet Delay vs Number of VoIP Flows');
hold off;

% Figure to show the asked results for 2.b) iv)
figure(4);
hold on;
grid on;
bar(nVoips, PLv_values');                                   % Bar Graph
er = errorbar(nVoips, PLv_values', PLv_terms);              % Set the error bar
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Avg. VoIP Packet Loss (%)')
title('Avg. VoIP Packet Loss vs Number of VoIP Flows');
hold off;


% mesmas conclusoes que a anterior mas packetloss aumentar com o aumento de
% nr de flows