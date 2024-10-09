package com.hrzj.yule.calc.service;

import com.hrzj.yule.calc.pojo.entity.mysql.FixCash;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-06-17 16:47
 */
public interface ReadCashService {

  void readAndUpdateCash(String path);

  void updateFixCash(List<FixCash> fixCashes);
}
