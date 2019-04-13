package com.systelab.aws;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AWSS3ExampleApp {
    Region region = Region.EU_CENTRAL_1;
    S3Client s3 = S3Client.builder().region(region).build();


    public void deleteBucket(String bucketName) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        s3.deleteBucket(deleteBucketRequest);
    }

    public void deleteBucketObject(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
        s3.deleteObject(deleteObjectRequest);
    }

    public void getBucketObject(String bucketName, String key, String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
        s3.getObject(getObjectRequest, ResponseTransformer.toFile(Paths.get(fileName)));
    }

    private ByteBuffer getBytesFromFile(String fileName) throws IOException, URISyntaxException {
        URL res = getClass().getClassLoader().getResource(fileName);
        byte[] bFile = Files.readAllBytes(Paths.get(res.toURI()));
        return ByteBuffer.wrap(bFile);
    }

    public void addObjectToBucket(String bucketName, String key, String fileName) throws IOException, URISyntaxException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();
        s3.putObject(putObjectRequest, RequestBody.fromByteBuffer(getBytesFromFile(fileName)));
    }

    public void deleteBucketContent(String bucketName) {
        ListObjectsV2Response listObjectsV2Response;
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).maxKeys(1).build();
        do {
            listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
            for (S3Object s3Object : listObjectsV2Response.contents()) {
                deleteBucketObject(bucketName, s3Object.key());
            }

            listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName)
                    .continuationToken(listObjectsV2Response.nextContinuationToken())
                    .build();

        } while (listObjectsV2Response.isTruncated());
        deleteBucket(bucketName);
    }

    public void getBucketContent(String bucketName) {
        ListObjectsV2Response listObjectsV2Response;
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).maxKeys(1).build();
        do {
            listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
            for (S3Object s3Object : listObjectsV2Response.contents()) {
                System.out.println(s3Object.key());
            }

            listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName)
                    .continuationToken(listObjectsV2Response.nextContinuationToken())
                    .build();

        } while (listObjectsV2Response.isTruncated());
    }


    public void createBucket(String bucketName) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest
                .builder()
                .bucket(bucketName)
                .createBucketConfiguration(CreateBucketConfiguration.builder()
                        .locationConstraint(region.id())
                        .build())
                .build();
        s3.createBucket(createBucketRequest);
    }

    public void getBuckets() {
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));
    }

    public static void main(String[] args) {
        AWSS3ExampleApp example = new AWSS3ExampleApp();
        example.getBuckets();
        example.getBucketContent("aserra.temp");
        try {
            example.addObjectToBucket("aserra.temp", "example.html", "example.html");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
