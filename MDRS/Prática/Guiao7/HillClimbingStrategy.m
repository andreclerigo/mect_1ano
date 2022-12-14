function [sol, load, Loads, energy] = HillClimbingStrategy(nNodes, Links, T, sP, nSP, sol, load, Loads, energy, L, alfa)
    nFlows = size(T,1);    
    % set the best local variables
    bestLocalLoad = load;
    bestLocalLoads = Loads;
    bestLocalEnergy = energy;
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
                    % calculate loads and energy
                    %Loads = calculateLinkLoads(nNodes, Links, T, sP, auxSol);
                    %auxLoad = max(max(Loads(:, 3:4)));
                    [auxLoad, auxLoads, auxEnergy] = calculateLoadEnergy(nNodes, Links, T, sP, auxSol, L, alfa);
                        
                    % check if the current load is better then start load
                    if auxEnergy < bestLocalEnergy
                        bestLocalLoad = auxLoad;
                        bestLocalLoads = auxLoads;
                        bestLocalEnergy = auxEnergy;
                        bestLocalSol = auxSol;
                    end
                end
            end
        end

        if bestLocalEnergy < energy
            load = bestLocalLoad;
            Loads = bestLocalLoads;
            energy = bestLocalEnergy;
            sol = bestLocalSol;
        else
            improved = false;
        end
    end
end