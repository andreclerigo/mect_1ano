%Aula 1

%% 1.a)
p = 0.6;
n = 4;
right_answer = p + (1-p)/n;
fprintf('The probabilty of the student to select the right answer is: %d%%\n', right_answer*100);

%% 1.b)
p = 0.7;
n = 5;
knows = (p*n) / ( 1 + (n-1)*p );
fprintf('The probability of the student to known the answer when selects the right answer is: %.1f%%\n', knows*100);

%% 1.c)
x = linspace(0, 1, 100);

prob3 = 1*x + 1/3 * (1-x);
prob4 = 1*x + 1/4 * (1-x);
prob5 = 1*x + 1/5 * (1-x);

figure(1);
plot(100*x, 100*prob3, 'b', 100*x, 100*prob4, 'b--', 100*x, 100*prob5, 'b:');
yticks(0:20:100);
xticks(0:10:100);
xlabel('p (%)');
legend('n=3','n=4','n=5', 'location','northwest');
title('Probability of right answer (%)');
ylim([0, 100]);
grid on;


%% 1.d)
x = linspace(0, 1, 100);

knows3 = (x*3)./(1 + (3-1)*x);
knows4 = (x*4)./(1 + (4-1)*x);
knows5 = (x*5)./(1 + (5-1)*x);

figure(2);
plot(100*x, 100*knows3, 'b', 100*x, 100*knows4, 'b--', 100*x, 100*knows5, 'b:');
yticks(0:20:100);
xticks(0:10:100);
xlabel('p (%)');
legend('n=3','n=4','n=5', 'location','northwest');
title('Probability of knowing the answer (%)');
ylim([0, 100]);
grid on;
