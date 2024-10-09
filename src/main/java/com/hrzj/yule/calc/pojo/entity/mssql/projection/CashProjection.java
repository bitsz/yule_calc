package com.hrzj.yule.calc.pojo.entity.mssql.projection;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/15 星期一 15:48
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CashProjection {

  private Integer row;
  private Long cId;
  private Long crId;
  private String room;
  private Long roomTypeId;
  private String roomType;
  private Date businessDay;
  private String esName;
  private String foodFee;
  private String outMinFoodFee;
  private String serviceFee;
  private String serviceFeeMoney;
  private String totalMoney;
  private String trueMoney;
  private int payMode;
  private String startTime;
  private String endTime;
  private String cashComment;
  private String roomComment;
  private BigDecimal fixMoney;
  private Long eId;
  private String eName;


}
