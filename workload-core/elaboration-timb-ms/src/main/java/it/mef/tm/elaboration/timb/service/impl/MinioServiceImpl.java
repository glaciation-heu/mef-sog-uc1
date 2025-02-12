package it.mef.tm.elaboration.timb.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import it.mef.tm.elaboration.timb.service.MinioService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Service
@Data
public class MinioServiceImpl implements MinioService {

    @Value(value = "${minio.bucket}")
    private String bucket;

    @Value(value = "${minio.subPathBucket}")
    private String subPathBucket;

    @Value(value = "${minio.endpoint}")
    private String endpoint;

    @Value(value = "${minio.credentials.accessKey}")
    private String accessKey;

    @Value(value = "${minio.credentials.secretKey}")
    private String secretKey;

    @Value(value = "${minio.enabled}")
    private boolean minioEnabled;

    @Override
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public void checkBucket(MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } else {
            System.out.println("Bucket "+ bucket +" already exists.");
        }
    }

    @Override
    public void uploadObject(String objectName, String objectPath, Map<String, String> tags) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = minioClient();
        checkBucket(minioClient);
        String name = objectName;
        if (subPathBucket != null && !subPathBucket.isEmpty()) {
            name = subPathBucket + objectName;
        }

        minioClient.traceOn(System.out);
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucket)
                        .tags(tags)
                        .object(name)
                        .filename(objectPath)
                        .contentType("application/xml")
                        .build());
    }


}
