function plotGraph(Nodes,Links,n)
    figure(n);
    co= Nodes(:,1)+j*Nodes(:,2);
    plot(co,'o','MarkerEdgeColor','k','MarkerFaceColor','w','MarkerSize',18)
    hold on
    for i=1:size(Links,1)
        plot([Nodes(Links(i,1),1) Nodes(Links(i,2),1)],[Nodes(Links(i,1),2) Nodes(Links(i,2),2)],'b-')
    end
    plot(co,'o','MarkerEdgeColor','k','MarkerFaceColor','w','MarkerSize',18)
    for i=1:length(co)
        text(Nodes(i,1),Nodes(i,2),sprintf('%d',i),'HorizontalAlignment','center','Color','k','FontSize',14);
    end
    grid on
    hold off
end