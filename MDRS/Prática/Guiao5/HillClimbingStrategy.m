function [sol, load] = HillClimbingStrategy(nNodes, Links, T, sP, nSP, sol, load)
    nFlows = size(T,1);    
    % set the best local variables
    bestLocalLoad = load;
    bestLocalSol = sol;

    % Hill Climbing Strategy
    improved = true;
    while improved
        % test each flow
        for flow = 1 : nFlows
            % test each path of the flow
            for path = 1 : nSP(flow)
                if path ~= sol(flow)
                    % change the path for that flow
                    auxSol = sol;
                    auxSol(flow) = path;
                    % calculate loads
                    Loads = calculateLinkLoads(nNodes, Links, T, sP, auxSol);
                    auxLoad = max(max(Loads(:, 3:4)));
                        
                    % check if the current load is better then start load
                    if auxLoad < bestLocalLoad
                        bestLocalLoad = auxLoad;
                        bestLocalSol = auxSol;
                    end
                end
            end
        end

        if bestLocalLoad < load
            load = bestLocalLoad;
            sol = bestLocalSol;
        else
            improved = false;
        end
    end
end