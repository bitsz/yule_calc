package com.hrzj.yule.calc.service.impl;

import com.hrzj.yule.calc.mapper.mssql.ShopListDetailCashMapper;
import com.hrzj.yule.calc.pojo.entity.mssql.projection.CashProjection;
import com.hrzj.yule.calc.pojo.entity.mssql.projection.ShopListDetailCashProjection;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.pojo.entity.mysql.CashItem;
import com.hrzj.yule.calc.repository.mysql.CashItemsRepository;
import com.hrzj.yule.calc.repository.mysql.CashRepository;
import com.hrzj.yule.calc.service.CashSaveAsService;
import com.hrzj.yule.calc.util.DbContextHolder;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-08 16:20
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CashSaveAsServiceImpl implements CashSaveAsService {

  private final CashRepository cashRepository;

  private final ShopListDetailCashMapper shopListDetailCashMapper;

  private final CashItemsRepository cashItemsRepository;

  @Resource(name = "mysqlEntityManagerFactory")
  private EntityManagerFactory mysqlEntityManagerFactory;

  @Override
  public void saves(List<CashProjection> cash) {
    EntityManager entityManager = mysqlEntityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    List<Cash> cashList = new ArrayList<>();
    for (CashProjection cashProjection : cash) {
      Cash build = new Cash();
      build.setId(cashProjection.getCId());
      build.setBusinessDay(DateUtil.format(cashProjection.getBusinessDay(), NORM_DATE_PATTERN));
      build.setCrId(cashProjection.getCrId());
      build.setRoomName(cashProjection.getRoom());
      build.setRoomTypeId(cashProjection.getRoomTypeId());
      build.setRoomTypeName(cashProjection.getRoomType());
      build.setEsName(cashProjection.getEsName());
      build.setStartTime(DateUtil.parse(cashProjection.getStartTime(), NORM_DATETIME_PATTERN));
      build.setEndTime(DateUtil.parse(cashProjection.getEndTime(), NORM_DATETIME_PATTERN));
      build.setFoodFee(new BigDecimal(cashProjection.getFoodFee()));
      build.setOutMinFoodFee(new BigDecimal(cashProjection.getOutMinFoodFee()));
      build.setServiceFee(new BigDecimal(cashProjection.getServiceFee()));
      build.setServiceFeeMoney(new BigDecimal(cashProjection.getServiceFeeMoney()));
      build.setTotalMoney(new BigDecimal(cashProjection.getTotalMoney()));
      build.setCTrueMoney(new BigDecimal(cashProjection.getTrueMoney()));
      build.setCPayMode(cashProjection.getPayMode());
      build.setCCashComment(cashProjection.getCashComment());
      build.setCRoomComment(cashProjection.getRoomComment());
      build.setEmpId(cashProjection.getEId());
      build.setEmpName(cashProjection.getEName());
      build.setTime(new Date());
      cashList.add(build);
    }
    String db = DbContextHolder.getDB();
    List<ShopListDetailCashProjection> items = shopListDetailCashMapper.findCashItems(db);
    List<CashItem> cashItems = new ArrayList<>();
    for (ShopListDetailCashProjection item : items) {
      CashItem build = CashItem.builder()
        .id(item.getId())
        .cId(item.getCashId())
        .slType(item.getSlType())
        .foodId(item.getFoodId())
        .foodName(item.getFoodName())
        .foodTypeId(item.getFtId())
        .foodTypeName(item.getFtName())
        .bigType(item.getFoodBigType().getSmId())
        .bigTypeName(item.getFoodBigType().getSmName())
        .empId(item.getEmpId())
        .empName(item.getEmpName())
        .money(item.getMoney())
        .quantity(Integer.valueOf(item.getQuantity()))
        .time(new Date())
        .fPrice(item.getPrice())
        .build();
      cashItems.add(build);
    }
    cashItemsRepository.saveAll(cashItems);

    if (CollectionUtils.isNotEmpty(cashList)) {
      try {
        transaction.begin();  // 开始事务
        cashRepository.saveAll(cashList);
        cashRepository.flush();
        transaction.commit();  // 提交事务
      } catch (Exception e) {
        if (transaction != null && transaction.isActive()) {
          transaction.rollback();  // 回滚事务
        }
        throw e;
      } finally {
        entityManager.close();  // 关闭实体管理器
      }

    }
  }
}
