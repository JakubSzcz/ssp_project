#!/bi$n/bash
SERVER=$1
MINWAIT=0
MAXWAIT=5
while :
do
    for i in $(seq 5 5 30); do
        wget "http://${SERVER}:8080/${i}MBfile" > /dev/null
        rm ${i}MBfile
        sleep $((MINWAIT+RANDOM % (MAXWAIT-MINWAIT)))
    done
done

