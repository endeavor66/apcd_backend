package com.nju.apcd.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("permission_change")
public class PermissionChange {
    private Long id;
    private String repo;
    private String people;
    private String prNumber;
    private Date changeTime;
    private String permission;
}
