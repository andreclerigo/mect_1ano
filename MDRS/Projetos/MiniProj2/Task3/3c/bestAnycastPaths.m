function [sP, nSP] = bestAnycastPaths(nNodes, anycastNodes, L, T_any)
    sP = cell(1, nNodes);
    nSP = zeros(1, nNodes);
    for n = 1:nNodes
        if ismember(n, anycastNodes)    % if the node is a anycastNode skip it
            if ismember(n, T_any(:, 1))
                sP{n} = {[n n]};
                nSP(n) = 1;
            else
                nSP(n) = -1;   
            end
            continue;
        end

        if ~ismember(n, T_any(:, 1))     % node is not from T_any matrix
            nSP(n) = -1;
            continue;
        end

        best = inf;
        for a = 1:length(anycastNodes)
            [shortestPath, totalCost] = kShortestPath(L, n, anycastNodes(a), 1);
            
            if totalCost(1) < best
                sP{n} = shortestPath;
                nSP(n) = length(totalCost);
                best = totalCost;
            end
        end
    end
    
    nSP = nSP(nSP~=-1);                 % remove unwanted values
    sP = sP(~cellfun(@isempty, sP));    % remove empty entry from the cell array
end
