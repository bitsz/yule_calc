package com.hrzj.yule.calc.pojo.entity.mssql;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/15 星期一 16:44
 */
@Entity
@Data
@Table(name = "Cash")
public class Cash {
  @Id
  private Long cID;
  private String cId;
  private String room;
  private String businessDay;
  private String foodFee;
  private String outMinFoodFee;
  private String serviceFee;
  private String serviceFeeMoney;
  private String totalMoney;
  private String trueMoney;
  private String payMode;
  private String startTime;
  private String endTime;
  private String eName;

}
