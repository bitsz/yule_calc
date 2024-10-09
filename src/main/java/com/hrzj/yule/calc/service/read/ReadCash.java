package com.hrzj.yule.calc.service.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.hrzj.yule.calc.config.exception.CommonException;
import com.hrzj.yule.calc.mapper.mysql.CashMapper;
import com.hrzj.yule.calc.mapper.mysql.FixCashMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.FixCash;
import com.hrzj.yule.calc.pojo.vo.CashReport;
import com.hrzj.yule.calc.repository.mysql.FixCashRepository;
import com.hrzj.yule.calc.service.ReadCashService;
import com.hrzj.yule.calc.util.BeanCopyTool;
import com.hrzj.yule.calc.util.excel.ExcelMergeHelper;
import com.hrzj.yule.calc.util.excel.FieldEasyExcelListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.hrzj.yule.calc.pojo.vo.CashReport.depts;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-20 9:46
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ReadCash implements ReadCashService {

  private final CashMapper cashMapper;
  private final FixCashRepository fixCashRepository;
  private final FixCashMapper fixCashMapper;

//  @Resource(name = "mysqlEntityManagerFactory")
//  private EntityManagerFactory mysqlEntityManagerFactory;

  @Override
  @Transactional(value = "mysqlTransactionManager")
  public void readAndUpdateCash(String path) {
    boolean exist = Files.exists(Paths.get(path));
    if (!exist) {
      return;
    }

    Integer sheetNo = 0;
    Integer headRowNumber = 1;

    //读取excel文件内容更新实际支付金额
    BufferedInputStream inputStream = null;
    try {
      inputStream = FileUtil.getInputStream(new File(path));
    } catch (Exception e) {
      e.printStackTrace();
      log.error("文件不存在 {}", e.getMessage());
      throw new CommonException(path + " 文件不存在");
    }

    FieldEasyExcelListener<CashReport> excelListener = new FieldEasyExcelListener(headRowNumber);

    ExcelReaderBuilder read = EasyExcel.read(inputStream, CashReport.class, excelListener).autoCloseStream(true);
    read.extraRead(CellExtraTypeEnum.MERGE)
      .sheet(sheetNo)
      .headRowNumber(headRowNumber)
      .doRead();

    List<CellExtra> extraMergeInfoList = excelListener.getExtraMergeInfoList();
    List<CashReport> datas = new ExcelMergeHelper().explainMergeData(excelListener.getData(), extraMergeInfoList,
      headRowNumber);
    List<FixCash> fixCashes = new ArrayList<>();
    for (CashReport data : datas) {
      cashMapper.fixMoney(data.getFixMoneyCard(), data.getFixMoneyCash(), data.getTipMoney(), data.getHandlingFee(),
        data.getNoReward(),
        data.getRewardOther(),
        data.getRewardActive(),
        data.getCommission(),
        data.getRemark(),
        depts[3].equals(data.getDept()) ? "是" : "否",
        data.getCashId());
      FixCash fixCash = BeanCopyTool.convertCopyBean(data, FixCash.class);
      fixCashes.add(fixCash);
    }
    List<List<FixCash>> split = CollUtil.split(fixCashes, 20);
    for (List<FixCash> cashes : split) {
      fixCashMapper.deleteFixCash(cashes.stream().map(FixCash::getCashId).collect(Collectors.toList()));
      fixCashMapper.batchInsert(cashes);
    }
  }

  @Override
  @Transactional(value = "mysqlJpaTransactionManager")
  public void updateFixCash(List<FixCash> fixCashes) {
    fixCashRepository.saveAll(fixCashes);
  }

  public List<CashReport> verifyPrintMoney(String selectedFilePath) {
    log.info("验证文件完整性{}", selectedFilePath);
    Integer sheetNo = 0;
    Integer headRowNumber = 1;

    //读取excel文件内容更新实际支付金额
    BufferedInputStream inputStream = FileUtil.getInputStream(new File(selectedFilePath));
    FieldEasyExcelListener<CashReport> excelListener = new FieldEasyExcelListener(headRowNumber);

    ExcelReaderBuilder read = EasyExcel.read(inputStream, CashReport.class, excelListener).autoCloseStream(true);
    read.extraRead(CellExtraTypeEnum.MERGE)
      .sheet(sheetNo)
      .headRowNumber(headRowNumber)
      .doRead();

    List<CellExtra> extraMergeInfoList = excelListener.getExtraMergeInfoList();
    List<CashReport> datas = new ExcelMergeHelper().explainMergeData(excelListener.getData(), extraMergeInfoList,
      headRowNumber);
    for (CashReport data : datas) {
      if (null == data.getFixMoneyCash() || null == data.getFixMoneyCard()) {
        log.info("账单{},实收不能为空,请补充.", data.getCashId());
        throw new CommonException("实收不能为空,请补充");
      }
    }
    return datas;
  }

  public Map<ReadCash.group, List<CashReport>> group(List<CashReport> datas) {
    Map<ReadCash.group, List<CashReport>> map = new ConcurrentHashMap();
    //无奖励账单
    List<CashReport> noRewardCash =
      datas.stream().filter(cashReport -> StringUtils.isNotBlank(cashReport.getNoReward())).collect(Collectors.toList());
    map.put(group.noRewardCash, noRewardCash);

    //其它额度奖励账单
    List<CashReport> rewardCash =
      datas.stream().filter(cashReport -> null != cashReport.getRewardOther()).collect(Collectors.toList());
    map.put(group.rewardOther, rewardCash);

    //活动奖励账单
    List<CashReport> proportionRewardCash =
      datas.stream().filter(cashReport -> null != cashReport.getRewardActive()).collect(Collectors.toList());
    map.put(group.rewardActive, proportionRewardCash);

    //通提
    List<CashReport> commissionCash =
      datas.stream().filter(cashReport -> null != cashReport.getCommission()).collect(Collectors.toList());
    map.put(group.commissionCash, commissionCash);
    return map;
  }

  public Map<Long, BigDecimal> getCashIdByGroup(Map<ReadCash.group, List<CashReport>> map, ReadCash.group g) {
    Map<Long, BigDecimal> result = new ConcurrentHashMap<>();
    List<CashReport> cashReportList = map.get(g);
    switch (g) {
      case noRewardCash:
        result = cashReportList.stream().collect(Collectors.toMap(CashReport::getCashId, c -> BigDecimal.ZERO));
        break;
      case rewardOther:
        result = cashReportList.stream().collect(Collectors.toMap(CashReport::getCashId,
          CashReport::getRewardOther));
        break;
      case rewardActive:
        result = cashReportList.stream().collect(Collectors.toMap(CashReport::getCashId,
          CashReport::getRewardActive));
        break;
      case commissionCash:
        result = cashReportList.stream().collect(Collectors.toMap(CashReport::getCashId,
          CashReport::getCommission));
        break;

    }

    return result;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  public enum group {
    noRewardCash("无奖励账单"),
    rewardOther("其它励账单"),
    rewardActive("活动奖励账单"),
    commissionCash("通提"),

    ;

    private String desc;
  }


}
