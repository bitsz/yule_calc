package com.hrzj.yule.calc.mapper.mssql;

import com.hrzj.yule.calc.pojo.entity.mssql.Employee;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-09 15:21
 */
public interface EmployeeMapper {

  List<Employee> findByEEnable(@Param("db") String db, @Param("enable") boolean enable);
}
