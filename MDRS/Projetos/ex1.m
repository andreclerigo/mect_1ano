% Exercicio 1

%% 1.a)
rates = [1500 1600 1700 1800 1900]; % rate of arrival to the queue in pps
P = 100000;                         % Packets to be transmitted (stoping criteria)
C = 10;                             % Capacity of connectio with 10Mbps
f = 1000000;                        % Queue Size in Bytes
N = 20;                             % Times to run the simulation
b = 10^-6;                          % Bit error rate
alfa = 0.1;                         % 90% confidence interval for results

% Variables to store bar graph data
APD_values = zeros(1, length(rates));
APD_terms = zeros(1, length(rates));
PL_values = zeros(1, length(rates));
PL_terms = zeros(1, length(rates));

% Variables to store the simulation results
PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);

% Test all rates required
for i = 1:length(rates)
    % Run the simulator N times
    for it = 1:N
            [PL(it), APD(it), MPD(it), TT(it)] = Simulator2(rates(i), C, f, P, b);
    end
    
    % Calculate Avg. Packet Delay
    media = mean(APD);
    term = norminv(1-alfa/2)*sqrt(var(APD)/N);
    APD_values(i) = media;
    APD_terms(i) = term;
    
    % Calculate Avg. Packet Loss
    media = mean(PL);
    term = norminv(1-alfa/2)*sqrt(var(PL)/N);
    PL_values(i) = media;
    PL_terms(i) = term;
end

% Figure to show the asked results for 1.a) i)               
figure(1);
hold on;
grid on;
bar(rates, APD_values');                            % Bar Graph
ylim([0 9]);                                        % Set y axis values between 0 and 9
er = errorbar(rates, APD_values', APD_terms);       % Set the error bar
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Packet Rate (pps)') 
ylabel('Avg. Packet Delay (ms)')
title('Average Packet Delay vs Packet Rate');
hold off;

% Figure to show the asked results for 1.b) i)
figure(2);
hold on;
grid on;
bar(rates, PL_values');                             % Bar Graph
er = errorbar(rates, PL_values', PL_terms);         % Set the error bar
er.Color = [0 0 0];
er.LineStyle = 'none';
xlabel('Packet Rate (pps)')
ylabel('Packet Loss (%)')
title('Packet Loss vs Packet Rate');
hold off;
