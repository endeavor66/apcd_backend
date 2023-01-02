package com.nju.apcd.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadRecord {
    private Long id;
    private Integer successFileNumber;
    private String successFileName;
    private Integer errorFileNumber;
    private String errorFileName;
    private Date operateTime;
    private String operator;
}
