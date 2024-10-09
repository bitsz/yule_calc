package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.config.enums.FoodBigType;
import com.hrzj.yule.calc.config.enums.Method;
import com.hrzj.yule.calc.config.enums.SalesType;
import com.hrzj.yule.calc.config.enums.YesOrNoEnums;
import com.hrzj.yule.calc.fx.query.Loading;
import com.hrzj.yule.calc.mapper.mysql.CashItemMapper;
import com.hrzj.yule.calc.mapper.mysql.CashMapper;
import com.hrzj.yule.calc.mapper.mysql.SalesEmployeeMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.pojo.entity.mysql.CashEmployee;
import com.hrzj.yule.calc.pojo.entity.mysql.CashItem;
import com.hrzj.yule.calc.pojo.po.MoneyRule;
import com.hrzj.yule.calc.pojo.po.OtherRule;
import com.hrzj.yule.calc.pojo.po.RuleDJAllField;
import com.hrzj.yule.calc.pojo.vo.CashReport;
import com.hrzj.yule.calc.pojo.vo.Report;
import com.hrzj.yule.calc.pojo.vo.ReportDJ;
import com.hrzj.yule.calc.service.ImportService;
import com.hrzj.yule.calc.service.ReadDjRuleService;
import com.hrzj.yule.calc.service.export.ExportReport;
import com.hrzj.yule.calc.service.read.ReadCash;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 业绩计算
 * @date 2024-04-29 14:48
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Performance {


  private final CashMapper cashMapper;

  private final CashItemMapper cashItemMapper;

  private final SalesEmployeeMapper salesEmployeeMapper;

  private final ReadCash readCash;


  /**
   * @Author lifengquan
   * @Description 计算提成
   * @Date 15:29 2024-04-30
   **/
  public String royalty(Loading loading, String selectedFilePath, String cashId, String businessDayStar,
                        String businessDayEnd) {
    //验证打印金额(实际支付金额)是否有录入
    List<CashReport> cashReportList = readCash.verifyPrintMoney(selectedFilePath);
    Map<ReadCash.group, List<CashReport>> allGroupData = readCash.group(cashReportList);
    Map<Long, BigDecimal> noRewardCash = readCash.getCashIdByGroup(allGroupData, ReadCash.group.noRewardCash);
    Map<Long, BigDecimal> rewardCash = readCash.getCashIdByGroup(allGroupData, ReadCash.group.rewardOther);
    Map<Long, BigDecimal> proportionRewardCash = readCash.getCashIdByGroup(allGroupData,
      ReadCash.group.rewardActive);
    Map<Long, BigDecimal> commissionCash = readCash.getCashIdByGroup(allGroupData, ReadCash.group.commissionCash);


    String fileName = "维也纳业绩报表";
    List<Report> dataList = new ArrayList<>();
    boolean isFilter = true;
    if (null == businessDayEnd || null == businessDayStar) {
      isFilter = false;
    } else {
      fileName = String.format("%s【%s至%s】", fileName, businessDayStar, businessDayEnd);
    }
    //读取规则
    List<MoneyRule> moneyRules = ImportService.obtainExcelData();
    FoodCalcUtil foodCalcUtil = new FoodCalcUtil(cashMapper, cashItemMapper, businessDayStar, businessDayEnd, cashId,
      moneyRules, noRewardCash, rewardCash, proportionRewardCash, commissionCash);
    for (MoneyRule moneyRule : moneyRules) {
      loading.showMessage(moneyRule.getName());
      //商务部业绩
      Report report = foodCalcUtil.calc(moneyRule, isFilter);
      if (null != report) {
        dataList.add(report);
      }
    }

    //以下是DJ业绩
    List<ReportDJ> djData = new ArrayList<>();
    List<RuleDJAllField> ruleDJAllFields = ReadDjRuleService.obtainExcelData();
    List<CashEmployee> dj = salesEmployeeMapper.getDJ();
    for (CashEmployee cashEmployee : dj) {
      String empName = cashEmployee.getEmpName();
      loading.showMessage(empName);
      SalesEmployeeDJ salesEmployee = new SalesEmployeeDJ(empName);

      Long id = cashEmployee.getId();
      //员工的所有收款单据
      List<Cash> empCash = cashMapper.cashByEmpId(cashId, id, businessDayStar, businessDayEnd);
      List<Long> cashIdList = empCash.stream().map(Cash::getId).collect(Collectors.toList());
      ;
      if (CollectionUtils.isEmpty(cashIdList)) {
        djData.add(ReportDJ.builder().name(empName).build());
        continue;
      }
      //销售商品
      List<CashItem> allCashItems = this.cashItemMapper.cashItemsByEmpId(id, cashIdList);
      BigDecimal sales = FoodCalcUtil.calc(allCashItems);

      BigDecimal finalSales = sales;
      RuleDJAllField common =
        ruleDJAllFields.stream().filter(rule -> null != rule.getDjCommonCondition() && rule.getDjCommonCondition().multiply(new BigDecimal(
            "10000")).compareTo(finalSales) < 0).collect(Collectors.toList())
          .stream().max(Comparator.comparing(RuleDJAllField::getDjCommonCondition)).orElse(null);

      RuleDJAllField award =
        ruleDJAllFields.stream().filter(rule -> null != rule.getDjAwardCondition() && rule.getDjAwardCondition().multiply(new BigDecimal(
            "10000")).compareTo(finalSales) < 0).collect(Collectors.toList())
          .stream().max(Comparator.comparing(RuleDJAllField::getDjAwardCondition)).orElse(null);


      if (null != common) {
        //通提是否包含跟台、小吃
        String snack = common.getDjCommonSnack();

        BigDecimal snackSale;
        YesOrNoEnums snackEnums = YesOrNoEnums.get(snack);
        if (snackEnums.equals(YesOrNoEnums.no)) {
          List<CashItem> items = FoodCalcUtil.calc(allCashItems, FoodBigType.gentai, FoodBigType.xiaochi);
          snackSale = FoodCalcUtil.calc(items);
          SalesItem snackCalc = new SalesItem(SalesType.gentai,
            new PercentageCommissionStrategy(common.getDjCommonSnackPoint()));
          salesEmployee.addSalesItem(snackCalc, snackSale);
          sales = sales.subtract(null == snackSale ? BigDecimal.ZERO : snackSale);
        }

        //通提提成点
        BigDecimal commonPoint = common.getDjCommonPoint();
        if (null != commonPoint) {
          SalesItem commonWine = new SalesItem(SalesType.common,
            new PercentageCommissionStrategy(commonPoint));
          salesEmployee.addSalesItem(commonWine, sales);
        }
      }

      if (null != award) {
        OtherRule otherRule = new OtherRule();
        otherRule.setPoint(award.getDjAward());
        FoodCalcUtil.other(salesEmployee, otherRule, Method.FIXED, sales);
      }
      // 计算提成
      ReportDJ report = salesEmployee.calculateTotalCommission();
      report.setName(empName);
      report.setCommonPoint(common.getDjCommonPoint());
      report.setSnackPoint(common.getDjCommonSnackPoint());
      if (null != report.getOtherMoney()) {
        report.setThisMonthHandOutOther(award.getDjAwardIsHandOut());
      }
      report.setTotalSale(sales);
      djData.add(report);
    }
    //ExportReportDj.export(djData, fileName.replace("维也纳商务业绩报表", "维也纳DJ绩报表"));
    loading.showMessage("文件合并");
    return ExportReport.export(dataList, djData, fileName);
  }
}
