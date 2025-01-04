#!/bi$n/bash
SERVER=$1
MINWAIT=0
MAXWAIT=5
while :
do
    wget "http://${SERVER}:8080/5MBfile"  > /dev/null
    sleep $((MINWAIT+RANDOM % (MAXWAIT-MINWAIT)))
done

