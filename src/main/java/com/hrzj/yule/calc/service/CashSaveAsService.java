package com.hrzj.yule.calc.service;

import com.hrzj.yule.calc.pojo.entity.mssql.projection.CashProjection;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-08 16:19
 */
public interface CashSaveAsService {

  void saves(List<CashProjection> cash);
}
