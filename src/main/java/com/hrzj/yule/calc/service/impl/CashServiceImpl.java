package com.hrzj.yule.calc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrzj.yule.calc.mapper.mysql.CashMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.service.CashService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-28 16:11
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CashServiceImpl extends ServiceImpl<CashMapper, Cash> implements CashService {

  private static final Logger log = LoggerFactory.getLogger(CashServiceImpl.class);

  private final CashMapper cashMapper;

  @Value("${page.size}")
  private Integer pageSize;


  @Override
  public IPage<Cash> findCashRecords(String sn, String start, String end, int currentPage) {
    LambdaQueryWrapper<Cash> wrapper = Wrappers.lambdaQuery(Cash.class);
    if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
      wrapper.between(Cash::getBusinessDay, start, end);
    }
    if (StringUtils.isNotBlank(sn)) {
      wrapper.eq(Cash::getId, sn);
    }
    wrapper.orderByAsc(Cash::getId);
    Page<Cash> page = Page.of(currentPage, pageSize);
    page.setOptimizeCountSql(true);
    IPage data = cashMapper.selectPage(page, wrapper);
    long total = data.getTotal();
    long pages = data.getPages();
    log.info("总条数{} 总页数{}", total, pages);
    return page;
  }
}
