%Aula 2

rate = 1000;            %pps
capacity = 10*10^6;
delay = 10*10^-6;
x = 64:1518;

%% 4.a)
prob_left = (1 - (0.19 + 0.23 + 0.17)) / ((109 - 65 + 1) + (1517 - 111 + 1));
avg_bytes = 0.19*64 + 0.23*110 + 0.17*1518 + sum((65:109)*(prob_left)) + sum((111:1517)*(prob_left));
avg_time = avg_bytes * 8 / capacity;

fprintf('The average packet size (in Bytes) is: %.2f bytes and the average packet transmission time of the IP flow is: %.2e seconds\n', avg_bytes, avg_time);

%% 4.b)
throughput = rate * avg_bytes * 8 / 10^6;
fprintf('The average throughput (in Mbps) of the IP flow is: %.2f Mbps\n', throughput);

%% 4.c)
link_capacity = capacity / (avg_bytes * 8);
fprintf('The capacity of the link is: %.2f pps\n', link_capacity);

%% 4.d)
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
wq = (rate * ES2) / (2 * (1 - rate * ES));
w = wq + avg_time + delay;

fprintf('The average packet queuing delay is: %.2e seconds and the average packet system delay of the IP flow is: %.2e seconds\n', wq, w);

%% 4.e)
y = 100:2000;
wq_func = (y * ES2) ./ (2 * (1 - y * ES));

figure(1);
plot(y, wq_func);
title("Average system delay (seconds)");
xlabel("{\lambda} (pps)")
grid on;

%% 4.f)
capacities = [10*10^6 20*10^6 100*10^6];
y1 = 100:2000;
y2 = 200:4000;
y3 = 1000:20000;

x1 = (y1 ./ (capacities(1) / (avg_bytes * 8))) * 100;
x2 = (y2 ./ (capacities(2) / (avg_bytes * 8))) * 100;
x3 = (y3 ./ (capacities(3) / (avg_bytes * 8))) * 100;

S1 = (x .* 8) ./ capacities(1);
S12 = (x .* 8) ./ capacities(1);
S2 = (x .* 8) ./ capacities(2);
S22 = (x .* 8) ./ capacities(2);
S3 = (x .* 8) ./ capacities(3);
S32 = (x .* 8) ./ capacities(3);

for i = 1:length(x)
    if i == 1
        S1(i) = S1(i) * 0.19;
        S12(i) = S12(i)^2 * 0.19;
        S2(i) = S2(i) * 0.19;
        S22(i) = S22(i)^2 * 0.19;
        S3(i) = S3(i) * 0.19;
        S32(i) = S32(i)^2 * 0.19;
    elseif i == 110-64+1
        S1(i) = S1(i) * 0.23;
        S12(i) = S12(i)^2 * 0.23;
        S2(i) = S2(i) * 0.23;
        S22(i) = S22(i)^2 * 0.23;
        S3(i) = S3(i) * 0.23;
        S32(i) = S32(i)^2 * 0.23;
    elseif i == 1518-64+1
        S1(i) = S1(i) * 0.17;
        S12(i) = S12(i)^2 * 0.17;
        S2(i) = S2(i) * 0.17;
        S22(i) = S22(i)^2 * 0.17;
        S3(i) = S3(i) * 0.17;
        S32(i) = S32(i)^2 * 0.17;
    else
        S1(i) = S1(i) * prob_left;
        S12(i) = S12(i)^2 * prob_left;
        S2(i) = S2(i) * prob_left;
        S22(i) = S22(i)^2 * prob_left;
        S3(i) = S3(i) * prob_left;
        S32(i) = S32(i)^2 * prob_left;
    end
end

wq1 = y1 .* sum(S12) ./ (2.*(1 - y1 .* sum(S1)));
wq2 = y2 .* sum(S22) ./ (2.*(1 - y2 .* sum(S2)));
wq3 = y3 .* sum(S32) ./ (2.*(1 - y3 .* sum(S3)));

avg_times = zeros(1, 3);
for i=1:3
    avg_times(i) = (avg_bytes * 8) / capacities(i);
end

sys1 = wq1 + avg_times(1) + delay;
sys2 = wq2 + avg_times(2) + delay;
sys3 = wq3 + avg_times(3) + delay;

figure(2);
plot(x1, sys1, 'b', x2, sys2, 'r', x3, sys3, 'g');
title("Average system delay (seconds)");
legend('C = 10 Mbps','C = 20 Mbps','C = 100 Mbps', 'location','northwest');
xlabel("{\lambda} (% of the link capability)")
grid on;