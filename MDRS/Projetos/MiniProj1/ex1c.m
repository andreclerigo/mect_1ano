% Exercicio 1

%% 1.c)
% Equal probability for Data Packets with a size different of 64, 110 and
% 1518 Bytes
prob_left = (1 - (0.19 + 0.23 + 0.17)) / ((109 - 65 + 1) + (1517 - 111 + 1));
b = [1e-6 1e-4];                % Values of ber to test
ploss_ber = [0 0];              % Probabilities of packet loss

% Test the ber values
for i = 1 : length(b)
    % Calculate the prob. of having at least 1 error for the given packet
    % size
    for packetSize = 64 : 1518
        if (packetSize == 64)
            ploss_ber(i) = ploss_ber(i) + (1 -(nchoosek(packetSize*8, 0) * b(i)^0 * (1-b(i))^(packetSize*8))) * 0.19;
        elseif (packetSize == 110)
            ploss_ber(i) = ploss_ber(i) + (1 -(nchoosek(packetSize*8, 0) * b(i)^0 * (1-b(i))^(packetSize*8))) * 0.23;
        elseif (packetSize == 1518)
            ploss_ber(i) = ploss_ber(i) + (1 -(nchoosek(packetSize*8, 0) * b(i)^0 * (1-b(i))^(packetSize*8))) * 0.17;
        else
            ploss_ber(i) = ploss_ber(i) + (1 -(nchoosek(packetSize*8, 0) * b(i)^0 * (1-b(i))^(packetSize*8))) * prob_left;
        end
    end
end

ploss_ber = (ploss_ber ./ (0.19 + 0.23 + 0.17 + ((109 - 65 + 1) + (1517 - 111 + 1)) * prob_left)) * 100;

fprintf(['Considering that the bit error rate is the only factor that can cause Packet to be lost, the theoretical packet loss (%%)\n' ...
    'for a bit error rate of 10^-6 is: %.4f %%\nand for a bit error rate of 10^-4 is: %.4f %%\n'], ploss_ber(1), ploss_ber(2));