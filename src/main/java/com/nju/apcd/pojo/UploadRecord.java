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
    private Integer fileNumber;
    private String fileName;
    private Date operateTime;
    private String operator;
}
