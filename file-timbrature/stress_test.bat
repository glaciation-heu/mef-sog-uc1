@echo off
set loopcount=100
curl -X POST "http://10.14.1.170:8081/scheduled-client/api.noipa.it/sec/workload/V1/startWorkload" -H "accept: */*" -d ""
:loop
    curl -X POST "http://10.14.1.170:8081/scheduled-client/api.noipa.it/sec/file/timestamps/V1/upload" -H "accept: application/json" -H "Content-Type: multipart/form-data" -F "file=@Timbrature per Maggio 2023.DAY"
    curl -X POST "http://10.14.1.170:8081/scheduled-client/api.noipa.it/sec/file/timestamps/V1/upload" -H "accept: application/json" -H "Content-Type: multipart/form-data" -F "file=@Timbrature per Maggio 2023_2.DAY"
    set /a loopcount=loopcount-1
    if %loopcount%==0 goto exitloop
goto loop
:exitloop
pause
