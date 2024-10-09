package com.hrzj.yule.calc.repository.mysql;

import com.hrzj.yule.calc.pojo.entity.mysql.Cash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-28 14:20
 */
@Repository
public interface CashRepository extends JpaRepository<Cash, Long> {
}
