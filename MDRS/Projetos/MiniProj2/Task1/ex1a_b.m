% Exercicio 1

%% ex1.a)
clear all
close all
clc

% Initial variables
load('InputDataProject2.mat');
nNodes = size(Nodes,1);
nFlows_uni = size(T_uni, 1);
nFlows_any = size(T_any, 1);
lc = 50;        % Link capacity of 50Gbps
nc = 500;       % Node capacity of 500Gbps
anycastNodes = [5 12];

% Traffic flows for unicast service
% Computing up to k=1 shortest path for all flows
k = 1;
sP_uni = cell(1, nFlows_uni);           % sP{f}{i} is the i-th path of flow f
nSP_uni = zeros(1, nFlows_uni);         % nPS{f}{i} is the number of paths of flow f
for f = 1 : nFlows_uni
    [shortestPath, totalCost] = kShortestPath(L, T_uni(f,1), T_uni(f,2), k);
    sP_uni{f} = shortestPath;
    nSP_uni(f)= length(totalCost);
end
% Traffic flows for anycast service
[sP_any, nSP_any] = bestAnycastPaths(nNodes, anycastNodes, L, T_any);


% Reconstructing T matrix
% srcNode dstNode upRate dwRate
T_any = [T_any(:, 1) zeros(size(T_any,1), 1) T_any(:, 2:3)];
for i = 1 : size(T_any, 1) 
    T_any(i, 2) = sP_any{i}{1}(end);
end

% Calculate general T, sP and nSP
T = [T_uni; T_any];
sP = cat(2, sP_uni, sP_any);
nSP = cat(2, nSP_uni, nSP_any);

sol = ones(1, nFlows_uni + nFlows_any);
[Loads, linkEnergy] = calculateLinkLoadEnergy(nNodes, Links, T, sP, sol, L, lc);
maxLoad = max(max(Loads(:,3:4)));

for i = 1 : length(Loads)
    fprintf('{%d - %d}:\t%.2f\t%.2f\n', Loads(i), Loads(i, 2), Loads(i, 3), Loads(i, 4))
end
fprintf('Worst Link Load: %.2f Gbps\n\n', maxLoad);
%% ex1.b)
sleepingLinks = '';
for i = 1 : size(Loads, 1)
    if max(Loads(i, 3:4)) == 0
        sleepingLinks = append(sleepingLinks, ' {', num2str(Loads(i,1)), ', ', num2str(Loads(i,2)), '}');
    end
end

nodeEnergy = calculateNodeEnergy(T, sP, nNodes, nc, sol);

fprintf('Network energy consumption: %.2f\n', linkEnergy + nodeEnergy);
fprintf('List of links in sleeping mode:%s\n', sleepingLinks);