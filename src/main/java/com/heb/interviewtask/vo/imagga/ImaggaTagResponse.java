package com.heb.interviewtask.vo.imagga;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImaggaTagResponse {
    @JsonProperty("result")
    private TagResult result;

    @JsonAlias("status")
    private ImaggaStatus status;
}
