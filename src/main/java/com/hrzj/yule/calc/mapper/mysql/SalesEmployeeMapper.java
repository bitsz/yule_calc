package com.hrzj.yule.calc.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.CashEmployee;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-19 11:11
 */
@Repository
@Mapper
public interface SalesEmployeeMapper extends BaseMapper<CashEmployee> {

  void saves(List<CashEmployee> employeeList);

  void clear();

  List<CashEmployee> getDJ();
}
