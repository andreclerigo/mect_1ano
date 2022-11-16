function [sol, load] = greedyRandomizedStrategy(nNodes, Links, T, sP, nSP)
    nFlows = size(T,1);
    % random order of flows 
    randFlows = randperm(nFlows);
    sol = zeros(1, nFlows);

    % iterate through each flow
    for flow = randFlows
        path_index = 0;
        best_load = inf;

        % test every path "possible" in a certain load
        for path = 1 : nSP(flow)
            % try the path for that flow
            sol(flow) = path;
            % calculate loads
            Loads = calculateLinkLoads(nNodes, Links, T, sP, sol);
            load = max(max(Loads(:, 3:4)));
            
            % check if the current load is better then bestLoad
            if load < best_load
                % change index of path and load
                path_index = path;
                best_load = load;
            end
        end
        sol(flow) = path_index;
    end
    load = best_load;
end