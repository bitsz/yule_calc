package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.config.enums.SalesType;
import com.hrzj.yule.calc.pojo.vo.Report;

import java.math.BigDecimal;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 商务
 * @date 2024-05-19 12:41
 */
@Slf4j
public class SaleEmployeeCommerce extends SalesEmployeeBase<Report> {


  public SaleEmployeeCommerce(String name) {
    super(name);
  }

  @Override
  public Report calculateTotalCommission() {
    log.info("核算{}", getName());
    BigDecimal totalCommission = BigDecimal.ZERO;
    Report report = new Report();
    // 计算销售项目提成
    for (Map.Entry<SalesItem, BigDecimal> entry : this.getSales().entrySet()) {
      SalesItem item = entry.getKey();
      SalesType type = item.getSalesType();
      BigDecimal salesAmount = entry.getValue();
      BigDecimal commission = item.calculateCommission(salesAmount);
      totalCommission = totalCommission.add(commission);
      switch (type) {
        case common:
          report.setCommonMoney(commission);
          break;
        case common6000:
          report.setWine6000Sale(salesAmount);
          report.setWine6000Moeny(commission);
          break;
        case gentai:
          report.setSnackSale(salesAmount);
          report.setSnackMoeny(commission);
          break;
        case redWine:
          report.setRedWineSale(salesAmount);
          report.setRedWineMoney(commission);
          break;
        case other:
          report.setOtherMoney(commission);
          break;
        case importedWine:
          report.setImportedWineSale(salesAmount);
          report.setImportedWineMoney(commission);
          break;


      }
      log.info("{} {}  提成{}", item.getSalesType().getValue(), salesAmount, commission);
    }
    report.setSum(totalCommission);
    return report;
  }
}
