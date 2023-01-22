function [sol, Loads, maxLoad, energy] = HillClimbingStrategy(nNodes, Links, T, sP, nSP, sol, Loads, energy, L)
    nFlows = size(T,1);    
    % set the best local variables
    maxLoad = max(max(Loads(:, 3:4)));
    bestLocalLoads = Loads;
    bestLocalSol = sol;
    bestLocalEnergy = energy;

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
                    [auxLoads, auxLinkEnergy] = calculateLinkLoadEnergy(nNodes, Links, T, sP, auxSol, L);
                    nodeEnergy = calculateNodeEnergy(T, sP, nNodes, 500, auxSol);
                    auxEnergy = auxLinkEnergy + nodeEnergy;
                        
                    % check if the current link energy is better then best
                    % local energy
                    if auxEnergy < bestLocalEnergy
                        bestLocalLoads = auxLoads;
                        bestLocalSol = auxSol;
                        bestLocalEnergy = auxEnergy;
                    end
                end
            end
        end

        if bestLocalEnergy < energy
            Loads = bestLocalLoads;
            sol = bestLocalSol;
            energy = bestLocalEnergy;

            if Loads == Inf
                maxLoad = Inf;
            else
                maxLoad = max(max(Loads(:, 3:4)));
            end
        else
            improved = false;
        end
    end
end