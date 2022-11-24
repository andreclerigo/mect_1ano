clear all
close all
clc

load('InputData2.mat')
nNodes= size(Nodes,1);
nLinks= size(Links,1);
nFlows= size(T,1);

MTBF= (450*365*24)./L;
A= MTBF./(MTBF + 24);
A(isnan(A))= 0;
A= A + eye(size(A));
Alog= -log(A);

% Computing up to k=6 pairs of link disjoint paths
% for all flows from 1 to nFlows:
k= 6;
sP= cell(2,nFlows);
nSP= zeros(1,nFlows);
% sP{1,f}{i} is the 1st path of the i-th path pair of flow f
% sP{2,f}{i} is the 2nd path of the i-th path pair of flow f
% nSP(f) is the number of path pairs of flow f

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
end

% Visualizing all path pairs of flow 2:
f= 2;
fprintf('Flow %d:\n',f);
for i= 1:nSP(f)
    fprintf('  Path pair %d:\n',i);
    fprintf('    Path 1:  %s\n',num2str(sP{1,f}{i}));
    fprintf('    Path 2:  %s\n',num2str(sP{2,f}{i}));
end

% Computing the link loads using the first path pair of each flow
% with 1+1 protection:
sol= ones(1,nFlows);
Loads= calculateLinkLoads1plus1(nNodes,Links,T,sP,sol);
% Determine the worst link load:
maxLoad= max(max(Loads(:,3:4)))
% Determine the total bandwidth required in all links:
TotalBand= sum(sum(Loads(:,3:4)))

% Computing the link loads using the first path pair of each flow
% with 1:1 protection:
sol= ones(1,nFlows);
Loads= calculateLinkLoads1to1(nNodes,Links,T,sP,sol);
% Determine the worst link load:
maxLoad= max(max(Loads(:,3:4)))
% Determine the total bandwidth required in all links:
TotalBand= sum(sum(Loads(:,3:4)))


