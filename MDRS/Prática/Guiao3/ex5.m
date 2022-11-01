% Aula 3

%% 5.a)
fprintf('5.a)\n');
rate = 1800;        %pps
P = 10000;          %stoping criteria
C = 10;             %10Mbps
f = 1000000;        %Bytes
N = 10;             %times to simulate

PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);
for it = 1:N
        [PL(it), APD(it), MPD(it), TT(it)] = Simulator1(rate, C, f, P);
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

%% 5.b)
fprintf('\n5.b)\n');
N = 100;             %times to simulate

PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);
for it = 1:N
        [PL(it), APD(it), MPD(it), TT(it)] = Simulator1(rate, C, f, P);
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

%% 5.c)
fprintf('\n5.c)\n');
f = 10000;        %Bytes
N = 100;             %times to simulate

PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);
for it = 1:N
        [PL(it), APD(it), MPD(it), TT(it)] = Simulator1(rate, C, f, P);
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

%Com a diminuição da fila faz com que o Packet Loss aumente mas o atraso da
%fila diminua (media e maximo)

%% 5.d)
fprintf('\n5.d)\n');
f = 2000;        %Bytes
N = 100;             %times to simulate

PL = zeros(1, N);
APD = zeros(1, N);
MPD = zeros(1, N);
TT = zeros(1, N);
for it = 1:N
        [PL(it), APD(it), MPD(it), TT(it)] = Simulator1(rate, C, f, P);
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

%Com a diminuição da fila faz com que o Packet Loss aumente mas o atraso da
%fila diminua (media e maximo), no entanto, o tamanho da queue é demasiada
%pequena porque o PacketLoss é demasiado grande e o Throughput não é
%maximizado

%% 5.e)
fprintf('\n5.e)\n');
f = 1000000;            %Bytes
rate = 1800;            %pps
capacity = 10*10^6;
x = 64:1518;

prob_left = (1 - (0.19 + 0.23 + 0.17)) / ((109 - 65 + 1) + (1517 - 111 + 1));
avg_bytes = 0.19*64 + 0.23*110 + 0.17*1518 + sum((65:109)*(prob_left)) + sum((111:1517)*(prob_left));
avg_time = avg_bytes * 8 / capacity;

S = (x .* 8) ./ (capacity);
S2 = (x .* 8) ./ (capacity);

for i = 1:length(x)
    if i == 1
        S(i) = S(i) * 0.19;
        S2(i) = S2(i)^2 * 0.19;
    elseif i == 110-64+1
        S(i) = S(i) * 0.23;
        S2(i) = S2(i)^2 * 0.23;
    elseif i == 1518-64+1
        S(i) = S(i) * 0.17;
        S2(i) = S2(i)^2 * 0.17;
    else
        S(i) = S(i) * prob_left;
        S2(i) = S2(i)^2 * prob_left;
    end
end

ES = sum(S);
ES2 = sum(S2);

throughput =  rate * avg_bytes * 8 / 10^6;
wsystem = rate * ES2 / (2*(1 - rate * ES)) + ES;
loss = ( (avg_bytes * (8 / 10^6)) / ((f * (8 / 10^6)) + capacity) ) * 100;

fprintf("Packet Loss (%%)\t= %.4f\n", loss);
fprintf("Av. Packet Delay(ms)\t= %.4f\n", wsystem * 1000);
fprintf("Throughput (Mbps)\t= %.4f \n\n", throughput);