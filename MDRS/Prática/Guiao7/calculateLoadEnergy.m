function [load, Loads, energy] = calculateLoadEnergy(nNodes,Links,T,sP,Solution, L, alfa)
    nFlows= size(T,1);
    nLinks= size(Links,1);
    aux= zeros(nNodes);
    for i= 1:nFlows
        if Solution(i)>0
            path= sP{i}{Solution(i)};
            for j=2:length(path)
                aux(path(j-1),path(j))= aux(path(j-1),path(j)) + T(i,3); 
                aux(path(j),path(j-1))= aux(path(j),path(j-1)) + T(i,4);
            end
        end
    end
    Loads= [Links zeros(nLinks,2)];
    energy = 0;
    for i= 1:nLinks
        Loads(i,3)= aux(Loads(i,1),Loads(i,2));
        Loads(i,4)= aux(Loads(i,2),Loads(i,1));
        
        % link in sleeping mode
        if max(Loads(i, 3:4)) == 0
            energy = energy + 0.5;
        else
            %nodeA = Loads(i, 1);
            %nodeB = Loads(i, 2);
            % len from nodeA to nodeB
            len = L(Loads(i, 1), Loads(i, 2));
            energy = energy + 10 + 0.1 * len;
        end
    end
    
    load = max(max(Loads(:, 3:4)));
    % If the worst link load is greater than max capacity , energy will be infinite    
    if load > alfa*10
        energy = inf;
    end
end
