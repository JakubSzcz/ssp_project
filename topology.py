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

def int2dpid( dpid ):
    try:
         dpid = hex( dpid )[ 2: ]
         dpid = '0' * ( 16 - len( dpid ) ) + dpid
         return dpid
    except IndexError:
         raise Exception( 'Unable to derive default datapath ID - '
                             'please either specify a dpid or use a '
                             'canonical switch name such as s23.' )


def myNetwork():
    bandwidth = 10   # Mbit/s
    net = Mininet( topo=None,
                   build=False,
                   link=TCLink,
                   ipBase='192.168.0.0/24')

    info( '*** Adding controller\n' )
    c0=net.addController(name='c0',
                      controller=RemoteController,
                      protocol='tcp',
                      ip='127.0.0.1',
                      port=6653)

    info( '*** Add switches\n')
    s5 = net.addSwitch('s5', cls=OVSKernelSwitch, dpid=int2dpid(5))
    s2 = net.addSwitch('s2', cls=OVSKernelSwitch, dpid=int2dpid(2))
    s4 = net.addSwitch('s4', cls=OVSKernelSwitch, dpid=int2dpid(4))
    s3 = net.addSwitch('s3', cls=OVSKernelSwitch, dpid=int2dpid(3))
    s1 = net.addSwitch('s1', cls=OVSKernelSwitch, dpid=int2dpid(1))

    info( '*** Add hosts\n')
    h3 = net.addHost('h3', cls=Host, ip='192.168.0.3/24', defaultRoute=None)
    h2 = net.addHost('h2', cls=Host, ip='192.168.0.2/24', defaultRoute=None)
    h1 = net.addHost('h1', cls=Host, ip='192.168.0.1/24', defaultRoute=None)

    info( '*** Add links\n')
    net.addLink(s1, s2, bw=bandwidth)
    net.addLink(s1, s3, bw=bandwidth)
    net.addLink(s1, s5, bw=bandwidth)
    net.addLink(s2, h1, bw=bandwidth)
    net.addLink(s2, s4, bw=bandwidth)
    net.addLink(s2, s5, bw=bandwidth)
    net.addLink(s3, h3, bw=bandwidth)
    net.addLink(s3, s4, bw=bandwidth)
    net.addLink(s3, s5, bw=bandwidth)
    net.addLink(s4, s5, bw=bandwidth)
    net.addLink(s4, h2, bw=bandwidth)

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
