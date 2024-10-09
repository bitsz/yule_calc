package com.hrzj.yule.calc.repository.mssql;

import com.hrzj.yule.calc.pojo.entity.mssql.Employee;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/19 星期五 9:12
 */
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

  List<Employee> findByEEnable(boolean enable);
}
