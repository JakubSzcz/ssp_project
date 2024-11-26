#!/bi$n/bash
SERVER=$1
HOST=$2
MINWAIT=0
MAXWAIT=5
mkdir $HOST
cd $HOST
while :
do
    for i in $(seq 50 50 300); do
        wget "http://${SERVER}:8080/${i}MBfile"
        rm ${i}MBfile
        sleep $((MINWAIT+RANDOM % (MAXWAIT-MINWAIT)))
    done
done

