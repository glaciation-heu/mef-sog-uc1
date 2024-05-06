# Instructions of demo

<ol>
  <li>Connect to OpenVPN</li>
  <li>Open Command Prompt #1 and connect via ssh to the master node: 

    ssh ubuntu@10.14.1.170

  </li>
  <li>Open Command Prompt #2 copy one or more files into the scheduled-client clocking directory using the scp command: 

    scp "test-file.xxx" ubuntu@10.14.1.171:/home/ubuntu/volume/tm-gla/mef/timbrature/

or connect via VPN to the address:

    http://10.14.1.170:8081/scheduled-client/swagger-ui/
      
  </li>
  <li>Open Command Prompt #3 and run the following command:
        
    ssh ubuntu@10.14.1.170
    kubectl exec --tty -i kafka-client --namespace kafka -- bash
    kafka-console-consumer.sh --bootstrap-server kafka.kafka.svc.cluster.local:9092 --topic test 

  right now you are listening as a consumer on the test queue

  </li>
  <li>On Command Prompt #1 execute the scheduler start curl:
        
    curl -X POST http://localhost:30001/scheduled-client/api.noipa.it/sec/workload/V1/startWorkload

or use the swagger interface.
  
  </li>
  <li>On Command Prompt #3 you will be able to see the message entered on kafka with the name of the uploaded file.</li>
  <li>If you want to view the actual movement of the file, use Command Prompt #2 to execute the following commands:

    ssh ubuntu@10.14.1.171
    cd /volume/tm-gla/mef/to-be-elaborated/
    ll

  </li>
  <li>If you want to stop the scheduler on Command Prompt #1 run curl stop the scheduler: 
    
    curl -X POST http://localhost:30001/scheduled-client/api.noipa.it/sec/workload/V1/stopWorkload

or use the swagger interface.
  
  </li>
    
</ol>
