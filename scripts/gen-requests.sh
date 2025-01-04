#!/bi$n/bash
SERVER=$1
MINWAIT=3
MAXWAIT=7
while :
do
    wget "http://${SERVER}:8080/5MBfile" -O /dev/null &
    sleep $((MINWAIT+RANDOM % (MAXWAIT-MINWAIT)))
done

