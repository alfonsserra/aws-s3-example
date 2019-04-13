package com.systelab.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

public class AWSS3ExampleApp {

    public void getBuckets() {
        Region region = Region.EU_CENTRAL_1;
        S3Client s3 = S3Client.builder().region(region).build();

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));
    }

    public static void main(String[] args) {
        AWSS3ExampleApp example = new AWSS3ExampleApp();
        example.getBuckets();
    }
}
