package com.hrzj.yule.calc.mapper.mssql;

import com.hrzj.yule.calc.pojo.entity.mssql.projection.CashProjection;
import com.hrzj.yule.calc.pojo.entity.mysql.CashOutMinFood;
import com.hrzj.yule.calc.pojo.entity.mysql.CashPresentFood;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/15 星期一 17:13
 */
@Repository
public interface CashProjectionMapper {


  List<CashProjection> findCash(@Param("db") String db, @Param("sn") String sn);

  List<CashOutMinFood> cOutMinFoodFeeList(@Param("db") String db);

  //赠送的商品
  List<CashPresentFood> cPresentFoodFeeList(@Param("db") String db);
}
