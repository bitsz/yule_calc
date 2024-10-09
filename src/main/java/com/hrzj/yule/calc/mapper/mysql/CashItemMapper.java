package com.hrzj.yule.calc.mapper.mysql;

import com.hrzj.yule.calc.pojo.entity.mysql.CashItem;
import com.hrzj.yule.calc.pojo.entity.mysql.projection.CashItemProjection;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-29 14:52
 */
public interface CashItemMapper {

  List<CashItem> cashItemsByEmpId(@Param("empId") Long empId, @Param("cashList") List<Long> cashList);

  List<CashItem> cashItemsByEmpGroup(@Param("empGroup") String empGroup, @Param("cashList") List<Long> cashList);

  List<CashItem> cashItemsByCid(@Param("cId") Long cId);

  List<CashItemProjection> cashItemsByCidAndBusinessDay(@Param("cId") String cId,
                                                        @Param("businessDayStart") String businessDayStart,
                                                        @Param("businessDayEnd") String businessDayEnd);
}
