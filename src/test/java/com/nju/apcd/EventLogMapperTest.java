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
        EventLogQueryParam param = new EventLogQueryParam();
        param.setProject("zipkin");
        param.setScene("fork_merge");
        param.setCurrentPage("2");
        param.setPageSize("10");

        // 构造查询参数，分页查询(注：参数值允许为NULL，表明不添加到查询条件集合)
        QueryWrapper<EventLog> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(param.getProject())){
            queryWrapper.eq("repo", param.getProject());
        }
        if(StrUtil.isNotBlank(param.getPrNumber())){
            queryWrapper.eq("pr_number", Integer.parseInt(param.getPrNumber()));
        }
        if(StrUtil.isNotBlank(param.getScene())){
            queryWrapper.eq("scene", param.getScene());
        }
        Page<EventLog> page = Page.of(Long.parseLong(param.getCurrentPage()), Long.parseLong(param.getPageSize()));
        // 分页查询
        Page<EventLog> result = eventLogMapper.selectPage(page, queryWrapper);
        // 构造返回结果
        PageResult<EventLog> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());

        System.out.println(result.getTotal());
        System.out.println(JSON.toJSONString(pageResult));
    }
}
