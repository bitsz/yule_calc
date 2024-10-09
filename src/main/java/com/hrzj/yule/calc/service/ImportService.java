package com.hrzj.yule.calc.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.hrzj.yule.calc.pojo.po.CompanyActivityRule;
import com.hrzj.yule.calc.pojo.po.Floor3_4;
import com.hrzj.yule.calc.pojo.po.Floor48;
import com.hrzj.yule.calc.pojo.po.ImportedRule;
import com.hrzj.yule.calc.pojo.po.MoneyRule;
import com.hrzj.yule.calc.pojo.po.OtherRule;
import com.hrzj.yule.calc.pojo.po.Pay3_4_100;
import com.hrzj.yule.calc.pojo.po.Pay3_4_95;
import com.hrzj.yule.calc.pojo.po.Pay48_100;
import com.hrzj.yule.calc.pojo.po.Pay48_95;
import com.hrzj.yule.calc.pojo.po.RoyaltyDetail;
import com.hrzj.yule.calc.pojo.po.RuleAllField;
import com.hrzj.yule.calc.pojo.po.WineAndBeerRule;
import com.hrzj.yule.calc.util.excel.ExcelMergeHelper;
import com.hrzj.yule.calc.util.excel.FieldEasyExcelListener;

import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/18 星期四 15:49
 */
@Service
@Slf4j
public class ImportService {

  public static final String line = "\\|";

  //最新规则数据
  private static final String read_file = "template\\维也纳待遇表(最新员工).xlsx";

  public static List<MoneyRule> obtainExcelData() {
    BufferedInputStream inputStream = FileUtil.getInputStream(new File(read_file));
    Integer sheetNo = 0;
    Integer headRowNumber = 6;

    FieldEasyExcelListener easyExcelListener = new FieldEasyExcelListener(headRowNumber);
    ExcelReaderBuilder read = EasyExcel.read(inputStream, RuleAllField.class, easyExcelListener).autoCloseStream(true);
    read.extraRead(CellExtraTypeEnum.MERGE)
      .sheet(sheetNo)
      .headRowNumber(headRowNumber)
      .doRead();
    List<CellExtra> extraMergeInfoList = easyExcelListener.getExtraMergeInfoList();
    List<RuleAllField> data = new ExcelMergeHelper().explainMergeData(easyExcelListener.getData(), extraMergeInfoList,
      headRowNumber);

    Map<String, List<RuleAllField>> collect =
      data.stream().filter(user -> null != user.getId()).collect(Collectors.groupingBy(RuleAllField::getId));
    Iterator<String> iterator = collect.keySet().iterator();
    List<MoneyRule> moneyRule = new ArrayList<MoneyRule>();
    while (iterator.hasNext()) {
      List<RuleAllField> userExcelDtos = collect.get(iterator.next());
      List<OtherRule> otherRules = new ArrayList<>();
      List<ImportedRule> importedRules = new ArrayList<>();
      MoneyRule rule = new MoneyRule();
      RoyaltyDetail royaltyDetail = new RoyaltyDetail();
      for (RuleAllField userExcelDto : userExcelDtos) {
        String nameWithGroup = userExcelDto.getNameWithGroup();
        BigDecimal commonPoint = userExcelDto.getCommonPoint();
        BigDecimal wineAndBeerBonusPoint = userExcelDto.getWineAndBeerBonusPoint();
        String snack = userExcelDto.getCommonSnack();
        BigDecimal snackPoint = userExcelDto.getCommonSnackPoint();
        if (null != nameWithGroup) {
          String[] name = nameWithGroup.split(line);
          String teamNameWithName = name[1];
          rule.setTeamNameWithName(teamNameWithName);
          //员工id
          Long empId = Long.valueOf(name[0]);
          String[] teamNameWhitName = teamNameWithName.split("\\.");
          //团队组名
          rule.setTeamName(teamNameWhitName[0]);
          int length = teamNameWhitName.length;
          if (length >= 2) {
            //员工名字
            rule.setName(teamNameWhitName[1]);
          } else {
            //员工名字
            rule.setName(teamNameWhitName[0]);
          }

          rule.setId(empId);
        }
        if (null != commonPoint) {
          royaltyDetail.setCommonPoint(commonPoint);
        }
        royaltyDetail.setCommonFilter(userExcelDto.getCommonFilter());
        royaltyDetail.setCommon6000(userExcelDto.getCommon6000Point());
        if (null != wineAndBeerBonusPoint) {
          WineAndBeerRule wineAndBeerRule = new WineAndBeerRule(wineAndBeerBonusPoint,
            userExcelDto.getThisMonthHandOutRedBeer());
          royaltyDetail.setWineAndBeerRule(wineAndBeerRule);
        }
        if (null != snack) {
          royaltyDetail.setSnack(snack);
        }
        if (null != snackPoint) {
          royaltyDetail.setSnackPoint(snackPoint);
        }

        String otherRuleMethod = userExcelDto.getOtherRuleMethod();
        if (null != otherRuleMethod) {
          OtherRule otherRule = new OtherRule(userExcelDto.getMergeOtherGroup(), userExcelDto.getOtherRuleMethod(),
            userExcelDto.getOtherRulePoint(),
            userExcelDto.getOtherRuleCondition(),
            userExcelDto.getThisMonthHandOutOther());
          otherRules.add(otherRule);
        }
        String importedRuleMethod = userExcelDto.getImportedRuleMethod();
        if (null != importedRuleMethod) {
          ImportedRule importedRule = new ImportedRule(userExcelDto.getImportedRuleMethod(),
            userExcelDto.getImportedRulePoint(),
            userExcelDto.getImportedRuleCondition(), userExcelDto.getImportedRuleFilter(),
            userExcelDto.getThisMonthHandOutImport());

          importedRules.add(importedRule);
        }
        CompanyActivityRule companyActivityRule = new CompanyActivityRule();
        Floor3_4 floor3_4 = new Floor3_4();
        Floor48 floor48 = new Floor48();
        String companyActivityRuleFloor34Pay95Method = userExcelDto.getCompanyActivityRuleFloor3_4Pay_95Method();
        String companyActivityRuleFloor34Pay100Method = userExcelDto.getCompanyActivityRuleFloor3_4Pay_100Method();
        if (null != companyActivityRuleFloor34Pay95Method) {
          Pay3_4_95 pay95s = new Pay3_4_95();
          BigDecimal companyActivityRuleFloor34Pay95Point = userExcelDto.getCompanyActivityRuleFloor3_4Pay_95Point();
          pay95s.setMethod(companyActivityRuleFloor34Pay100Method);
          pay95s.setPoint(companyActivityRuleFloor34Pay95Point);
          pay95s.setSnack(userExcelDto.getCompanyActivityRuleFloor3_4Pay_95Snack());
          pay95s.setFilter(userExcelDto.getCompanyActivityRuleFloor3_4Pay_95Filter());
          floor3_4.setPay95s(pay95s);
        }
        if (null != companyActivityRuleFloor34Pay100Method) {
          Pay3_4_100 pay100 = new Pay3_4_100();
          BigDecimal companyActivityRuleFloor34Pay100Point = userExcelDto.getCompanyActivityRuleFloor3_4Pay_100Point();
          pay100.setMethod(companyActivityRuleFloor34Pay100Method);
          pay100.setPoint(companyActivityRuleFloor34Pay100Point);
          pay100.setSnack(userExcelDto.getCompanyActivityRuleFloor3_4Pay_100Snack());
          pay100.setFilter(userExcelDto.getCompanyActivityRuleFloor3_4Pay_100Filter());
          floor3_4.setPay100s(pay100);
        }

        Pay48_95 pay48_95 = new Pay48_95();
        String companyActivityRuleFloor48Pay95Method = userExcelDto.getCompanyActivityRuleFloor48Pay_95Method();
        if (null != companyActivityRuleFloor48Pay95Method) {
          pay48_95.setMethod(companyActivityRuleFloor48Pay95Method);
          pay48_95.setPoint(userExcelDto.getCompanyActivityRuleFloor48Pay_95Point());
        }

        Pay48_100 pay48_100 = new Pay48_100();
        String companyActivityRuleFloor48Pay100Method = userExcelDto.getCompanyActivityRuleFloor48Pay_100Method();
        if (null != companyActivityRuleFloor48Pay100Method) {
          pay48_100.setMethod(companyActivityRuleFloor48Pay100Method);
          pay48_100.setPoint(userExcelDto.getCompanyActivityRuleFloor48Pay_100Point());
          pay48_100.setSnack(userExcelDto.getCompanyActivityRuleFloor48Pay_100Snack());
          pay48_100.setFilter(userExcelDto.getCompanyActivityRuleFilter());
        }

        floor48.setPay48_95(pay48_95);
        floor48.setPay48_100(pay48_100);
        royaltyDetail.setOtherRule(otherRules);
        royaltyDetail.setImportedRule(importedRules);
        companyActivityRule.setFloor3_4(floor3_4);
        companyActivityRule.setFloor48(floor48);
        royaltyDetail.setCompanyActivityRule(companyActivityRule);
        rule.setRoyaltyDetail(royaltyDetail);
        if (null != nameWithGroup) {
          moneyRule.add(rule);
        }
      }
      moneyRule = moneyRule.stream().distinct().collect(Collectors.toList());
    }
    for (MoneyRule rule : moneyRule) {

      String name = rule.getName();
      log.info("------------------{}---------------------", name);
      RoyaltyDetail royaltyDetail = rule.getRoyaltyDetail();
      log.info("通提提成点:" + royaltyDetail.getCommonPoint());
      log.info("红酒、啤酒额外奖励:" + royaltyDetail.getWineAndBeerRule());
      log.info("是否包含跟台、小吃:" + royaltyDetail.getSnack());
      List<OtherRule> otherRule = royaltyDetail.getOtherRule();
      log.info("其他额外奖励:" + otherRule);
      List<ImportedRule> importedRule = royaltyDetail.getImportedRule();
      log.info("洋酒奖励:" + JSONUtil.toJsonPrettyStr(importedRule));
      CompanyActivityRule companyActivityRule = royaltyDetail.getCompanyActivityRule();
      Floor3_4 floor34 = companyActivityRule.getFloor3_4();
      Pay3_4_95 pay95s = floor34.getPay95s();
      log.info("公司酒水活动奖励 3-4楼 95折买单:" + pay95s);
      Pay3_4_100 pay100s = floor34.getPay100s();
      log.info("公司酒水活动奖励 3-4楼 全单买单:" + pay100s);
      Floor48 floor48 = companyActivityRule.getFloor48();
      Pay48_95 pay4895 = floor48.getPay48_95();
      log.info("公司酒水活动奖励 48楼 95折及以上:" + pay4895);
      log.info("------------------{}---------------------\n", name);
    }
    return moneyRule;
  }
}
