package com.hrzj.yule.calc.repository.mysql;

import com.hrzj.yule.calc.pojo.entity.mysql.CashPresentFood;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-11 13:35
 */
@Repository
public interface CashPresentFoodRepository extends JpaRepository<CashPresentFood, Long> {
}
