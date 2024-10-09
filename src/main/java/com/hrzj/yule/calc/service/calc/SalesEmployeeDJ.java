package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.config.enums.SalesType;
import com.hrzj.yule.calc.pojo.vo.ReportDJ;

import java.math.BigDecimal;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: DJ
 * @date 2024-05-19 12:39
 */
@Slf4j
public class SalesEmployeeDJ extends SalesEmployeeBase<ReportDJ> {


  public SalesEmployeeDJ(String name) {
    super(name);
  }

  @Override
  public ReportDJ calculateTotalCommission() {
    BigDecimal totalCommission = BigDecimal.ZERO;
    ReportDJ report = new ReportDJ();
    // 计算DJ销售项目提成
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
        case other:
          report.setOtherMoney(commission);
          break;
        case gentai:
          report.setSnackSale(salesAmount);
          report.setSnackMoeny(commission);

          break;
      }
      log.info("DJ {} {}  提成{}", item.getSalesType().getValue(), salesAmount, commission);
    }
    report.setSum(totalCommission);
    return report;
  }
}
