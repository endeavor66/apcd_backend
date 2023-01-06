package com.nju.apcd.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.StrUtil;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.pojo.AnomalyResult;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.AnomalyDetectQueryParam;
import com.nju.apcd.pojo.param.AnomalyResultQueryParam;
import com.nju.apcd.service.AnomalyDetectService;
import com.nju.apcd.utils.ScriptUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnomalyDetectServiceImpl implements AnomalyDetectService {
    public ServerResponse anomalyDetect(AnomalyDetectQueryParam param){
        String project = param.getProject();
        String[] sceneList = param.getScenes().split(",");
        String[] dateList = param.getDateRange().split(",");
        // 是否需要重新计算特征
        if(param.getFeatureFlag() == true){
            for(String scene: sceneList){
                if(scene.equals(Constants.COMMITTER)){
                    ScriptUtil.committer_feature_extract(project, dateList[0], dateList[1]);
                }else if(scene.equals(Constants.MAINTAINER)){
                    ScriptUtil.maintainer_feature_extract(project, dateList[0], dateList[1]);
                }else if(scene.equals(Constants.REVIEWER)){
                    ScriptUtil.reviewer_feature_extract(project, dateList[0], dateList[1]);
                }
            }
        }else{
            // 判断特征文件是否生成完成
            for(String scene: sceneList){
                String fileName = project + "_" + scene + "_feature.csv";
                String filePath = Constants.DATA_DIR + "/" + fileName;
                if(!FileUtil.exist(filePath)){
                    // TODO 目前用户勾选的场景只要有一个没有找到相关的特征文件，就不做异常检测
                    return ServerResponse.fail("没有找到特征文件" + fileName + ", 请重新生成");
                }
            }
        }
        // 执行异常检测
        ScriptUtil.anomaly_detection(project, param.getScenes(), param.getAlgorithms());
        // TODO 投票(暂时没用上Algorithms参数)
        ScriptUtil.multi_model_vote(project, param.getScenes(), param.getAlgorithms());
        return ServerResponse.ok("异常检测执行完成");
    }

    @Override
    public ServerResponse getAnomalyResult(AnomalyResultQueryParam param) {
        String project = param.getProject();
        String scene = param.getScene();
        String resultType = param.getResultType();
        //获取投票数据
        String filePath = Constants.DATA_DIR + "/anomaly_detection/multi_model_vote/" + project + "_" + scene + "_multi_model_vote.csv";
        if(!FileUtil.exist(filePath)){
            return ServerResponse.fail(FileUtil.file(filePath).getName() + "文件不存在");
        }
        CsvReader reader = CsvUtil.getReader();
        reader.setContainsHeader(true);
        CsvData data = reader.read(FileUtil.file(filePath));
        //筛选投票数据
        List<CsvRow> rows = data.getRows();
        if(StrUtil.isNotBlank(resultType)){
            rows = data.getRows().stream().filter(row -> row.getByName("vote").equals(resultType)).collect(Collectors.toList());
        }
        //获取各属性的上下四分位数据
        List<CsvRow> quartiles = getQuartiles(project, scene);
        //组装结果
        AnomalyResult result = new AnomalyResult(
                project,
                scene,
                data.getHeader(),
                rows,
                quartiles);
        return ServerResponse.ok(result);
    }

    public List<CsvRow> getQuartiles(String project, String scene){
        CsvReader reader = CsvUtil.getReader();
        reader.setContainsHeader(true);
        String filePath = Constants.DATA_DIR + "/anomaly_detection/box_plot/" + project + "_" + scene + "_box_plot.csv";
        if(!FileUtil.exist(filePath)){
            return null;
        }
        CsvData data = reader.read(FileUtil.file(filePath));
        return data.getRows();
    }
}
