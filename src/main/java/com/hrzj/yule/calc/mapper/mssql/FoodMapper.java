package com.hrzj.yule.calc.mapper.mssql;

import com.hrzj.yule.calc.pojo.entity.mssql.Food;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/19 星期五 9:12
 */
public interface FoodMapper {

  List<Food> findAll(@Param("db") String db);
}
