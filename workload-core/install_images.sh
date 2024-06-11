#!/bin/bash
USERNAME=ubuntu
HOSTS="10.14.1.171 10.14.1.172 10.14.1.173 10.14.1.174"
SCRIPT="sudo ctr -n=k8s.io images import elaboration-timb-ms.tar"

sudo ctr -n=k8s.io images import elaboration-timb-ms/elaboration-timb-ms.tar
#sudo ctr -n=k8s.io images import scheduled-client/scheduled-client.tar

sudo scp /home/ubuntu/mef-sog-uc1/workload-core/elaboration-timb-ms/elaboration-timb-ms.tar ubuntu@10.14.1.171:/home/ubuntu/
sudo scp /home/ubuntu/mef-sog-uc1/workload-core/elaboration-timb-ms/elaboration-timb-ms.tar ubuntu@10.14.1.172:/home/ubuntu/
sudo scp /home/ubuntu/mef-sog-uc1/workload-core/elaboration-timb-ms/elaboration-timb-ms.tar ubuntu@10.14.1.173:/home/ubuntu/
sudo scp /home/ubuntu/mef-sog-uc1/workload-core/elaboration-timb-ms/elaboration-timb-ms.tar ubuntu@10.14.1.174:/home/ubuntu/

#sudo scp /home/ubuntu/mef-sog-uc1/workload-core/scheduled-client/scheduled-client.tar ubuntu@10.14.1.171:/home/ubuntu/
#sudo scp /home/ubuntu/mef-sog-uc1/workload-core/scheduled-client/scheduled-client.tar ubuntu@10.14.1.172:/home/ubuntu/
#sudo scp /home/ubuntu/mef-sog-uc1/workload-core/scheduled-client/scheduled-client.tarubuntu@10.14.1.173:/home/ubuntu/
#sudo scp /home/ubuntu/mef-sog-uc1/workload-core/scheduled-client/scheduled-client.tar ubuntu@10.14.1.174:/home/ubuntu/

#for HOSTNAME in ${HOSTS} ; do
#    ssh -o StrictHostKeyChecking=no -l ${USERNAME} ${HOSTNAME} "${SCRIPT}"
#done


#ssh ubuntu@10.14.1.171
#sudo ctr -n=k8s.io images import elaboration-timb-ms.tar
##sudo ctr -n=k8s.io images import scheduled-client.tar
#exit
#
#ssh ubuntu@10.14.1.172
#sudo ctr -n=k8s.io images import elaboration-timb-ms.tar
##sudo ctr -n=k8s.io images import scheduled-client.tar
#exit
#
#ssh ubuntu@10.14.1.173
#sudo ctr -n=k8s.io images import elaboration-timb-ms.tar
##sudo ctr -n=k8s.io images import scheduled-client.tar
#exit
#
#ssh ubuntu@10.14.1.174
#sudo ctr -n=k8s.io images import elaboration-timb-ms.tar
##sudo ctr -n=k8s.io images import scheduled-client.tar
#exit


