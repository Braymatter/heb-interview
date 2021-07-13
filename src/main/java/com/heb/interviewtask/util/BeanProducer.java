package com.heb.interviewtask.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanProducer {
    @Bean
    public AmazonS3 produceS3Client(){
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAV762SFL5MGKR6AUR","5L9padTMKXAZVyhF+m3YuBpRkDI9QaSBvvZ6VuE3")))
                .withRegion(Regions.US_WEST_1)
                .build();
    }
}
