package com.hrzj.yule.calc.pojo.entity.mysql;

import com.baomidou.mybatisplus.annotation.TableField;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-28 13:44
 */
@Entity
@Table(name = "cash")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cash {

  @Id
  private Long id;
  @TableField("crId")
  private Long crId;
  @TableField("roomName")
  private String roomName;
  @TableField("roomTypeId")
  private Long roomTypeId;
  @TableField("roomTypeName")
  private String roomTypeName;
  @TableField("businessDay")
  private String businessDay;
  @TableField("esName")
  private String esName;
  @TableField("startTime")
  private Date startTime;
  @TableField("endTime")
  private Date endTime;
  @TableField("foodFee")
  private BigDecimal foodFee;
  @TableField("outMinFoodFee")
  private BigDecimal outMinFoodFee;
  @TableField("serviceFee")
  private BigDecimal serviceFee;
  @TableField("serviceFeeMoney")
  private BigDecimal serviceFeeMoney;
  @TableField("totalMoney")
  private BigDecimal totalMoney;
  @TableField("cTrueMoney")
  private BigDecimal cTrueMoney;
  @TableField("cPayMode")
  private int cPayMode;
  @TableField("cCashComment")
  private String cCashComment;
  @TableField("cRoomComment")
  private String cRoomComment;
  @TableField("empId")
  private Long empId;
  @TableField("empName")
  private String empName;
  @TableField("time")
  private Date time;
  @TableField("fixMoneyCard")
  private BigDecimal fixMoneyCard;
  @TableField("fixMoneyCash")
  private BigDecimal fixMoneyCash;
  @TableField("tipMoney")
  private BigDecimal tipMoney;
  @TableField("handlingFee")
  private BigDecimal handlingFee;
  @TableField("noReward")
  private String noReward;
  @TableField("rewardOther")
  private BigDecimal rewardOther;
  @TableField("rewardActive")
  private BigDecimal rewardActive;
  @TableField("commission")
  private BigDecimal commission;
  @TableField("remark")
  private String remark;
  @TableField("vip")
  private String vip;

  @TableField(exist = false)
  @Transient
  private BigDecimal fixMoney;

  public BigDecimal getFixMoney() {
    fixMoney = (null != fixMoneyCard ? fixMoneyCard : BigDecimal.ZERO).add((null != fixMoneyCash ? fixMoneyCash :
      BigDecimal.ZERO));
    return fixMoney;
  }
}
