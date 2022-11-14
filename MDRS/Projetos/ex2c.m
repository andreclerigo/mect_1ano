% Exercicio 2

%% 2.c)
% Equal probability for Data Packets with a size different of 64, 110 and
% 1518 Bytes
prob_left_data = (1 - (0.19 + 0.23 + 0.17)) / ((109 - 65 + 1) + (1517 - 111 + 1));
% Equal probability for VoIP Packets
prob_voip = 1 / (130 - 110 + 1);

b = 1e-5;                       % Values of ber to test
ploss_data = 0;                 % Probabilities of data packet loss
ploss_voip = 0;                 % Probabilities of VoIP packet loss

% Calculate the prob. of having at least 1 error for the given packet size
for packetSize = 64 : 1518
    if (packetSize == 64)
        ploss_data = ploss_data + (1 -(nchoosek(packetSize*8, 0) * b^0 * (1-b)^(packetSize*8))) * 0.19;
    elseif (packetSize == 110)
        ploss_data = ploss_data + (1 -(nchoosek(packetSize*8, 0) * b^0 * (1-b)^(packetSize*8))) * 0.23;
    elseif (packetSize == 1518)
        ploss_data = ploss_data + (1 -(nchoosek(packetSize*8, 0) * b^0 * (1-b)^(packetSize*8))) * 0.17;
    else
        ploss_data = ploss_data + (1 -(nchoosek(packetSize*8, 0) * b^0 * (1-b)^(packetSize*8))) * prob_left_data;
    end
end

ploss_data = (ploss_data ./ (0.19 + 0.23 + 0.17 + ((109 - 65 + 1) + (1517 - 111 + 1)) * prob_left_data)) * 100;

% Calculate the prob. of having at least 1 error for the given packet size
for packetSize = 110 : 130
    ploss_voip = ploss_voip + (1 -(nchoosek(packetSize*8, 0) * b^0 * (1-b)^(packetSize*8))) * prob_voip;
end

ploss_voip = ploss_voip .* 100;

fprintf('%f\n%f\n', ploss_data, ploss_voip);
fprintf(['Considering that the bit error rate is the only factor that can cause Packet to be lost, the theoretical packet loss (%%)\n' ...
    'for data packet is: %.4f %%\nand for a voip packets is: %.4f %%\n'], ploss_data, ploss_voip);
