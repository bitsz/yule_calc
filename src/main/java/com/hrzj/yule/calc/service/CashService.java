package com.hrzj.yule.calc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-28 16:10
 */
public interface CashService extends IService<Cash> {


  IPage<Cash> findCashRecords(String sn, String start, String end, int currentPage);
}
