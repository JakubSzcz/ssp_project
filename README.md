# WRR LB

## Topology

![topology](https://github.com/user-attachments/assets/36939775-b968-4a80-8be9-e8c7d127c023)

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
    rm -rf floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt/*
    cp -r java/* floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt
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
1. Add information about paths from `multipath.json` file via Rest API:
    ```console
   curl -X POST http://127.0.0.1:8080/sdnlab/paths -H 'Content-Type: application/json' -d @jsons/multipath.json
    ```
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
        source ./scripts/simulate-traffic.sh
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

## Diagrams
1. Switch

![wrr_ssp_switch](https://github.com/user-attachments/assets/0e40e726-c59a-4243-b123-3da425d3afd3)

2. Controller

![wrr_ssp_controller_hl](https://github.com/user-attachments/assets/0023b676-c718-4701-a9ff-e2834a3f8f8e)

3. WRR

![wrr_ssp_algo](https://github.com/user-attachments/assets/956a63f5-2b3c-4305-8ab4-95cfc08c9dec)
