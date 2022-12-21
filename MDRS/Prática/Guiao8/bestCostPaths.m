function [costs, sP] = bestCostPaths(nNodes, anycastNodes, D)
    % Computing up to k=1 shortest path
    k= 1;
    sP= cell(1, nNodes);
    costs = zeros(1, nNodes);
    for n = 1:nNodes
        best = inf;
        if ismember(n, anycastNodes)    % if the node is a anycastNode skip it
            costs(n) = 0;
            continue;
        end
    
        for a = 1:length(anycastNodes)
            [shortestPath, totalCost] = kShortestPath(D, n, anycastNodes(a), k);
    
            if totalCost < best
                sP{n}= shortestPath;
                best = totalCost;
                costs(n) = totalCost;
            end
        end
    end
end