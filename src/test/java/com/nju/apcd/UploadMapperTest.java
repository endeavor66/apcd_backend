package com.nju.apcd;

import com.nju.apcd.mapper.UploadRecordMapper;
import com.nju.apcd.pojo.UploadRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class UploadMapperTest {

    @Autowired
    UploadRecordMapper uploadRecordMapper;

    @Test
    public void test1(){
        List<String> fileNameList = new ArrayList<>(Arrays.asList("a.txt", "b.txt", "c.txt"));
        UploadRecord record = new UploadRecord();
        record.setFileNumber(fileNameList.size());
        record.setFileName(String.join(",", fileNameList));
        // TODO 登录功能完成后，这里需要更换为登陆人
        record.setOperator("admin");
        record.setOperateTime(new Date());
        uploadRecordMapper.insert(record);
    }
}
