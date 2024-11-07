package it.mef.tm.elaboration.timb.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import it.mef.tm.elaboration.timb.util.ListUtility;

@Component
public class MetricsLog {

    private static final String METRICS_NAME_CPU = "process.cpu.load";
    private static final String METRICS_NAME_RAM = "process.memory.usage";

	@Value("${path.metrics}")
	private String pathMetrics;

    @Value("${metrics.enabled}")
    private boolean metricsEnabled;
	
    @Autowired
    private MeterRegistry meterRegistry;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    @PostConstruct
    public void init() {
        Gauge.builder(METRICS_NAME_CPU, this, MetricsLog::getProcessCpuLoad)
                .baseUnit("m")
                .description("CPU Load")
                .register(meterRegistry);
        Gauge.builder(METRICS_NAME_RAM, this, MetricsLog::getMemoryUsage)
                .baseUnit("byte")
                .description("Memory Usage")
                .register(meterRegistry);
    }

    public Double getProcessCpuLoad() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

            return Optional.ofNullable(list)
                    .map(l -> l.isEmpty() ? null : l)
                    .map(List::iterator)
                    .map(Iterator::next)
                    .map(Attribute.class::cast)
                    .map(Attribute::getValue)
                    .map(Double.class::cast)
                    .orElse(null);

        } catch (Exception ex) {
            return null;
        }
    }

    public Long getMemoryUsage() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=Memory");
            AttributeList list = mbs.getAttributes(name, new String[]{"HeapMemoryUsage"});

            return ListUtility.isNullOrEmpty(list) ?
                    null : ((Long) ((CompositeDataSupport) ((Attribute) list.get(0)).getValue()).get("used"));

        } catch (Exception ex) {
            return null;
        }
    }

    public void writeLogMetrics(Path fileInProgress) throws IOException {
        if (!metricsEnabled) return;
        String idFile = fileInProgress.toFile().getName();
        String dimFile = String.format("%,d kilobytes", Files.size(fileInProgress) / 1024);
//        System.out.println("FILE ID: "+idFile+" - FILE DIMENSION: "+dimFile+" - CPU LOAD: "+getProcessCpuLoad()+" - MEMORY USAGE: "+getMemoryUsage());
        Path path = Paths.get(new StringJoiner(File.separator).add(pathMetrics).add("elaboration-metrics.log").toString());
        if (path.toFile().exists() && Files.size(path) / 1024 > 10000 ) {
        	Files.move(path, Paths.get(new StringJoiner(File.separator).add(pathMetrics).add(new Date().getTime() + "-" + path.toFile().getName()).toString()));
        }
        try (BufferedWriter buffer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            buffer.write(String.format("%s | FILE ID: %s | FILE DIMENSION: %s | CPU LOAD: %s | MEMORY USAGE: %s", 
            		format.format(new Date()), idFile, dimFile, getProcessCpuLoad(), getMemoryUsage()));
            buffer.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
