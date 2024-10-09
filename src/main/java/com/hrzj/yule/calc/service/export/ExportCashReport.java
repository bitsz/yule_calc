package com.hrzj.yule.calc.service.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hrzj.yule.calc.config.exception.CommonException;
import com.hrzj.yule.calc.fx.query.Loading;
import com.hrzj.yule.calc.mapper.mssql.CashProjectionMapper;
import com.hrzj.yule.calc.mapper.mysql.CashItemMapper;
import com.hrzj.yule.calc.mapper.mysql.CashMapper;
import com.hrzj.yule.calc.mapper.mysql.FixCashMapper;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.pojo.entity.mysql.CashItem;
import com.hrzj.yule.calc.pojo.entity.mysql.CashPresentFood;
import com.hrzj.yule.calc.pojo.entity.mysql.FixCash;
import com.hrzj.yule.calc.pojo.entity.mysql.projection.CashItemProjection;
import com.hrzj.yule.calc.pojo.vo.CashReport;
import com.hrzj.yule.calc.repository.mysql.CashPresentFoodRepository;
import com.hrzj.yule.calc.service.ReadCashService;
import com.hrzj.yule.calc.service.calc.FoodCalcUtil;
import com.hrzj.yule.calc.util.ExtractNumbers;
import com.hrzj.yule.calc.util.excel.style.AloneWidthStyle;
import com.hrzj.yule.calc.util.excel.style.CustomRowWriteHandler;
import com.hrzj.yule.calc.util.excel.style.FreezeRowColHandler;
import com.hrzj.yule.calc.util.excel.style.FreezeRowColOptions;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;
import static cn.hutool.core.text.StrPool.BACKSLASH;
import static com.hrzj.yule.calc.config.enums.FoodBigType.NO;
import static com.hrzj.yule.calc.config.enums.FoodBigType.gentai;
import static com.hrzj.yule.calc.config.enums.FoodBigType.hongjiu;
import static com.hrzj.yule.calc.config.enums.FoodBigType.pijiu;
import static com.hrzj.yule.calc.config.enums.FoodBigType.xiaochi;
import static com.hrzj.yule.calc.config.enums.FoodBigType.yangjiu;

/**
 * @author lifengquan
 * @description: 账单业绩明细
 * @date 2024-05-16 15:15
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ExportCashReport {

  private final static String name = "账单统计表";
  private final CashMapper cashMapper;
  private final CashItemMapper cashItemMapper;
  private final FixCashMapper fixCashMapper;
  private final ReadCashService readCashService;
  private final CashPresentFoodRepository cashPresentFoodRepository;


  public String export(Loading loading, String sn, String start, String end) {
    // 设置 ZipSecureFile 的最小膨胀比
    ZipSecureFile.setMinInflateRatio(0.001);
    String fileName = null;
    LambdaQueryWrapper<Cash> wrapper = Wrappers.lambdaQuery(Cash.class);
    if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
      wrapper.between(Cash::getBusinessDay, start, end);
      fileName = String.format("%s【%s至%s】.xlsx", name, start, end);
    }
    if (StringUtils.isNotBlank(sn)) {
      wrapper.eq(Cash::getId, sn);
      fileName = String.format("%s【%s】.xlsx", name, sn);
    }
    Long count = cashMapper.selectCount(wrapper);
    if (count == 0) {
      throw new CommonException("暂无数据");
    }
    fileName = (null == fileName ? name + ".xlsx" : fileName);
    loading.showMessage("同步实际金额");
    readCashService.readAndUpdateCash("report/" + fileName.replace(name, name + "-销售明细"));
    wrapper.orderByAsc(Cash::getId);
    List<Cash> cashList = cashMapper.selectList(wrapper);

    List<CashReport> cashReportList = new ArrayList<>();
    for (Cash cash : cashList) {
      cashReportList.add(calc(loading, cash));
    }

    Collections.sort(cashReportList);

    OutputStream outputStream = FileGenUtil.genFile(fileName);

    EasyExcelFactory.write(outputStream, CashReport.class).
      registerWriteHandler(FileGenUtil.getExcelCellWidthStyleStrategy()).
      registerWriteHandler(FileGenUtil.getHorizontalCellStyleStrategy()).
      registerWriteHandler(new AloneWidthStyle(new ArrayList<AloneWidthStyle.Column>() {
        {
          add(AloneWidthStyle.Column.builder().columnIndex(0).width(14).build());
          add(AloneWidthStyle.Column.builder().columnIndex(1).width(14).build());
          add(AloneWidthStyle.Column.builder().columnIndex(2).width(12).build());
          add(AloneWidthStyle.Column.builder().columnIndex(3).width(12).build());
          add(AloneWidthStyle.Column.builder().columnIndex(4).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(6).width(12).build());
          add(AloneWidthStyle.Column.builder().columnIndex(21).width(15).build());
          add(AloneWidthStyle.Column.builder().columnIndex(22).width(12).build());
          add(AloneWidthStyle.Column.builder().columnIndex(23).width(12).build());
          add(AloneWidthStyle.Column.builder().columnIndex(24).width(12).build());
          add(AloneWidthStyle.Column.builder().columnIndex(26).width(20).build());
        }
      })).
      registerWriteHandler(new FreezeRowColHandler(new FreezeRowColOptions(2, 1, 0, 0, "A1:AA1"))).
      // registerWriteHandler(new CustomCellStyleHandler()).
        registerWriteHandler(new CustomRowWriteHandler(cashReportList, findFixCash(sn, start, end), CashReport.class)).
      autoCloseStream(Boolean.TRUE).sheet(name).
      doWrite(cashReportList);


    String path = "report".concat(BACKSLASH).concat(fileName);
    loading.showMessage("处理销售明细");
    String newFilePath = appendCashItem(sn, start, end, path);
    try {
      Files.delete(Paths.get(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return newFilePath;
  }

  private List<FixCash> findFixCash(String sn, String start, String end) {
    LambdaQueryWrapper<FixCash> wrapperFixCash = Wrappers.lambdaQuery(FixCash.class);
    if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
      wrapperFixCash.between(FixCash::getBusinessDay, start, end);
    }
    if (Objects.nonNull(sn)) {
      wrapperFixCash.eq(FixCash::getCashId, sn);
    }
    wrapperFixCash.orderByAsc(FixCash::getBusinessDay);
    List<FixCash> fixCashes = fixCashMapper.selectList(wrapperFixCash);
    return fixCashes;
  }

  public String appendCashItem(String sn, String start, String end, String path) {
    String newFile = path.replace(name, name + "-销售明细");
    //销售明细
    List<CashItemProjection> cashItemProjections = cashItemMapper.cashItemsByCidAndBusinessDay(sn, start, end);
    ExcelWriterBuilder writerBuilder =
      EasyExcel.write(path, CashItemProjection.class).registerWriteHandler(FileGenUtil.getExcelCellWidthStyleStrategy()).
        registerWriteHandler(new AloneWidthStyle(new ArrayList<AloneWidthStyle.Column>() {
          {
            add(AloneWidthStyle.Column.builder().columnIndex(0).width(17).build());
            add(AloneWidthStyle.Column.builder().columnIndex(1).width(15).build());
            add(AloneWidthStyle.Column.builder().columnIndex(2).width(20).build());
            add(AloneWidthStyle.Column.builder().columnIndex(4).width(15).build());
            add(AloneWidthStyle.Column.builder().columnIndex(5).width(10).build());
            add(AloneWidthStyle.Column.builder().columnIndex(6).width(10).build());
            add(AloneWidthStyle.Column.builder().columnIndex(7).width(12).build());
            add(AloneWidthStyle.Column.builder().columnIndex(8).width(15).build());
            add(AloneWidthStyle.Column.builder().columnIndex(9).width(10).build());

          }
        })).
        registerWriteHandler(FileGenUtil.getHorizontalCellStyleStrategy()).

        registerWriteHandler(new FreezeRowColHandler(new FreezeRowColOptions(2, 1, 0, 0, "A1:J1")))
        .needHead(true)
        .autoCloseStream(true);
    writerBuilder.withTemplate(path).file(newFile).sheet("销售明细").doWrite(cashItemProjections);
    return newFile;
  }

  public CashReport calc(Loading loading, Cash cash) {

    String day = DateUtil.format(DateUtil.parse(cash.getBusinessDay(), NORM_DATE_PATTERN), NORM_DATE_PATTERN);
    loading.showMessage(day);
    List<CashItem> cashItems = cashItemMapper.cashItemsByCid(cash.getId());
    //赠送的商品
    List<CashPresentFood> presentData =
      cashPresentFoodRepository.findBy(Example.of(CashPresentFood.builder().cId(cash.getId()).build()),
      query -> query.project("sldID", "cId", "fName").all());
    //过滤出赠送的商品不计算业绩
    if (CollectionUtils.isNotEmpty(presentData)){
      presentData.forEach(presentFood -> {
        log.info("{} 赠送商品:{}",cash.getId(), presentFood.getFName());
      });
      Set<Long> presentFoodIds = presentData.stream()
        .map(CashPresentFood::getSldID)
        .collect(Collectors.toSet());
      cashItems = cashItems.stream()
        .filter(cashItem -> !presentFoodIds.contains(cashItem.getId()))
        .collect(Collectors.toList());
    }
    CashReport cashReport = new CashReport();
    cashReport.setBusinessDay(day);
    cashReport.setCashId(cash.getId());
    cashReport.setRoom(cash.getRoomName());
    cashReport.setEmpName(cash.getEmpName());

    List<CashItem> importWineItems = FoodCalcUtil.calc(cashItems, yangjiu);
    if (CollectionUtils.isNotEmpty(importWineItems)) {

      BigDecimal importWine6000Sale =
        importWineItems.stream().filter
          (
            importedWineItem -> importedWineItem.getMoney().compareTo(BigDecimal.ZERO) > 0
              &&
              (
                (
                  (
                    importedWineItem.getUnitId().equals(10000011L) &&
                      importedWineItem.getFPrice().compareTo(ExtractNumbers.findOnlyOneInt(importedWineItem.getFoodName()) > 2 ? new BigDecimal("18000") : new BigDecimal("12000")) > 0
                  )
                    ||
                    (
                      !importedWineItem.getUnitId().equals(10000011L)
                        && importedWineItem.getFPrice().compareTo(new BigDecimal("6000")) > 0
                    )
                )
              )
          ).map(e -> e.getMoney()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
      cashReport.setImportWine6000Sale(importWine6000Sale);
    }

    cashReport.setSubjoinSale(FoodCalcUtil.calc(cashItems, gentai, false));
    BigDecimal importWineSale = FoodCalcUtil.calc(cashItems, yangjiu, false);
    if (null != importWineSale) {
      importWineSale = importWineSale.subtract(null == cashReport.getImportWine6000Sale() ? BigDecimal.ZERO :
        cashReport.getImportWine6000Sale());
      cashReport.setImportWineSale(importWineSale);
    }
    cashReport.setRedWineSale(FoodCalcUtil.calc(cashItems, hongjiu, false));
    cashReport.setBeerSale(FoodCalcUtil.calc(cashItems, pijiu, false));
    cashReport.setSnacksSale(FoodCalcUtil.calc(cashItems, xiaochi, false));
    //统一加100 空气净化器
    cashReport.setCompensation(cash.getOutMinFoodFee().add(new BigDecimal("100")));
    cashReport.setHam(FoodCalcUtil.calc(cashItems, NO, true));
    String vip = cash.getVip();

    cashReport.setDept(cashReport.getDept());
    cashReport.setSumSale(cashReport.getSumSale());
    //刷卡
    BigDecimal fixMoneyCard = cash.getFixMoneyCard();
    //现金
    BigDecimal fixMoneyCash = cash.getFixMoneyCash();
    BigDecimal cTrueMoney = cash.getCTrueMoney();

    if (null == fixMoneyCard && null == fixMoneyCash) {
      money(cashReport, cTrueMoney, null);
      cashReport.setDiscount(new BigDecimal("100"));
    } else {
      cashReport.setFixMoneyCard(fixMoneyCard);
      cashReport.setFixMoneyCash(fixMoneyCash);

      if (null != fixMoneyCard || null != fixMoneyCash) {
        cashReport.setTotal(cash.getFixMoney());
        money(cashReport, fixMoneyCard, fixMoneyCash);
      }

      if (null != cashReport.getTotal() && null != cash.getCTrueMoney() && cash.getCTrueMoney().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal discount =
          cashReport.getTotal().divide(cash.getCTrueMoney(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(
            "100"));
        cashReport.setDiscount(discount);
      }
    }

    cashReport.setNoReward(cash.getNoReward());
    cashReport.setRewardOther(cash.getRewardOther());
    cashReport.setRewardActive(cash.getRewardActive());
    cashReport.setCommission(cash.getCommission());
    cashReport.setRemark(cash.getRemark());
    cashReport.setVip(vip);
    cashReport.setCTrueMoney(cash.getCTrueMoney());
    cashReport.setTipMoney(cash.getTipMoney());
    cashReport.setHandlingFee(cash.getHandlingFee());
//    fixCashRepository.findById(cash.getId()).ifPresent(e -> {
//      cashReport.setSubjoinSale(e.getSubjoinSale());
//      cashReport.setImportWine6000Sale(e.getImportWine6000Sale());
//      cashReport.setImportWineSale(e.getImportWineSale());
//      cashReport.setRedWineSale(e.getRedWineSale());
//      cashReport.setBeerSale(e.getBeerSale());
//      cashReport.setSnacksSale(e.getSnacksSale());
//      cashReport.setOtherSale(e.getOtherSale());
//      cashReport.setSumSale(e.getSumSale());
//      cashReport.setCompensation(e.getCompensation());
//      cashReport.setHam(e.getHam());
//      cashReport.setTotal(e.getTotal());
//      cashReport.setDiscount(e.getDiscount());
//    });
    log.info("{} 计算完成", cashReport.getCashId());
    return cashReport;
  }

  private void money(CashReport cashReport, BigDecimal moneyCash, BigDecimal tipMoney) {
    if (null == moneyCash) {
      moneyCash = BigDecimal.ZERO;
    }
    if (null == tipMoney) {
      tipMoney = BigDecimal.ZERO;
    }
    BigDecimal all = moneyCash.add(tipMoney);
    cashReport.setOtherSale(all.
      subtract(null == cashReport.getSubjoinSale() ? BigDecimal.ZERO : cashReport.getSubjoinSale()).
      subtract(null == cashReport.getImportWine6000Sale() ? BigDecimal.ZERO : cashReport.getImportWine6000Sale()).
      subtract(null == cashReport.getImportWineSale() ? BigDecimal.ZERO : cashReport.getImportWineSale()).
      subtract(null == cashReport.getRedWineSale() ? BigDecimal.ZERO : cashReport.getRedWineSale()).
      subtract(null == cashReport.getBeerSale() ? BigDecimal.ZERO : cashReport.getBeerSale()).
      subtract(null == cashReport.getSnacksSale() ? BigDecimal.ZERO : cashReport.getSnacksSale()).
      subtract(null == cashReport.getHam() ? BigDecimal.ZERO : cashReport.getHam()).
      subtract(null == cashReport.getCompensation() ? BigDecimal.ZERO : cashReport.getCompensation())
    );
  }
}
