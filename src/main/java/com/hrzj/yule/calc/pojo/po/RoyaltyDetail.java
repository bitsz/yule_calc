package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 待遇提成明细
 * @date 2024/4/17 星期三 17:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoyaltyDetail {

  @ExcelProperty(value = {"待遇明细", "通提提成点"})
  //通提提成点
  private BigDecimal commonPoint;

  @ExcelProperty(value = {"待遇明细", "排除"})
  private String commonFilter;

  @ExcelProperty(value = {"待遇明细", "6000+酒水提成（百分比）"})
  private BigDecimal common6000;

  @ExcelProperty(value = {"待遇明细", "是否包含跟台、小吃"})
  private String snack;

  @ExcelProperty(value = {"待遇明细", "跟台、小吃提成点(百分比)"})
  private BigDecimal snackPoint;

  @ExcelProperty(value = {"待遇明细", "红酒、啤酒额外奖励"})
  private WineAndBeerRule wineAndBeerRule;


  @ExcelProperty(value = {"待遇明细", "其他额外奖励"})
  //其他额外奖励
  private List<OtherRule> otherRule;

  @ExcelProperty(value = {"待遇明细", "洋酒奖励"})
  //洋酒奖励
  private List<ImportedRule> importedRule;

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励"})
  //公司活动奖励
  private CompanyActivityRule companyActivityRule;


}
