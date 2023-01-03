package com.nju.apcd.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("process_events")
public class EventLog {
    private Long id;
    private String repo;
    private Integer prNumber;
    private String activity;
    private Date createdAt;
    private String people;
    private String scene;
}
