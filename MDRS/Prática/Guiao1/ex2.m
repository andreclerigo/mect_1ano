%Aula 1

%% 2.a)
p = 10^-2;
bytes = 100;
bits = 8;
no_errors = nchoosek(bytes*bits, 0) * (1-p)^(bytes*bits);

fprintf('The probability of a data frame of 100 Bytes to be received without errors is: %.4f%%\n', 100*no_errors);

%% 2.b)
p = 10^-3;
bytes = 1000;
one_error = nchoosek(bytes*bits, 1) * p * (1-p)^(bytes*bits);
fprintf('The probability of a data frame of 1000 Bytes to be received with exactly one error is: %.4f%%\n', 100*one_error);

%% 2.c)
p = 10^-4;
bytes = 200;

one_or_more_errors = 1 - (nchoosek(bytes*bits, 0) * (1-p)^(bytes*bits));
fprintf('The probability of a data frame of 200 Bytes to be received with one or more errors is: %.4f%%\n', 100*one_or_more_errors);

%% 2.d)
x = logspace(-8, -2);

no_errors100 = nchoosek(100*bits, 0) .* (1-x).^(100*bits);
no_errors200 = nchoosek(200*bits, 0) .* (1-x).^(200*bits);
no_errors1000 = nchoosek(1000*bits, 0) .* (1-x).^(1000*bits);

figure(1);
semilogx(x, 100*no_errors100, 'b', x, 100*no_errors200, 'b--', x, 100*no_errors1000, 'b:');
xlabel('Bit Error Rate');
legend('100 Bytes','200 Bytes','1000 Bytes', 'location','southwest');
title('Probability of packet reception without errors (%)');
grid on;

%% 2.e)
x = linspace(64*bits, 1518*bits);

no_errors4 = (1-10^-4).^x;
no_errors3 = (1-10^-3).^x;
no_errors2 = (1-10^-2).^x;

figure(2);
semilogy(x, no_errors4, 'b', x, no_errors3, 'b--', x, no_errors2, 'b:');
xlabel('Packet Size (Bytes)');
legend('ber=1e-4','ber=1e-3','ber=1e-2', 'location','southwest');
title('Probability of packet reception without errors');
grid on;
