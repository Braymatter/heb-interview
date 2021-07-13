package com.heb.interviewtask.services;

import com.heb.interviewtask.vo.DetectionResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

@Service
public interface ObjectDetectionService {
    public List<DetectionResult> findImageObjects(URL imageUrl);
}
