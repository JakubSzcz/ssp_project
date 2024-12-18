# WRR LB

## Topology

![topologia](https://github.com/user-attachments/assets/5128c0a8-fb29-4e17-b0a2-27d039dc9efc)



## Installation
On the host machine:
1. Install mininet virtual machine: https://github.com/mininet/mininet/releases/download/2.2.2/mininet-2.2.2-170321-ubuntu-14.04.4-server-amd64.zip
1. Install x-server e.g. https://sourceforge.net/projects/vcxsrv/
1. Clone the repository (WSL recommended):
    ```console
    git clone https://github.com/JakubSzcz/ssp_project.git
    cd ssp_project
    ```
1. Download and unzip floodlight controller [link](http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-1.2-lab7.zip)
    ```console
    wget http://www.kt.agh.edu.pl/~rzym/lectures/TI-SDN/floodlight-1.2-lab7.zip
    unzip floodlight-1.2-lab7.zipa
    rm floodlight-1.2-lab7.zip
    ```
1. Copy java files to controller folder
    ```console
    rm floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt/*
    cp java/* floodlight-1.2-lab7/src/main/java/pl/edu/agh/kt
    ```

On the mininet virtual machine:
1. Clone the repository
    ```console
    git clone https://github.com/JakubSzcz/ssp_project.git
    cd ssp_project
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
    ```console
    source ./scripts/simulate-traffic.sh
    ```
