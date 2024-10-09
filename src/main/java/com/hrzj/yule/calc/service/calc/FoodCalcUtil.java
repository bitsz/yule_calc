package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.config.enums.Floor;
import com.hrzj.yule.calc.config.enums.FoodBigType;
import com.hrzj.yule.calc.config.enums.FoodSmallType;
import com.hrzj.yule.calc.config.enums.GoodsFilter;
import com.hrzj.yule.calc.config.enums.Method;
import com.hrzj.yule.calc.config.enums.RoomType;
import com.hrzj.yule.calc.config.enums.SalesType;
import com.hrzj.yule.calc.config.enums.YesOrNoEnums;
import com.hrzj.yule.calc.mapper.mysql.CashItemMapper;
import com.hrzj.yule.calc.mapper.mysql.CashMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.pojo.entity.mysql.CashItem;
import com.hrzj.yule.calc.pojo.po.ImportedRule;
import com.hrzj.yule.calc.pojo.po.MoneyRule;
import com.hrzj.yule.calc.pojo.po.OtherRule;
import com.hrzj.yule.calc.pojo.po.RoyaltyDetail;
import com.hrzj.yule.calc.pojo.po.WineAndBeerRule;
import com.hrzj.yule.calc.pojo.vo.Report;
import com.hrzj.yule.calc.util.ExtractNumbers;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.StrPool.DOT;
import static cn.hutool.core.text.StrPool.LF;
import static com.hrzj.yule.calc.config.enums.FoodBigType.NO;
import static com.hrzj.yule.calc.config.enums.FoodBigType.gentai;
import static com.hrzj.yule.calc.config.enums.FoodBigType.hongjiu;
import static com.hrzj.yule.calc.config.enums.FoodBigType.pijiu;
import static com.hrzj.yule.calc.config.enums.FoodBigType.xiaochi;
import static com.hrzj.yule.calc.config.enums.FoodBigType.yangjiu;

/**
 * @author lifengquan
 * @description: 计算食品提成
 * @date 2024-05-16 15:40
 */
@Slf4j
public class FoodCalcUtil {


  public static final String line = "\\|";
  private final CashMapper cashMapper;
  private final CashItemMapper cashItemMapper;
  private String businessDayStar, businessDayEnd, cashId;
  private Map<Long, BigDecimal> noRewardCash, rewardOther, rewardOtherActive, commissionCash;

  private List<MoneyRule> moneyRules;

  public FoodCalcUtil(CashMapper cashMapper, CashItemMapper cashItemMapper, String businessDayStar,
                      String businessDayEnd, String cashId, List<MoneyRule> moneyRules,
                      Map<Long, BigDecimal> noRewardCash, Map<Long, BigDecimal> rewardOther,
                      Map<Long, BigDecimal> rewardOtherActive, Map<Long, BigDecimal> commissionCash) {
    this.cashMapper = cashMapper;
    this.cashItemMapper = cashItemMapper;
    this.businessDayStar = businessDayStar;
    this.businessDayEnd = businessDayEnd;
    this.moneyRules = moneyRules;
    this.cashId = cashId;
    this.noRewardCash = noRewardCash;
    this.rewardOther = rewardOther;
    this.rewardOtherActive = rewardOtherActive;
    this.commissionCash = commissionCash;
  }

  public static BigDecimal calc(List<CashItem> foods, FoodBigType foodBigType, Boolean ham) {
    log.debug("计算 {}", foodBigType.getSmName());
    if (ham && foodBigType.equals(NO)) {
      List<CashItem> hams =
        foods.stream().filter(food -> food.getFoodTypeId().equals(FoodSmallType.XBYHT.getFoodTypeId())).collect(Collectors.toList());
      return calc(hams);
    }
    List<FoodSmallType> group;
    List<Long> foodIds = new ArrayList<>();
    group = FoodSmallType.group(foodBigType);
    foodIds =
      group.stream().map(foodSmallType -> foodSmallType.getFoodTypeId()).collect(Collectors.toList());

    final List<Long> exclusion = GoodsFilter.get(foodBigType);
    //移除需要排除的商品（某些特定商品是不需要计算业绩的）
    List<Long> finalFoodIds = foodIds;
    List<CashItem> items =
      foods.stream().filter(cashItem -> finalFoodIds.contains(cashItem.getFoodTypeId())
        && !exclusion.contains(cashItem.getFoodId())).collect(Collectors.toList());
    return calc(items);
  }


  public static List<CashItem> calc(List<CashItem> foods, FoodBigType... foodBigType) {
    List<FoodSmallType> group = FoodSmallType.group(foodBigType);
    List<Long> all =
      group.stream().map(foodSmallType -> foodSmallType.getFoodTypeId()).collect(Collectors.toList());
    List<CashItem> importedWineItems =
      foods.stream().filter(cashItem -> all.contains(cashItem.getFoodTypeId())).collect(Collectors.toList());
    return importedWineItems;
  }

  public static BigDecimal calc(List<CashItem> cashItems) {
    if (CollectionUtils.isNotEmpty(cashItems)) {
      return cashItems.stream().map(e -> e.getMoney()).reduce(BigDecimal::add).get();
    }
    return null;
  }

  //计算其它业绩(奖励)
  public static void other(SalesEmployeeBase salesEmployee, OtherRule rule, Method methodEnum, BigDecimal sales) {
    SalesItem ext;
    switch (methodEnum) {
      case FIXED:
        if (null != rule.getPoint()) {
          ext = new SalesItem(SalesType.other, new FixedCommissionStrategy(rule.getPoint()));
          salesEmployee.addSalesItem(ext, sales);
        }
        break;
      default:
        if (null != rule.getPoint()) {
          ext = new SalesItem(SalesType.other, new PercentageCommissionStrategy(rule.getPoint()));
          salesEmployee.addSalesItem(ext, sales);
        }
        break;
    }
  }

  public Report calc(MoneyRule moneyRule, boolean isFilter) {
    ConcurrentHashMap<Long, List<Long>> empMergeMap = new ConcurrentHashMap<Long, List<Long>>();
    //红酒销售额
    BigDecimal redWideSales = BigDecimal.ZERO;
    //进口洋酒销售额
    BigDecimal importedWideSales;
    BigDecimal importedWinePoint = null;
    //订房人提成规则
    RoyaltyDetail royaltyDetail = moneyRule.getRoyaltyDetail();
    //员工id
    Long empId = moneyRule.getId();
    String groupName = moneyRule.getTeamName();
    String name = moneyRule.getName();
    String empGroup = groupName.contains(DOT) ? groupName : groupName.concat(DOT);
    //同组的所有收款单据
    List<Cash> empCash = null;
    if (groupName.equals("商务")) {
      empCash = cashMapper.cashByEmpGroup(moneyRule.getTeamNameWithName(), moneyRule.getTeamNameWithName(),
        this.businessDayStar,
        this.businessDayEnd);
    } else {
      empCash = cashMapper.cashByEmpGroup(empGroup, name,
        this.businessDayStar,
        this.businessDayEnd);
    }

    if (CollectionUtils.isEmpty(empCash)) {
      return null;
    }
    //每单100赔偿(空气净化器)
    BigDecimal compensation = new BigDecimal("100").multiply(new BigDecimal(String.valueOf(empCash.size())));

    BigDecimal unchangedCashSale = null;
    BigDecimal aloneCommonSaleSum = null;
    BigDecimal aloneCommonSaleMoney = null;

    //没有单独设置提成的账单
    List<Cash> unchangedCash =
      empCash.stream().filter(cash -> !this.commissionCash.keySet().contains(cash.getId())).collect(Collectors.toList());
    unchangedCashSale = unchangedCash.stream().map(e -> e.getFixMoney()).reduce(BigDecimal::add).get();

    //过滤出需要单独计算提成的账单
    if (!this.commissionCash.isEmpty()) {
      List<Cash> alone =
        empCash.stream().filter(cash -> this.commissionCash.keySet().contains(cash.getId())).collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(alone)) {
        aloneCommonSaleSum = alone.stream().map(e -> e.getFixMoney()).reduce(BigDecimal::add).get();
        aloneCommonSaleMoney = BigDecimal.ZERO;
        //计算特殊提成的账单的和
        for (Cash cash : alone) {
          BigDecimal fixMoney = cash.getFixMoney();
          BigDecimal commission = commissionCash.get(cash.getId());
          BigDecimal commissionMoney = fixMoney.multiply(commission).divide(new BigDecimal("100"), 2,
            RoundingMode.HALF_UP);
          aloneCommonSaleMoney = aloneCommonSaleMoney.add(commissionMoney);
        }
      }
    }

    BigDecimal jiuShui6000Sales = BigDecimal.ZERO;

    //总销售额度
    //BigDecimal sales = empCash.stream().map(e -> e.getFixMoney()).reduce(BigDecimallCashItems = {ArrayList@16187}
    // size = 117al::add).get();


    List<Long> cashIdList = null;
    if (isFilter) {
      cashIdList = empCash.stream().map(Cash::getId).collect(Collectors.toList());
    }
    //销售商品
    List<CashItem> allCashItems = this.cashItemMapper.cashItemsByEmpGroup(empGroup, cashIdList);
    //排除 6000+酒水
    String commonFilterStr = royaltyDetail.getCommonFilter();
    BigDecimal commonFilter = ExtractNumbers.findOnlyOne(commonFilterStr);
    if (null != commonFilter) {
      List<CashItem> jiuShuiItems = FoodCalcUtil.calc(allCashItems, yangjiu, hongjiu);
      jiuShui6000Sales =
        jiuShuiItems.stream().filter(
          importedWineItem -> importedWineItem.getMoney().compareTo(BigDecimal.ZERO) > 0
            &&
            (
              (
                importedWineItem.getUnitId().equals(10000011L) && importedWineItem.getFPrice().compareTo(commonFilter.multiply(new BigDecimal("2"))) > 0
                  ||
                  (!importedWineItem.getUnitId().equals(10000011L) && importedWineItem.getFPrice().compareTo(commonFilter) > 0)
              )

            )).map(e -> e.getMoney()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
      unchangedCashSale = unchangedCashSale.subtract(jiuShui6000Sales);
    }
    SaleEmployeeCommerce salesEmployee = new SaleEmployeeCommerce(moneyRule.getTeamNameWithName());

    //6000+酒水提成
    BigDecimal common6000 = royaltyDetail.getCommon6000();
    if (null != common6000) {
      SalesItem commonWine = new SalesItem(SalesType.common6000,
        new PercentageCommissionStrategy(common6000));
      salesEmployee.addSalesItem(commonWine, jiuShui6000Sales);
    }

    //通提是否包含跟台、小吃 不包含就要单独算
    String snack = royaltyDetail.getSnack();

    BigDecimal snackSale;
    YesOrNoEnums snackEnums = YesOrNoEnums.get(snack);
    if (snackEnums.equals(YesOrNoEnums.no)) {
      List<CashItem> items = FoodCalcUtil.calc(allCashItems, gentai, xiaochi);
      snackSale = FoodCalcUtil.calc(items);
      SalesItem snackCalc = new SalesItem(SalesType.gentai,
        new PercentageCommissionStrategy(royaltyDetail.getSnackPoint()));
      salesEmployee.addSalesItem(snackCalc, snackSale);
      unchangedCashSale =
        unchangedCashSale.subtract(null == snackSale ? BigDecimal.ZERO : snackSale).subtract(null == aloneCommonSaleSum ? BigDecimal.ZERO : aloneCommonSaleSum);

    }

    //红酒、啤酒额外奖励
    WineAndBeerRule wineAndBeerRule = royaltyDetail.getWineAndBeerRule();
    String thisMonthHandOut = null;
    if (null != wineAndBeerRule && null != wineAndBeerRule.getWineAndBeerBonusPoint()) {
      BigDecimal wineAndBeerBonusPoint = wineAndBeerRule.getWineAndBeerBonusPoint();
      thisMonthHandOut = wineAndBeerRule.getThisMonthHandOut();
      List<CashItem> redWineCashItem = FoodCalcUtil.calc(allCashItems, hongjiu, pijiu);

      if (CollectionUtils.isNotEmpty(redWineCashItem)) {
        redWideSales = redWineCashItem.stream().map(e -> e.getMoney()).reduce(BigDecimal::add).get();
      }
      SalesItem redWine = new SalesItem(SalesType.redWine,
        new PercentageCommissionStrategy(wineAndBeerBonusPoint));
      salesEmployee.addSalesItem(redWine, redWideSales);
      unchangedCashSale = unchangedCashSale.subtract(null == redWideSales ? BigDecimal.ZERO : redWideSales);
    }

    //扣除不算业绩不算提成的商品
    List<CashItem> excludeItems = FoodCalcUtil.calc(allCashItems, NO);
    BigDecimal excludeItemsSale = FoodCalcUtil.calc(excludeItems);
    unchangedCashSale =
      unchangedCashSale.subtract(null == excludeItemsSale ? BigDecimal.ZERO : excludeItemsSale).subtract(compensation);

    //通提提成点
    BigDecimal commonPoint = royaltyDetail.getCommonPoint();
    if (null != commonPoint) {
      SalesItem commonWine = new SalesItem(SalesType.common,
        new PercentageCommissionStrategy(commonPoint));
      salesEmployee.addSalesItem(commonWine, unchangedCashSale);
    }
    //无奖励的账单金额合计
    List<Cash> noRewardCash =
      empCash.stream().filter(cash -> this.noRewardCash.keySet().contains(cash.getId())).collect(Collectors.toList());
    BigDecimal noRewardCashSale =
      noRewardCash.stream().map(e -> e.getFixMoney()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

    //有其它奖励金额的账单金额合计
    List<Cash> rewardCash =
      empCash.stream().filter(cash -> this.rewardOther.keySet().contains(cash.getId())).collect(Collectors.toList());
    BigDecimal rewardOtherCashSale =
      rewardCash.stream().map(e -> e.getFixMoney()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

    BigDecimal rewardOtherMoney = BigDecimal.ZERO;
    if (!this.rewardOther.isEmpty()) {
      List<Cash> alone =
        empCash.stream().filter(cash -> this.rewardOther.keySet().contains(cash.getId())).collect(Collectors.toList());
      //计算特殊单中其它奖励的提成的和
      for (Cash cash : alone) {
        BigDecimal fixMoney = cash.getFixMoney();
        BigDecimal point = this.rewardOther.get(cash.getId());
        BigDecimal exclusionMoney = exclusionCashItems(cash);
        BigDecimal money = fixMoney.subtract(exclusionMoney).multiply(point).divide(new BigDecimal("100"), 2,
          RoundingMode.HALF_UP);
        rewardOtherMoney = rewardOtherMoney.add(money);
      }
    }


    //其他额外奖励(阶梯提成)
    AtomicReference<String> thisMonthHandOutOther = new AtomicReference<>();
    List<OtherRule> otherRule = royaltyDetail.getOtherRule();
    if (CollectionUtils.isNotEmpty(otherRule)) {

      BigDecimal finalSales = unchangedCashSale.subtract(rewardOtherCashSale).subtract(noRewardCashSale);
      Optional<OtherRule> max =
        otherRule.stream().filter(rule -> null != rule.getCondition() && rule.getCondition().multiply(new BigDecimal(
            "10000")).compareTo(finalSales) < 0).collect(Collectors.toList())
          .stream().max(Comparator.comparing(OtherRule::getCondition));

      if (max.isPresent()) {
        OtherRule rule = max.get();
        Method methodEnum = rule.getMethodEnum();
        String mergeOtherGroup = rule.getMergeOtherGroup();
        thisMonthHandOutOther.set(rule.getThisMonthHandOutOther());
        List<Long> mergeGroup = ExtractNumbers.findMany(mergeOtherGroup);
        //合并业绩
        if (CollectionUtils.isNotEmpty(mergeGroup)) {
          empMergeMap.put(empId, mergeGroup);
          List<Report> group = new ArrayList<>();
          for (Long id : mergeGroup) {
            MoneyRule team = this.moneyRules.stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
            if (id.equals(empId)) {
              rule.setMergeOtherGroup(null);
              Report mergeReport = calc(moneyRule, isFilter);
              group.add(mergeReport);
            } else {
              team.setTeamName(team.getTeamNameWithName());
              Report mergeReport = calc(team, isFilter);
              group.add(mergeReport);
            }
          }
          BigDecimal mergeSale = group.stream().map(e -> e.getTotalSale()).reduce(BigDecimal::add).get();
          OtherRule mergeSaleRule =
            otherRule.stream().filter(ruleMerge -> null != ruleMerge.getCondition() && ruleMerge.getCondition().multiply(new BigDecimal(
                "10000")).compareTo(mergeSale) < 0).collect(Collectors.toList())
              .stream().max(Comparator.comparing(OtherRule::getCondition)).orElse(null);
          other(salesEmployee, mergeSaleRule, methodEnum, mergeSale);
        } else {
          other(salesEmployee, rule, methodEnum, unchangedCashSale);
        }
      }
    }

    AtomicReference<String> thisMonthHandOutImport = new AtomicReference<>();
    //洋酒奖励
    List<ImportedRule> importedRule = royaltyDetail.getImportedRule();
    if (CollectionUtils.isNotEmpty(importedRule)) {

      List<CashItem> importedWineItems = FoodCalcUtil.calc(allCashItems, yangjiu);

      thisMonthHandOutImport.set(importedRule.get(0).getThisMonthHandOutImport());
      if (CollectionUtils.isNotEmpty(importedWineItems)) {
        importedWideSales = importedWineItems.stream().map(e -> e.getMoney()).reduce(BigDecimal::add).get();
        BigDecimal finalImportedWideSales1 = importedWideSales;
        Optional<ImportedRule> maxImportedRule =
          importedRule.stream().filter(rule -> null != rule.getCondition() && rule.getCondition().multiply(new BigDecimal("10000")).compareTo(finalImportedWideSales1) < 0).collect(Collectors.toList())
            .stream().max(Comparator.comparing(ImportedRule::getCondition));


        if (maxImportedRule.isPresent()) {
          ImportedRule rule = maxImportedRule.get();
          importedWinePoint = rule.getPoint();
          //减去滤出(低于filter才计算提成)
          importedWideSales =
            importedWineItems.stream().filter(importedWineItem -> importedWineItem.getMoney().compareTo(BigDecimal.ZERO) > 0
              && importedWineItem.getFPrice().compareTo(ExtractNumbers.findOnlyOne(rule.getFilter())) < 0).map(e -> e.getMoney()).reduce(BigDecimal::add).get();
        }
        BigDecimal finalImportedWideSales = importedWideSales;
        maxImportedRule.ifPresent(rule -> {
          Method methodEnum = rule.getMethodEnum();
          thisMonthHandOutImport.set(rule.getThisMonthHandOutImport());
          SalesItem imported;
          switch (methodEnum) {
            case FIXED:
              imported = new SalesItem(SalesType.importedWine, new FixedCommissionStrategy(rule.getPoint()));
              salesEmployee.addSalesItem(imported, finalImportedWideSales);
              break;
            default:
              imported = new SalesItem(SalesType.importedWine, new PercentageCommissionStrategy(rule.getPoint()));
              salesEmployee.addSalesItem(imported, finalImportedWideSales);
              break;
          }
        });
      } else {
        //有洋酒奖励规则，但未达条件
        importedWideSales = BigDecimal.ZERO;
      }
    } else {
      //无洋酒奖励规则
      importedWideSales = null;
    }


    BigDecimal rewardActiveMoney = BigDecimal.ZERO;
    if (!this.rewardOtherActive.isEmpty()) {
      List<Cash> alone =
        empCash.stream().filter(cash -> this.rewardOtherActive.keySet().contains(cash.getId())).collect(Collectors.toList());
      //计算特殊单中按百分比计算的提成的和
      for (Cash cash : alone) {
        BigDecimal fixMoney = cash.getFixMoney();
        BigDecimal point = this.rewardOtherActive.get(cash.getId());
        BigDecimal exclusionMoney = exclusionCashItems(cash);
        BigDecimal money = fixMoney.subtract(exclusionMoney).multiply(point).divide(new BigDecimal("100"), 2,
          RoundingMode.HALF_UP);
        rewardActiveMoney = rewardActiveMoney.add(money);
      }
    }
    List<Cash> aloneActive = new ArrayList<>();
    if (!this.rewardOtherActive.isEmpty()) {
      aloneActive =
        empCash.stream().filter(cash -> this.rewardOtherActive.keySet().contains(cash.getId())).collect(Collectors.toList());
    }

    //5.公司酒水活动奖励
    BigDecimal company = BigDecimal.ZERO;
    for (Cash cash : empCash) {
      if (!aloneActive.contains(cash)) {
        RoomType roomType = RoomType.valueOf(cash.getRoomTypeId());
        BigDecimal fixMoney = cash.getFixMoney();
        BigDecimal cTrueMoney = cash.getCTrueMoney();
        //实际支付比例
        BigDecimal proportion = fixMoney.divide(cTrueMoney, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        Floor floor = roomType.getFloor();
        BigDecimal exclusionMoney = exclusionCashItems(cash);
        BigDecimal reallyCalcMoney = cTrueMoney.subtract(exclusionMoney);
        BigDecimal floorRewards = new FloorPerformanceCalculationStrategyImpl().calculatePerformance(cash,
          royaltyDetail.getCompanyActivityRule(),
          reallyCalcMoney, proportion, floor);
        company = company.add(floorRewards);
      }
    }

    // 计算提成
    Report report = salesEmployee.calculateTotalCommission();
    BigDecimal totalCommission =
      report.getSum().add(null != aloneCommonSaleMoney ?
        aloneCommonSaleMoney : BigDecimal.ZERO).add(company).add(rewardActiveMoney);
    log.info("总销售额是:{};总提成金额是:{},公司酒水活动提成{}。-----{}", unchangedCashSale, totalCommission, company,
      moneyRule.getName());
    log.info(LF);
    report.setEmpId(empId);
    report.setNameWithGroup(moneyRule.getTeamNameWithName());
    report.setTotalSale(unchangedCashSale);
    report.setCommonPoint(commonPoint);
    report.setTotalSale2(aloneCommonSaleSum);
    report.setCommonMoney2(aloneCommonSaleMoney);
    report.setWine6000Point(common6000);
    report.setSnackPoint(royaltyDetail.getSnackPoint());
    report.setRedWinePoint(null != wineAndBeerRule ? wineAndBeerRule.getWineAndBeerBonusPoint() : null);
    if (null == report.getImportedWineSale()) {
      report.setImportedWineSale(importedWideSales);
    }
    report.setThisMonthHandOutRedBeer(thisMonthHandOut);
    report.setThisMonthHandOutOther(thisMonthHandOutOther.get());
    report.setSpecialMoney(rewardOtherMoney);
    report.setCompanyActiveSpecialMoney(rewardActiveMoney);
    report.setThisMonthHandOutImport(thisMonthHandOutImport.get());
    report.setImportedWinePoint(null != importedWinePoint ? (importedWinePoint.compareTo(new BigDecimal(100)) > 0 ?
      String.valueOf(importedWinePoint).concat("元") :
      String.valueOf(importedWinePoint)) : null);
    report.setCompanyActiveMoney(company);
    BigDecimal noPaymentMoney = report.noPaymentMoney();
    report.setNoPaymentMoney(noPaymentMoney);
    report.setSum(totalCommission.subtract(noPaymentMoney));
    return report;
  }

  private BigDecimal exclusionCashItems(Cash cash) {
    List<CashItem> cashItems = cashItemMapper.cashItemsByCid(cash.getId());
    BigDecimal gentaiSale = FoodCalcUtil.calc(cashItems, gentai, false);
    BigDecimal xiaochiSale = FoodCalcUtil.calc(cashItems, xiaochi, false);
    BigDecimal exclusionMoney = (null != gentaiSale ? gentaiSale : BigDecimal.ZERO.add(null != xiaochiSale ?
      xiaochiSale :
      BigDecimal.ZERO)).add(new BigDecimal("100"));
    return exclusionMoney;

  }

}
