package com.hrzj.yule.calc.util.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 读取数据监听
 * @date 2024/4/18 星期四 10:32
 */
@Slf4j
public class FieldEasyExcelListener<T> extends AnalysisEventListener<T> {

  private List<T> datas;

  private Integer rowIndex;

  private List<CellExtra> extraMergeInfoList;

  public FieldEasyExcelListener(Integer rowIndex) {
    this.rowIndex = rowIndex;
    datas = new ArrayList<>();
    extraMergeInfoList = new ArrayList<>();
  }

  @Override
  public void invoke(T data, AnalysisContext context) {
    log.debug("解析到一条数据: {}", JSONUtil.toJsonPrettyStr(data));
    context.readWorkbookHolder().setIgnoreEmptyRow(false);
    ReflectUtil.invoke(data, "setIndex", StrUtil.toString(context.readRowHolder().getRowIndex()));
    datas.add(data);
  }

  @Override
  public void doAfterAllAnalysed(AnalysisContext context) {
    log.info("所有数据解析完成！");
  }

  @Override
  public void extra(CellExtra extra, AnalysisContext context) {
    log.debug("读取到了一条合并信息:{}", JSONUtil.toJsonPrettyStr(extra));
    switch (extra.getType()) {
      case MERGE:
        if (extra.getRowIndex() >= rowIndex) {
          extraMergeInfoList.add(extra);
        }
        break;
      default:
    }
  }

  public List<T> getData() {
    return datas;
  }


  public List<CellExtra> getExtraMergeInfoList() {
    return extraMergeInfoList;
  }


}
