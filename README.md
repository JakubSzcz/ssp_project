# WRR LB

## Topology

![topologia](https://github.com/user-attachments/assets/5128c0a8-fb29-4e17-b0a2-27d039dc9efc)



## Installation
1. Install floodlight machine: http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-vm.zip
1. Install x-server on host machine e.g. https://sourceforge.net/projects/vcxsrv/
1. Clone the repository into floodlight machine
1. Download and unzip floodlight controller [link](http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-1.2-lab7.zip)
    ```console
    wget http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-1.2-lab7.zip
    unzip floodlight-1.2-lab7.zip
    rm floodlight-1.2-lab7.zip
    ```
1. Copy java files to controller folder
    ```console
    rm floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt/*
    cp java/* floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt
    ```
1. Create large files for traffic simulation
    ```console
    source ./scripts/setup.sh
    ```

## Run 
1. Run Floodlight controller
    - Run x-server on host machine
    - Run eclipse
        ```console
        eclipse &
        ```
    - Launch floodlight controller project
1. Add information about hosts from `info.json` file via Rest API:
    - address: `http://<controller_ip>:8080/sdnlab/hosts`
    - method: `POST`
1. Open mininet
    ```console
    sudo python topology.py
    ```
1. Run traffic simulation
   - Option 1 - run server and traffic generation separately
        - open HTTP server on h3 (`xterm h3`)
            ```console
            source scripts/open-server.sh
            ```
            or
            ```console
            cd files
            python3 -m http.server 8080
            ```
        - run file download on h1 (`xterm h1`)
            ```console
            source scripts/gen-requests.sh 192.168.0.3
            ```
        - run file download on h2 (`xterm h2`)
            ```console
            source scripts/gen-requests.sh 192.168.0.3
            ```
   - Option 2 - run server and traffic generation in one script
        ```console
        source ./scripts/simulate-traffic.s
        ```
1. Useful commands
   - kill all wget instances
        ```console
        pkill wget
        ```
   - show wget instances with TCP ports
        ```console
        netstat -p | grep wget
        ```
