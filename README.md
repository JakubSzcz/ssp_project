# WRR LB

## Topology

![topologia](https://github.com/user-attachments/assets/5128c0a8-fb29-4e17-b0a2-27d039dc9efc)



## Installation

1. Clone the repository into mininet machine (POX and mininet included).
1. Run: `source ./scripts/setup.sh`

## Run 
1. Run POX controller
    ```console
    $cd pox
    $./pox.py samples.pretty_log forwarding.l2_learning openflow.spanning_tree openflow.discovery
    ```
1. Run mininet: `sudo python topology.py`
