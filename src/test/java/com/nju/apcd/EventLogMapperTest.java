package com.nju.apcd;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nju.apcd.mapper.EventLogMapper;
import com.nju.apcd.pojo.EventLog;
import com.nju.apcd.pojo.PageResult;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.EventLogQueryParam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class EventLogMapperTest {

    @Resource
    EventLogMapper eventLogMapper;

    @Test
    public void pageSearch(){

    }
}
