package com.heb.interviewtask.vo.imagga;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectTag {
    private double confidence;

    @JsonProperty("tag")
    private LocalizedTag object;
}
