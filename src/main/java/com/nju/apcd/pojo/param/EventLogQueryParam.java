package com.nju.apcd.pojo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventLogQueryParam {
    public String project;
    public String scene;
    public String prNumber;

    // 分页参数
    public Long currentPage;
    public Long pageSize;
}
