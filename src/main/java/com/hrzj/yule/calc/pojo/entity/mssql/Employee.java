package com.hrzj.yule.calc.pojo.entity.mssql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/18 星期四 17:50
 */
@Entity
@Data
@Table(name = "Employee")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "eID")
  private Long id;

  private String eName;

  @Column(name = "eEnable")
  private boolean EEnable;

  @OneToOne
  @JoinColumn(name = "e_esID")
  private EmployeeStation employeeStation;
}
