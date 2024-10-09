package com.hrzj.yule.calc.mapper.mysql;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.FixCash;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-06-07 10:52
 */
@Repository
@Mapper
public interface FixCashMapper extends BaseMapper<FixCash> {

  @Override
  List<FixCash> selectList(@Param("ew") Wrapper<FixCash> queryWrapper);

  void batchInsert(@Param("list") List<FixCash> list);

  void deleteFixCash(@Param("ids") List<Long> ids);
}
