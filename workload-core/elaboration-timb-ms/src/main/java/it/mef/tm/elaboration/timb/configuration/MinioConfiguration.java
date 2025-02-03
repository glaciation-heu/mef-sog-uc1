package it.mef.tm.elaboration.timb.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class MinioConfiguration {

    @Value(value = "${minio.endpoint}")
    private String endpoint;

    @Value(value = "${minio.enabled}")
    private boolean enabled;

    @Value(value = "${minio.credentials.accessKey}")
    private String accessKey;

    @Value(value = "${minio.credentials.accessKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = null;
        if (enabled) {
            client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        }
        if (client != null) System.out.println("MinioClient created");
        return client;
    }
}
