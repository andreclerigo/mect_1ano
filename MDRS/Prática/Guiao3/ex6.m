% Aula3

%% 6.b)
fprintf('6.b)\n');
rate = 1800;        %pps
P = 10000;          %stoping criteria
C = 10;             %10Mbps
f = 1000000;        %Bytes
N = 100;            %times to simulate
b = 10^-6;          %ber        

PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);
for it = 1:N
        [PL(it), APD(it), MPD(it), TT(it)] = Simulator2(rate, C, f, P, b);
end

alfa = 0.1;         %90% confidence interval
media = mean(PL);
term = norminv(1-alfa/2)*sqrt(var(PL)/N);
fprintf('PacketLoss (%%)\t= %.2e +- %.2e\n', media, term);

media = mean(APD);
term = norminv(1-alfa/2)*sqrt(var(APD)/N);
fprintf('Av. Packet Delay (ms)\t= %.2e +- %.2e\n', media, term);

media = mean(MPD);
term = norminv(1-alfa/2)*sqrt(var(MPD)/N);
fprintf('Max. Packet Delay (ms)\t= %.2e +- %.2e\n', media, term);

media = mean(TT);
term = norminv(1-alfa/2)*sqrt(var(TT)/N);
fprintf('Throughput (Mbps)\t= %.2e +- %.2e\n', media, term);

%% 6.c)
fprintf('\n6.c)\n');
f = 10000;              %Bytes

PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);
for it = 1:N
        [PL(it), APD(it), MPD(it), TT(it)] = Simulator2(rate, C, f, P, b);
end

media = mean(PL);
term = norminv(1-alfa/2)*sqrt(var(PL)/N);
fprintf('PacketLoss (%%)\t= %.2e +- %.2e\n', media, term);

media = mean(APD);
term = norminv(1-alfa/2)*sqrt(var(APD)/N);
fprintf('Av. Packet Delay (ms)\t= %.2e +- %.2e\n', media, term);

media = mean(MPD);
term = norminv(1-alfa/2)*sqrt(var(MPD)/N);
fprintf('Max. Packet Delay (ms)\t= %.2e +- %.2e\n', media, term);

media = mean(TT);
term = norminv(1-alfa/2)*sqrt(var(TT)/N);
fprintf('Throughput (Mbps)\t= %.2e +- %.2e\n', media, term);

%% 6.d)
fprintf('\n6.d)\n');
f = 2000;               %Bytes

PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);
for it = 1:N
        [PL(it), APD(it), MPD(it), TT(it)] = Simulator2(rate, C, f, P, b);
end

media = mean(PL);
term = norminv(1-alfa/2)*sqrt(var(PL)/N);
fprintf('PacketLoss (%%)\t= %.2e +- %.2e\n', media, term);

media = mean(APD);
term = norminv(1-alfa/2)*sqrt(var(APD)/N);
fprintf('Av. Packet Delay (ms)\t= %.2e +- %.2e\n', media, term);

media = mean(MPD);
term = norminv(1-alfa/2)*sqrt(var(MPD)/N);
fprintf('Max. Packet Delay (ms)\t= %.2e +- %.2e\n', media, term);

media = mean(TT);
term = norminv(1-alfa/2)*sqrt(var(TT)/N);
fprintf('Throughput (Mbps)\t= %.2e +- %.2e\n', media, term);