package com.hrzj.yule.calc.mapper.mssql;

import com.hrzj.yule.calc.pojo.entity.mssql.projection.ShopListDetailCashProjection;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lifengquan
 * @description: 账单商品详情
 * @date 2024-04-28 12:41
 */
public interface ShopListDetailCashMapper {

  List<ShopListDetailCashProjection> findCashItems(@Param("db") String db);
}
