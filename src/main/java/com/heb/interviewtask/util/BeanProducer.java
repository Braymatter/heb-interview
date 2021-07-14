package com.heb.interviewtask.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BeanProducer {
    Environment env;

    @Autowired
    public BeanProducer(Environment env){
        this.env = env;
    }
    @Bean
    public AmazonS3 produceS3Client(){
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(env.getProperty("S3_ACCESS_KEY"),env.getProperty("S3_SECRET_KEY"))))
                .withRegion(Regions.US_WEST_1)
                .build();
    }
}
