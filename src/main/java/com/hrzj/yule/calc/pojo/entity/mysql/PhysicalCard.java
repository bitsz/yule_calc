package com.hrzj.yule.calc.pojo.entity.mysql;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "physical_card")
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalCard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //物理号
  private String physicalId;

  //编号
  private String serialNumber;

  //天
  private String day;

  private String data;

}
