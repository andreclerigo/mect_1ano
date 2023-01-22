function [sol, Loads, energy] = greedyRandomizedStrategy(nNodes, Links, T, sP, nSP, L)
    Loads = inf;
    nFlows = size(T, 1);
    % random order of flows 
    randFlows = randperm(nFlows);
    sol = zeros(1, nFlows);

    % iterate through each flow
    for flow = randFlows
        path_index = 0;
        best_Loads = inf;
        best_energy = inf;

        % test every path "possible" in a certain load
        for path = 1 : nSP(flow)
            % try the path for that flow
            sol(flow) = path;
            % calculate loads
            [Loads, linkEnergy] = calculateLinkLoadEnergy(nNodes, Links, T, sP, sol, L, 50);
            if linkEnergy < inf
                nodeEnergy = calculateNodeEnergy(T, sP, nNodes, 500, sol);
                energy = linkEnergy + nodeEnergy;
            else
                energy = inf;
            end

            % check if the current link energy is better then best link
            % energy
            if energy < best_energy
                % change index of path and load
                path_index = path;
                best_Loads = Loads;
                best_energy = energy;
            end
        end

        if path_index > 0
            sol(flow) = path_index;
        else
            energy = inf;
            break;
        end
    end
    Loads = best_Loads;
    energy = best_energy;
end
