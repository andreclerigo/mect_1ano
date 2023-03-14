for i in $(seq 1 100)
do
echo -e "\nRun n.o " $i
java main.SleepingBarbers <inData
done
