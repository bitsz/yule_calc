package com.hrzj.yule.calc.pojo.entity.mysql;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 */
@Entity
@Data
@Table(name = "scan_records")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanRecords {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //订单编号
  private String orderId;

  //用户编号
  private String day;

  //编号
  private String serialNumber;

  //数据目录
  private String dir;

  //创建时间
  private Date createTime;

}
