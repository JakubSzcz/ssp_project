#!/bi$n/bash -v
SERVER=$1
HOST=$2
MINWAIT=0
MAXWAIT=5
mkdir $HOST
cd $HOST
while :
do
    for i in $(seq 50 50 200); do
        wget "http://${SERVER}:8080/${i}MBfile"&
	    wget "http://${SERVER}:8081/${i+50}MBfile"&
	    wget "http://${SERVER}:8082/${i+100}MBfile"
        rm *
        sleep $((MINWAIT+RANDOM % (MAXWAIT-MINWAIT)))
    done
done
