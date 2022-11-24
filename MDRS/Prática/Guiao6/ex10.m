% Exercício 10

%% 10.a)
clear all
close all
clc

load('InputData2.mat')
nNodes= size(Nodes,1);
nLinks= size(Links,1);
nFlows= size(T,1);

MTBF = (450*365*24)./L;
A = MTBF./(MTBF + 24);
A(isnan(A))= 0;
A = A + eye(size(A));
Alog = -log(A);

k= 1;                           % Most available path
sP= cell(2,nFlows);
nSP= zeros(1,nFlows);
av_sum = 0;

for f=1:nFlows
    [shortestPath, totalCost] = kShortestPath(Alog,T(f,1),T(f,2),k);
    sP{1,f}= shortestPath;
    nSP(f)= length(totalCost);
    for i= 1:nSP(f)
        Aaux= Alog;
        path1= sP{1,f}{i};
        for j=2:length(path1)
            Aaux(path1(j),path1(j-1))= inf;
            Aaux(path1(j-1),path1(j))= inf;
        end
        [shortestPath, totalCost] = kShortestPath(Aaux,T(f,1),T(f,2),1);
        if ~isempty(shortestPath)
            sP{2,f}{i}= shortestPath{1};
        end
    end
    
    % calculate availabilty
    av = 1;
    % iterate through the first element of the path until the penultimate
    for node_idx = 1 : length(sP{1, f}{1}) - 1
        % first node of the link
        nodeA = sP{1, f}{1}(node_idx);
        % second node of the link
        nodeB = sP{1, f}{1}(node_idx + 1);
        % availability of the link between nodeA and nodeB
        av = av * A(nodeA, nodeB);
    end
    
    av_sum = av_sum + av;
    
    fprintf('Flow %d: Availability = %.7f - Path = %s\n', f, av, num2str(sP{1, f}{1}));
end

% 10.b)
fprintf('Average availability= %.7f\n', av_sum/nFlows);

%% 10.c)
clear all
close all
clc

load('InputData2.mat')
nNodes= size(Nodes,1);
nLinks= size(Links,1);
nFlows= size(T,1);

MTBF = (450*365*24)./L;
A = MTBF./(MTBF + 24);
A(isnan(A))= 0;
A = A + eye(size(A));
Alog = -log(A);

k= inf;                           % all paths
sP= cell(2,nFlows);
nSP= zeros(1,nFlows);
av_sum = 0;

for f = 1 : nFlows
    [shortestPath, totalCost] = kShortestPath(Alog,T(f,1),T(f,2),k);
    sP{1,f}= shortestPath;
    nSP(f)= length(totalCost);
    for i= 1:nSP(f)
        Aaux= Alog;
        path1= sP{1,f}{i};
        for j=2:length(path1)
            Aaux(path1(j),path1(j-1))= inf;
            Aaux(path1(j-1),path1(j))= inf;
        end
        [shortestPath, totalCost] = kShortestPath(Aaux,T(f,1),T(f,2),1);
        if ~isempty(shortestPath)
            sP{2,f}{i}= shortestPath{1};
        end
    end

    nPaths = 1;
    if length(sP{2,f}) > 1
        nPaths = 2;
    end
    
    % calculate availabilty
    av = ones(1,2);
    for idx = 1 : 2
        if idx == 2 && isempty(sP{2,f}{1})
            break
        end

        % iterate through the first element of the path until the penultimate
        for node_idx = 1 : length(sP{idx, f}{1}) - 1
            % first node of the link
            nodeA = sP{idx, f}{1}(node_idx);
            % second node of the link
            nodeB = sP{idx, f}{1}(node_idx + 1);
            % availability of the link between nodeA and nodeB
            av(idx) = av(idx) * A(nodeA, nodeB);
        end
    end

    if ~isempty(sP{2,f}{1})
        av(1) = 1 - ((1-av(1)) * (1-av(2)));
    end

    av_sum = av_sum + av(1);

    fprintf('Flow %d: Availability = %.7f -\tPath1 = %s\n', f, av(1), num2str(sP{1, f}{1}));
    fprintf('\t\t\t\t\tPath2 = %s\n', num2str(sP{2, f}{1}));
end

% 10.d)
fprintf('Average availability= %.7f\n', av_sum/nFlows);

%% 10.e)
clc

% Computing the link loads using the first path pair of each flow
% with 1+1 protection:
sol= ones(1,nFlows);
Loads= calculateLinkLoads1plus1(nNodes,Links,T,sP,sol);
% Determine the worst link load:
maxLoad= max(max(Loads(:,3:4)));
% Determine the total bandwidth required in all links:
TotalBand= sum(sum(Loads(:,3:4)));

fprintf('Worst link load = %.1f Gbps\n', maxLoad);
fprintf('Total bandwidth among all links = %.1f Gbps\n', TotalBand);

for i = 1 : length(Loads)
    fprintf('{%d - %d}:\t%.2f\t%.2f\n', Loads(i), Loads(i+length(Loads)), Loads(i+length(Loads)*2), Loads(i+length(Loads)*3))
    % porque é que isto é assim? nao sei
end

%% 10.f)
clc

% Computing the link loads using the first path pair of each flow
% with 1+1 protection:
sol= ones(1,nFlows);
Loads= calculateLinkLoads1to1(nNodes,Links,T,sP,sol);
% Determine the worst link load:
maxLoad= max(max(Loads(:,3:4)));
% Determine the total bandwidth required in all links:
TotalBand= sum(sum(Loads(:,3:4)));

fprintf('Worst link load = %.1f Gbps\n', maxLoad);
fprintf('Total bandwidth among all links = %.1f Gbps\n', TotalBand);

for i = 1 : length(Loads)
    fprintf('{%d - %d}:\t%.2f\t%.2f\n', Loads(i), Loads(i+length(Loads)), Loads(i+length(Loads)*2), Loads(i+length(Loads)*3))
    % porque é que isto é assim? nao sei
end