# Longhorn

## Installing

Installation from official website: https://longhorn.io/docs/1.6.1/deploy/install/install-with-helm/

## ReadWriteMany (RWX) Volume

The requirement for RWX to work is to install NFSv4 client.
In Longhorn system, backup feature requires NFSv4, v4.1 or v4.2, and ReadWriteMany (RWX) volume feature requires NFSv4.1. Before installing NFSv4 client userspace daemon and utilities, make sure the client kernel support is enabled on each Longhorn node.

* Check NFSv4.1 support is enabled in kernel 
    
    
    cat /boot/config-`uname -r`| grep CONFIG_NFS_V4_1

* Check NFSv4.1 support is enabled in kernel 
    
    
    cat /boot/config-`uname -r`| grep CONFIG_NFS_V4_2

The command used to install a NFSv4 client differs depending on the Linux distribution.

* For Debian and Ubuntu, use this command (on each cluster node):


    apt-get install nfs-common

Or, we also provide an nfs installer to make it easier for users to install nfs-client automatically:

    kubectl apply -f https://raw.githubusercontent.com/longhorn/longhorn/v1.6.2/deploy/prerequisite/longhorn-nfs-installation.yaml

Reference: 
* https://longhorn.io/docs/1.6.1/nodes-and-volumes/volumes/rwx-volumes/
* https://longhorn.io/docs/1.6.1/deploy/install/#installing-nfsv4-client
