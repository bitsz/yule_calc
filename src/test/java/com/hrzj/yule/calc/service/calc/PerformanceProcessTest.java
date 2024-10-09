package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.config.enums.FoodBigType;
import com.hrzj.yule.calc.mapper.mysql.CashItemMapper;
import com.hrzj.yule.calc.mapper.mysql.CashMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.pojo.entity.mysql.CashItem;
import com.hrzj.yule.calc.pojo.po.MoneyRule;
import com.hrzj.yule.calc.pojo.vo.CashReport;
import com.hrzj.yule.calc.service.ImportService;
import com.hrzj.yule.calc.service.read.ReadCash;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.DOT;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-24 16:07
 */
public class PerformanceProcessTest extends BaseTestClass {

  private static final Logger log = LoggerFactory.getLogger(PerformanceProcessTest.class);

  @Autowired
  private CashItemMapper cashItemMapper;

  @Autowired
  private CashMapper cashMapper;

  @Autowired
  private ReadCash readCash;


  @Test
  public void testRoyalty() {
    String name = "乔洋.乔洋";
    Long id = 0000543L;
    String selectedFilePath = "report\\账单统计表【2024-04-01至2024-04-30】.xlsx";
    String cashId = null;
    String businessDayStar = "2024-04-01";
    String businessDayEnd = "2024-04-30";
    MoneyRule moneyRule = null;
    //读取规则
    List<MoneyRule> moneyRules = ImportService.obtainExcelData();

    //验证打印金额(实际支付金额)是否有录入
    List<CashReport> cashReportList = readCash.verifyPrintMoney(selectedFilePath);
    Map<ReadCash.group, List<CashReport>> allGroupData = readCash.group(cashReportList);
    Map<Long, BigDecimal> noRewardCash = readCash.getCashIdByGroup(allGroupData, ReadCash.group.noRewardCash);
    Map<Long, BigDecimal> rewardCash = readCash.getCashIdByGroup(allGroupData, ReadCash.group.rewardOther);
    Map<Long, BigDecimal> proportionRewardCash = readCash.getCashIdByGroup(allGroupData,
      ReadCash.group.rewardActive);
    Map<Long, BigDecimal> commissionCash = readCash.getCashIdByGroup(allGroupData, ReadCash.group.commissionCash);

    FoodCalcUtil foodCalcUtil = new FoodCalcUtil(cashMapper, cashItemMapper, businessDayStar, businessDayEnd, cashId,
      moneyRules, noRewardCash, rewardCash, proportionRewardCash, commissionCash);
    for (MoneyRule rule : moneyRules) {
      if (rule.getTeamNameWithName().equals(name) || rule.getId().equals(id)) {
        moneyRule = rule;
        break;
      }
    }
    String groupName = moneyRule.getTeamName();
    String empGroup = groupName.contains(DOT) ? groupName : groupName.concat(DOT);
    //同组的所有收款单据
    List<Cash> empCash = null;
    if (groupName.equals("商务")) {
      empCash = cashMapper.cashByEmpGroup(moneyRule.getTeamNameWithName(), moneyRule.getTeamNameWithName(),
        businessDayStar,
        businessDayEnd);
    } else {
      empCash = cashMapper.cashByEmpGroup(empGroup, name,
        businessDayStar,
        businessDayEnd);
    }


    if (CollectionUtils.isEmpty(empCash)) {
      return;
    }

    BigDecimal total = empCash.stream().map(e -> e.getFixMoney()).reduce(BigDecimal::add).get();

    List<Long> cashIdList = empCash.stream().map(Cash::getId).collect(Collectors.toList());
    //销售商品
    List<CashItem> allCashItems = this.cashItemMapper.cashItemsByEmpGroup(empGroup, cashIdList);
    for (CashItem item : allCashItems) {
      log.info("---------物品({})----------- {} {} {} {}", item.getBigTypeName(), item.getCId(), item.getFoodName(),
        item.getFoodTypeName(),
        item.getMoney(),
        item.getQuantity(),
        item.getFPrice());
    }

    BigDecimal gx = FoodCalcUtil.calc(FoodCalcUtil.calc(allCashItems, FoodBigType.gentai, FoodBigType.xiaochi));
    BigDecimal no = FoodCalcUtil.calc(FoodCalcUtil.calc(allCashItems, FoodBigType.NO));
    BigDecimal yangjiu = FoodCalcUtil.calc(FoodCalcUtil.calc(allCashItems, FoodBigType.yangjiu));
    BigDecimal piujiu = FoodCalcUtil.calc(FoodCalcUtil.calc(allCashItems, FoodBigType.pijiu));
    BigDecimal hongjiu = FoodCalcUtil.calc(FoodCalcUtil.calc(allCashItems, FoodBigType.hongjiu));
    BigDecimal jiushui = FoodCalcUtil.calc(FoodCalcUtil.calc(allCashItems, FoodBigType.jiushui));

    for (CashItem item : FoodCalcUtil.calc(allCashItems, FoodBigType.jiushui)) {
      log.info("---------酒水(其它)----------- {} {} {} {}", item.getCId(), item.getFoodName(), item.getFoodTypeName(),
        item.getMoney(),
        item.getQuantity(),
        item.getFPrice());
    }

    log.info("----------跟台小吃{}", gx);
    log.info("-------无提成无业绩{}", no);
    log.info("----------啤酒红酒{}", piujiu.add(hongjiu));
    log.info("-------------洋酒{}", yangjiu);
    log.info("-------------酒水{}", jiushui);

    log.info("合计{}/{}", gx.add(hongjiu).add(piujiu).add(yangjiu).add(no).add(jiushui), total);
    log.info("差{}", total.subtract(gx.add(hongjiu).add(piujiu).add(yangjiu).add(no).add(jiushui)));

//    if (null!=moneyRule){
//      Report report = foodCalcUtil.calc(moneyRule, Boolean.TRUE);
//      if (null!=report){
//        log.info(report.toString());
//      }
//    }


  }
}
