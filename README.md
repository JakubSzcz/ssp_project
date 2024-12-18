# WRR LB

## Topology

![topologia](https://github.com/user-attachments/assets/5128c0a8-fb29-4e17-b0a2-27d039dc9efc)



## Installation
1. Install floodlight machine: http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-vm.zip
1. Clone the repository into mininet floodlight machine
1. Download and unzip floodlight controller: http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-1.2-lab2.zip
1. Copy java files to controller folder
    ```console
    rm floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt/*
    cp java/* floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt
    ```
1. Create large files for traffic simulation
    ```console
    floodlight@floodlight:~/ssp_project$ source ./scripts/setup.sh
    ```

## Run 
1. Run Floodlight controller
    - Run eclipse
        ```console
        eclipse &
        ```
    - Launch java code
1. Open mininet
    ```console
    sudo python topology.py
    ```
1. Run traffic simulation
    ```console
    source ./scripts/simulate-traffic.sh
    ```
