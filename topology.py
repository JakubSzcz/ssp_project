#!/usr/bin/python

from mininet.net import Mininet
from mininet.node import Controller, RemoteController, OVSController
from mininet.node import CPULimitedHost, Host, Node
from mininet.node import OVSKernelSwitch, UserSwitch
from mininet.node import IVSSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel, info
from mininet.link import TCLink, Intf
from subprocess import call

def myNetwork():

    net = Mininet( topo=None,
                   build=False,
                   ipBase='192.168.0.0/24')

    info( '*** Adding controller\n' )
    c0=net.addController(name='c0',
                      controller=RemoteController,
                      protocol='tcp',
                      ip='127.0.0.1',
                      port=6653)

    info( '*** Add switches\n')
    s5 = net.addSwitch('s5', cls=OVSKernelSwitch)
    s2 = net.addSwitch('s2', cls=OVSKernelSwitch)
    s4 = net.addSwitch('s4', cls=OVSKernelSwitch)
    s3 = net.addSwitch('s3', cls=OVSKernelSwitch)
    s1 = net.addSwitch('s1', cls=OVSKernelSwitch)

    info( '*** Add hosts\n')
    h3 = net.addHost('h3', cls=Host, ip='192.168.0.3/24', defaultRoute=None)
    h2 = net.addHost('h2', cls=Host, ip='192.168.0.2/24', defaultRoute=None)
    h1 = net.addHost('h1', cls=Host, ip='192.168.0.1/24', defaultRoute=None)

    info( '*** Add links\n')
    net.addLink(s1, s2)
    net.addLink(s2, s3)
    net.addLink(s3, s4)
    net.addLink(s4, s1)
    net.addLink(s1, s5)
    net.addLink(s5, s3)
    net.addLink(s4, s5)
    net.addLink(s5, s2)
    net.addLink(s2, h1)
    net.addLink(s3, h2)
    net.addLink(s4, h3)

    info( '*** Starting network\n')
    net.build()
    info( '*** Starting controllers\n')
    for controller in net.controllers:
        controller.start()

    info( '*** Starting switches\n')
    net.get('s5').start([c0])
    net.get('s2').start([c0])
    net.get('s4').start([c0])
    net.get('s3').start([c0])
    net.get('s1').start([c0])

    info( '*** Post configure switches and hosts\n')

    CLI(net)
    net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    myNetwork()

