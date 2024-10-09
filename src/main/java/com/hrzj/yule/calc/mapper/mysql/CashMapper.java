package com.hrzj.yule.calc.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-28 14:35
 */
@Repository
@Mapper
public interface CashMapper extends BaseMapper<Cash> {

  List<Cash> cashByEmpId(@Param("cashId") String cashId, @Param("empId") Long empId,
                         @Param("businessDayStar") String businessDayStar,
                         @Param("businessDayEnd") String businessDayEnd);

  List<Cash> cashByEmpGroup(@Param("empGroup") String empGroup, @Param("name") String name,
                            @Param("businessDayStar") String businessDayStar,
                            @Param("businessDayEnd") String businessDayEnd);


  void fixMoney(@Param("fixMoneyCard") BigDecimal fixMoneyCard,
                @Param("fixMoneyCash") BigDecimal fixMoneyCash,
                @Param("tipMoney") BigDecimal tipMoney,
                @Param("handlingFee") BigDecimal handlingFee,
                @Param("noReward") String noReward,
                @Param("rewardOther") BigDecimal rewardOther,
                @Param("rewardActive") BigDecimal rewardActive,
                @Param("commission") BigDecimal commission,
                @Param("remark") String remark,
                @Param("vip") String vip,
                @Param("id") Long id);


  //IPage<Cash> selectPage(Page<Cash> page, LambdaQueryWrapper<Cash> wrapper);

}
