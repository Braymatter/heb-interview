package com.heb.interviewtask.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.interviewtask.services.ObjectDetectionService;
import com.heb.interviewtask.vo.DetectionResult;
import com.heb.interviewtask.vo.imagga.ImaggaStatus;
import com.heb.interviewtask.vo.imagga.ImaggaTagResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImaggaDetectionServiceImpl implements ObjectDetectionService {
    OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    public List<DetectionResult> findImageObjects(URL imageUrl) {
        Request req = new Request.Builder()
                .url(String.format("https://api.imagga.com/v2/tags?image_url=%s", imageUrl.toString()))
                .header("Authorization", "Basic YWNjXzlkODliZmE4ZmNjMTA0MTpkYjY1OGMzNDU0MDk5Y2VhNDQ5NDM0YjYyZDhiYTNkMw==")
                .build();

        Call call = client.newCall(req);
        ImaggaTagResponse tagResponse = null;

        try {
            Response response = call.execute();

            if(response.code() != HttpStatus.OK.value()){
                log.error(String.format("[findImageObjects] Received Non-200 Response from imagga. Status: %d, Image: %s", response.code(), imageUrl));
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error Detecting Objects In Image [Imagga]");
            }
            String json = response.body().string();
            tagResponse = new ObjectMapper().readValue(json, ImaggaTagResponse.class);
        } catch (IOException e) {
            String msg = String.format("[findImageObjects] IO Exception for image url: %s", imageUrl.toString());
            log.error(msg);

            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error Detecting Objects In Image [Imagga]");
        }

        ImaggaStatus result = tagResponse.getStatus();
        if(!result.getType().equalsIgnoreCase("SUCCESS")){

            String message = String.format("[findImageObjects] Non-Success Result for tag detection. Type: %s \n message: %s", result.getType(), result.getText());
            log.error(message);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Imagga API Error");
        }

        return tagResponse.getResult().getTags().stream().map(tag -> {
            DetectionResult res = new DetectionResult();
            res.setConfidence(tag.getConfidence());
            res.setName(tag.getObject().getObjectName());
            return res;
        }).collect(Collectors.toList());
    }
}
