package com.hrzj.yule.calc.service.impl;

import com.hrzj.yule.calc.mapper.mssql.FoodMapper;
import com.hrzj.yule.calc.pojo.entity.mssql.Food;
import com.hrzj.yule.calc.pojo.entity.mysql.CashFood;
import com.hrzj.yule.calc.repository.mysql.CashFoodRepository;
import com.hrzj.yule.calc.service.FoodService;
import com.hrzj.yule.calc.util.BeanCopyTool;
import com.hrzj.yule.calc.util.DbContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 同步所有的商品
 * @date 2024-05-15 17:47
 */
@Service
@Slf4j
public class FoodServiceImpl implements FoodService {

  @Autowired
  private FoodMapper foodMapper;

  @Autowired
  private CashFoodRepository cashFoodRepository;

  @Override
  public void saves() {
    String db = DbContextHolder.getDB();
    List<Food> all = foodMapper.findAll(db);
    List<CashFood> cashFoods = BeanCopyTool.convertCopyList(all, CashFood.class);
    cashFoodRepository.saveAll(cashFoods);

  }
}
