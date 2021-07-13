package com.heb.interviewtask.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectionResult {
    String name;
    double confidence;
}
