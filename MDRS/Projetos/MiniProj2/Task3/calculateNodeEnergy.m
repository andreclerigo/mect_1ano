function energy = calculateNodeEnergy(T, sP, nNodes, nc, sol)
    nodesTraffic = zeros(1, nNodes);

    for flow = 1 : size(T,1)
        nodes  = sP{flow}{sol(flow)};
        for n = nodes
            nodesTraffic(n) = nodesTraffic(n) + sum(T(flow, 3:4));
        end
    end
    
    energy = 0;
    for i = 1 : length(nodesTraffic)
        energy = energy + (10 + 90 * (nodesTraffic(i)/nc)^2);
    end
end