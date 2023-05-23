echo "Killing General Repository Java process"
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'killall -9 -u sd106 java'

echo "Killing Control Site Java process"
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'killall -9 -u sd106 java'

echo "Killing Concet Site Java process"
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'killall -9 -u sd106 java'

echo "Killing Museum Java process."
sshpass -f password ssh sd106@l040101-ws04.ua.pt 'killall -9 -u sd106 java'

echo "Killing Assault Party1 Java process"
sshpass -f password ssh sd106@l040101-ws05.ua.pt 'killall -9 -u sd106 java'

echo "Killing Assault Party2 Java process"
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'killall -9 -u sd106 java'

echo "Killing Master Thief Java process"
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'killall -9 -u sd106 java'

echo "Killing Ordinary Thieves Java process"
sshpass -f password ssh sd106@l040101-ws08.ua.pt 'killall -9 -u sd106 java'
