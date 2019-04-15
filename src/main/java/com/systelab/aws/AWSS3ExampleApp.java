package com.systelab.aws;

import java.io.IOException;
import java.net.URISyntaxException;

public class AWSS3ExampleApp {

    public static void main(String[] args) {
        S3Service service = new S3Service();
        service.getBuckets();
        service.getBucketContent("aserra.modulab");
        try {
            service.addObjectToBucket("aserra.temp", "example.html", "example.html");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
