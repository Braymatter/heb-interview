package com.heb.interviewtask.vo.imagga;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResult {
    @JsonProperty("tags")
    private List<ObjectTag> tags;

}
