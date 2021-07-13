package com.heb.interviewtask.vo.imagga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImaggaStatus {
    private String text;
    private String type; //Could be an enum if we wanted
}
