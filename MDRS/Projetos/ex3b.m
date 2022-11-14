% Exercicio 3
clc;
clear;
close all;
%% 3.b)
rate = 1500;                % rate of arrival (pps)
P = 100000;                 % stoping criteria (nr. of packets)
C = 10;                     % capacity of the connection: 10Mbps
f = 10^4;                   % queue size (Bytes)
N = 20;                     % times to run the simulation
voip_flows = [10 20 30 40]; % nr voip flows
alfa = 1 - 0.9;             % 90% confidence interval

APDdata_values = zeros(1, length(voip_flows));
APDdata_terms = zeros(1, length(voip_flows));
PLdata_values = zeros(1, length(voip_flows));
PLdata_terms = zeros(1, length(voip_flows));

APDvoip_values = zeros(1, length(voip_flows));
APDvoip_terms = zeros(1, length(voip_flows));
PLvoip_values = zeros(1, length(voip_flows));
PLvoip_terms = zeros(1, length(voip_flows));

PLdata = zeros(1, N);
PLvoip = zeros(1, N);
APDdata = zeros(1, N);
APDvoip = zeros(1, N);
MPDdata = zeros(1, N);
MPDvoip = zeros(1, N);
TT = zeros(1, N);

for i = 1:length(voip_flows)
    for it = 1:N
            [PLdata(it), PLvoip(it), APDdata(it), APDvoip(it), MPDdata(it), MPDvoip(it), TT(it)] = Simulator4(rate, C, f, P, voip_flows(i));
    end
    
    media = mean(APDdata);
    term = norminv(1-alfa/2)*sqrt(var(APDdata)/N);
    APDdata_values(i) = media;
    APDdata_terms(i) = term;

    media = mean(APDvoip);
    term = norminv(1-alfa/2)*sqrt(var(APDvoip)/N);
    APDvoip_values(i) = media;
    APDvoip_terms(i) = term;

    media = mean(PLdata);
    term = norminv(1-alfa/2)*sqrt(var(PLdata)/N);
    PLdata_values(i) = media;
    PLdata_terms(i) = term;

    media = mean(PLvoip);
    term = norminv(1-alfa/2)*sqrt(var(PLvoip)/N);
    PLvoip_values(i) = media;
    PLvoip_terms(i) = term;
end

figure(1);
hold on;
grid on;
bar(voip_flows, APDdata_values');
er = errorbar(voip_flows, APDdata_values', APDdata_terms);
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Avg. Packet Delay of DATA packets(ms)')
title('Avg. DATA Packet Delay vs VoIP Flows');
hold off;

figure(2);
hold on;
grid on;
bar(voip_flows, PLdata_values');
er = errorbar(voip_flows, PLdata_values', PLdata_terms);
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Packet Loss of DATA packets(%)')
title('DATA Packet Loss vs VoIP Flows');
hold off;

figure(3);
hold on;
grid on;
bar(voip_flows, APDvoip_values');
er = errorbar(voip_flows, APDvoip_values', APDvoip_terms);
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Avg. Packet Delay of VoIP packets(ms)')
title('Avg. VoIP Packet Delay vs VoIP Flows');
hold off;

figure(4);
hold on;
grid on;
bar(voip_flows, PLvoip_values');
er = errorbar(voip_flows, PLvoip_values', PLvoip_terms);
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Number of VoIP Flows')
ylabel('Packet Loss of VoIP packets(%)')
title('VoIP Packet Loss vs VoIP Flows');
hold off;
