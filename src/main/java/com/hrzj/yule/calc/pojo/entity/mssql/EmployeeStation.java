package com.hrzj.yule.calc.pojo.entity.mssql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/19 星期五 9:35
 */
@Entity
@Data
@Table(name = "EmployeeStation")
public class EmployeeStation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "esID")
  private Long id;

  @Column(name = "esName")
  private String esName;
}
