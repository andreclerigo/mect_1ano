function energy = calculateNodeEnergy(T, sP, nNodes, nc, sol)
    nodesTraffic = zeros(1, nNodes);

    for flow = 1 : size(T,1)
        if sol(flow) ~= 0
            nodes  = sP{flow}{sol(flow)};
            for n = nodes
                nodesTraffic(n) = nodesTraffic(n) + sum(T(flow, 3:4));
            end
        end
    end

    energy = sum(10 + 90 * (nodesTraffic/nc).^2);
end