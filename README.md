# WRR LB

## Topology

![topologia](https://github.com/user-attachments/assets/5128c0a8-fb29-4e17-b0a2-27d039dc9efc)



## Installation
1. Clone the repository into mininet machine (POX and mininet included).
1. Create large files for traffic simulation
    ```console
    mininet@mininet-vm:~/ssp_project$ source ./scripts/setup.sh
    ```

## Run 
1. Run POX controller
    ```console
    mininet@mininet-vm:~$ cd pox
    mininet@mininet-vm:~/pox$ ./pox.py samples.pretty_log forwarding.l2_learning openflow.spanning_tree openflow.discovery
    ```
1. Open mininet: `sudo python topology.py`
1. Run traffic simulation: `source ./scripts/simulate-traffic.sh`
