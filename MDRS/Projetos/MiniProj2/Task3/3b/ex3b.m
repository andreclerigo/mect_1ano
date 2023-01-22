% Exercicio 3

%% ex3.b)
clear all
close all
clc

% Initial variables
load('InputDataProject2.mat');
nNodes = size(Nodes,1);
nFlows_uni = size(T_uni, 1);
anycastNodes = [5 12];

% Traffic flows for unicast service
% Computing up to k=1 shortest path for all flows
k = 6;
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

t = tic;
timeLimit = 60;
bestLoad = inf;
bestEnergy = inf;
contador = 0;
while toc(t) < timeLimit
    % greedy randomzied start
    [sol, Loads, energy] = greedyRandomizedStrategy(nNodes, Links, T, sP, nSP, L);

    % The first solution should have a maxLinkLoad bellow the maxmium link
    % capacity
    while energy == inf
        [sol, Loads, energy] = greedyRandomizedStrategy(nNodes, Links, T, sP, nSP, L);
    end

    [sol, Loads, maxLoad, energy] = HillClimbingStrategy(nNodes, Links, T, sP, nSP, sol, Loads, energy, L);
    
    if energy < bestEnergy
        bestSol = sol;
        bestLoad = maxLoad;
        bestLoads = Loads;
        bestEnergy = energy;
        bestLoadTime = toc(t);
    end
    contador = contador + 1;
end

changedLinks = '';
for i = 1 : size(bestLoads, 1)
    if max(bestLoads(i, 3:4)) > 50
        changedLinks = append(changedLinks, ' {', num2str(bestLoads(i,1)), ', ', num2str(bestLoads(i,2)), '}');
    end
end

sleepingLinks = '';
for i = 1 : size(Loads, 1)
    if max(Loads(i, 3:4)) == 0
        sleepingLinks = append(sleepingLinks, ' {', num2str(Loads(i,1)), ', ', num2str(Loads(i,2)), '}');
    end
end

fprintf("E = %.2f \t W = %.2f \t No. sols = %d \t time = %.2f\n", bestEnergy, bestLoad, contador, bestLoadTime);
fprintf("List of links that changed to 100Gbps:%s\n", changedLinks);
fprintf('List of links in sleeping mode:%s\n', sleepingLinks);