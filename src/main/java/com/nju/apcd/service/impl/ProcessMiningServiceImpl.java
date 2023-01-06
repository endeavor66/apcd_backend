package com.nju.apcd.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.mapper.ProcessModelMapper;
import com.nju.apcd.pojo.AnomalyResult;
import com.nju.apcd.pojo.ConformanceResult;
import com.nju.apcd.pojo.ProcessModel;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.ProcessModelQueryParam;
import com.nju.apcd.service.ProcessMiningService;
import com.nju.apcd.utils.ScriptUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessMiningServiceImpl implements ProcessMiningService {

    @Resource
    ProcessModelMapper processModelMapper;

    @Override
    public ServerResponse processDiscovery(ProcessModelQueryParam param) {
        // TODO 解析参数param,校验是否符合要求
        ScriptUtil.process_discovery(
                param.getProjectList(),
                param.getSceneList(),
                param.getAlgorithm(),
                param.getParam());
        return ServerResponse.ok("主流模式构建完成");
    }

    @Override
    public ServerResponse getProcessModel(String[] sceneList, String display) {
        List<ProcessModel> modelList = new ArrayList<>();
        for(String scene: sceneList){
            ProcessModel model = getProcessModel(scene, display);
            if(null == model){
                return ServerResponse.fail("没有找到相应的过程模型");
            }
            modelList.add(model);
        }
        return ServerResponse.ok(modelList);
    }

    public ProcessModel getProcessModel(String scene, String display){
        // 根据display选择不同的过程模型
        String path = null;
        if(display.equals("PetriNet")){
            path =  Constants.DATA_DIR + "/process_model/petri_net/" + scene + "_petri_net.png";
        }else if(display.equals("HeuristicsNet")){
            path =  Constants.DATA_DIR + "/process_model/heuristics_net/" + scene + "_heuristics_net.png";
        }else if(display.equals("BPMN")){
            path =  Constants.DATA_DIR + "/process_model/bpmn/" + scene + "_bpmn.png";
        }
        // 判断display是否合规 || 服务器是否保存有该display模型
        if(StrUtil.isBlank(path)){
            return null;
        }else if(!FileUtil.exist(path)){
            return null;
        }
        // 包装过程模型的信息
        String imgData = "data:image/png;base64," + Base64Utils.encodeToString(FileUtil.readBytes(path));
        ProcessModel model = processModelMapper.selectOne(new QueryWrapper<ProcessModel>().eq("scene", scene));
        model.setImgData(imgData);
        return model;
    }

    @Override
    public ServerResponse conformanceCheck(String project, String algorithm) {
        ScriptUtil.conformance_checking(project, algorithm);
        // 判断一致性检验是否正常执行(是否生成了结果文件)
        String filePath = "";
        if(algorithm.equals("alignments")){
            filePath = Constants.DATA_DIR + "/conformance_check/alignments/" + project + "_alignments.csv";
        }else if(algorithm.equals("token-based-replay")){
            filePath = Constants.DATA_DIR + "/conformance_check/tbr/" + project + "_tbr.csv";
        }
        if(FileUtil.exist(filePath)){
            return ServerResponse.ok("一致性检验执行完成", null);
        }else{
            return ServerResponse.fail("一致性检验执行失败");
        }
    }

    @Override
    public ServerResponse getConformanceCheckResult(String project, String algorithm) {
        // 判断结果文件是否存在
        String filePath = "";
        String[] header = null;
        if(algorithm.equals("alignments")){
            header = Constants.ALIGNMENTS_HEADER;
            filePath = Constants.DATA_DIR + "/conformance_check/alignments/" + project + "_alignments.csv";
        }else if(algorithm.equals("token-based-replay")){
            header = Constants.TBR_HEADER;
            filePath = Constants.DATA_DIR + "/conformance_check/tbr/" + project + "_tbr.csv";
        }
        if(!FileUtil.exist(filePath)){
            return ServerResponse.fail("一致性检验结果文件不存在");
        }
        //读取文件
        CsvReader reader = CsvUtil.getReader();
        reader.setContainsHeader(true);
        //筛选数据
        CsvData totalData = reader.read(FileUtil.file(filePath));
        List<List<String>> data = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        for (CsvRow row : totalData) {
            // 每一行只筛选出特定列的数据
            List<String> record = new ArrayList<>();
            for(String col: header){
                String v = row.getByName(col);
                //对于浮点数(fitness或trace_fitness),保留两位小数
                if(col.equals("fitness") || col.equals("trace_fitness")){
                    String formatFloat = df.format(Double.parseDouble(v));
                    record.add(formatFloat);
                }else{
                    record.add(v);
                }
            }
            data.add(record);
        }
        //结果封装
        ConformanceResult result = new ConformanceResult(project, algorithm, header, data);
        return ServerResponse.ok(result);
    }
}
