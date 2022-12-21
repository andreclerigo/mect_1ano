% Exercicio 12

%% ex12.a)
clear all
close all
clc

load('InputData2.mat')
nNodes= size(Nodes,1);
v = 2*10^5;             % v = 2x10^5 km/sec
D = L/v;                % Propagation Delay matrix
dwThroughput = [0.7 1.5 2.1 1.0 1.3 2.7 2.2 0.8 1.7 1.9 2.8;
                0.1 0.2 0.3 0.1 0.1 0.4 0.3 0.1 0.2 0.3 0.4];
dwThroughput = dwThroughput';
anycastNodes = [3 5];
sourceNodes = setdiff(1:nNodes, anycastNodes);

[costs, sP] = bestCostPaths(nNodes, anycastNodes, D);

fprintf("Anycast Nodes = %s\n", num2str(anycastNodes));
[worstCost, worstNode] = max(costs);
fprintf("Node with the worst round-trip delay: %d\n", worstNode);
fprintf("W = %.2f ms A = %.2f ms\n\n", worstCost * 2 * 1000, mean(costs) * 2 * 1000);

% ex12.b)
% Construcint the matrix T
T = zeros(length(sourceNodes), 4);
idx = 1;
% srcNode anyNode upSrc dwSrc
for p = sP
    if isempty(p{1})
        continue;
    end

    srcN = p{1}{1}(1);
    dstN = p{1}{1}(end);
    T(idx, 1) = srcN;
    T(idx, 2) = dstN;
    T(idx, 3) = dwThroughput(srcN, 2);
    T(idx, 4) = dwThroughput(srcN, 1);

    idx = idx + 1;
end

sol = ones(1, nNodes-length(anycastNodes));
sP = sP(~cellfun(@isempty, sP));    % remove empty entry from the cell array

Loads = calculateLinkLoads(nNodes, Links, T, sP, sol);
% Determine the worst link load:
maxLoad= max(max(Loads(:,3:4)));
fprintf("Anycast Nodes = %s\n", num2str(anycastNodes));
fprintf('Worst link load = %.1f Gbps\n', maxLoad);
for i = 1 : length(Loads)
    fprintf('{%d - %d}:\t%.2f\t%.2f\n', Loads(i), Loads(i+length(Loads)), Loads(i+length(Loads)*2), Loads(i+length(Loads)*3))
end

%% ex12.c)
clear all
close all
clc

load('InputData2.mat')
nNodes= size(Nodes,1);
v = 2*10^5;             % v = 2x10^5 km/sec
D = L/v;                % Propagation Delay matrix
dwThroughput = [0.7 1.5 2.1 1.0 1.3 2.7 2.2 0.8 1.7 1.9 2.8;
                0.1 0.2 0.3 0.1 0.1 0.4 0.3 0.1 0.2 0.3 0.4];
dwThroughput = dwThroughput';

combs = nchoosek(1:11, 2);      % all combinations of anycastNodes
bestWorstCost = inf;
bestAnycastNodes = combs(1, :);
bestSP = cell(1, nNodes);

for i = 1 : size(combs, 1)
    anycastNodes = combs(i, :);
    sourceNodes = setdiff(1:nNodes, anycastNodes);
    
    [costs, sP] = bestCostPaths(nNodes, anycastNodes, D);
    [worstCost, worstNode] = max(costs);

    if worstCost < bestWorstCost
        bestWorstCost = worstCost;
        bestAnycastNodes = anycastNodes;
        bestSP = sP;
    end
end

sP = bestSP;                % renew sP as the bestSP value
% Construcint the matrix T
T = zeros(length(sourceNodes), 4);
idx = 1;
% srcNode anyNode upSrc dwSrc
for p = sP
    if isempty(p{1})
        continue;
    end

    srcN = p{1}{1}(1);
    dstN = p{1}{1}(end);
    T(idx, 1) = srcN;
    T(idx, 2) = dstN;
    T(idx, 3) = dwThroughput(srcN, 2);
    T(idx, 4) = dwThroughput(srcN, 1);

    idx = idx + 1;
end

sol = ones(1, nNodes-length(anycastNodes));
sP = sP(~cellfun(@isempty, sP));    % remove empty entry from the cell array

Loads = calculateLinkLoads(nNodes, Links, T, sP, sol);
% Determine the worst link load:
maxLoad= max(max(Loads(:,3:4)));

fprintf("Anycast Nodes = %s\n", num2str(bestAnycastNodes));
fprintf("Worst round-trip delay = %.2f ms\n", bestWorstCost * 2 * 1000);
fprintf("Worst link load = %.2f Gbps\n", maxLoad);

%% ex12.d)
clear all
close all
clc

load('InputData2.mat')
nNodes= size(Nodes,1);
nFlows= size(T,1);
v = 2*10^5;             % v = 2x10^5 km/sec
D = L/v;                % Propagation Delay matrix
dwThroughput = [0.7 1.5 2.1 1.0 1.3 2.7 2.2 0.8 1.7 1.9 2.8;
                0.1 0.2 0.3 0.1 0.1 0.4 0.3 0.1 0.2 0.3 0.4];
dwThroughput = dwThroughput';

combs = nchoosek(1:11, 2);      % all combinations of anycastNodes
bestMaxLoad = inf;
bestCosts = inf;
bestAnycastNodes = zeros(1, 2);

for i = 1 : size(combs, 1)      % number of rows
    anycastNodes = combs(i, :);
    sourceNodes = setdiff(1:nNodes, anycastNodes);
    [costs, sP] = bestCostPaths(nNodes, anycastNodes, D);
    
    % Construcint the matrix T
    T = zeros(length(sourceNodes), 4);
    idx = 1;
    % srcNode anyNode upSrc dwSrc
    for p = sP
        if isempty(p{1})
            continue;
        end
    
        srcN = p{1}{1}(1);
        dstN = p{1}{1}(end);
        T(idx, 1) = srcN;
        T(idx, 2) = dstN;
        T(idx, 3) = dwThroughput(srcN, 2);
        T(idx, 4) = dwThroughput(srcN, 1);
    
        idx = idx + 1;
    end
    
    sol = ones(1, nNodes-length(anycastNodes));
    sP = sP(~cellfun(@isempty, sP));    % remove empty entry from the cell array
    
    Loads = calculateLinkLoads(nNodes, Links, T, sP, sol);
    % Determine the worst link load:
    maxLoad= max(max(Loads(:,3:4)));

    if maxLoad < bestMaxLoad
        bestMaxLoad = maxLoad;
        bestAnycastNodes = anycastNodes;
        bestCosts = costs;
    end
end

[worstCost, worstNode] = max(bestCosts);

fprintf("Anycast Nodes = %s\n", num2str(bestAnycastNodes));
fprintf("Worst round-trip delay = %.2f ms\n", worstCost * 2 * 1000);
fprintf("Worst link load = %.2f Gbps\n", bestMaxLoad);