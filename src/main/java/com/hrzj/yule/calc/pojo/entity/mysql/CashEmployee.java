package com.hrzj.yule.calc.pojo.entity.mysql;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-19 11:03
 */
@Entity
@Data
@Table(name = "employee")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashEmployee {

  @Id
  private Long id;

  private String empNo;

  private String empName;

  private Boolean empEnable;

  private int empSex;

  private int presentLevel;

  private Long esId;

  private String esName;

  private Boolean isDj;

  private String time;


}
