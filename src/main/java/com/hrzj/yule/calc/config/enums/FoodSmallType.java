package com.hrzj.yule.calc.config.enums;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-29 15:40
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FoodSmallType {

  BLD(10000048L, "白兰地", FoodBigType.yangjiu),
  WSJ(10000049L, "威士忌", FoodBigType.yangjiu),
  LJJ(10000050L, "力娇酒", FoodBigType.yangjiu),
  JJ(10000052L, "金酒", FoodBigType.yangjiu),
  LMJ(10000053L, "朗姆酒", FoodBigType.yangjiu),
  HJL(10000055L, "红酒类", FoodBigType.hongjiu),
  XY(10000056L, "香烟", FoodBigType.NO),
  XB(10000057L, "香槟", FoodBigType.yangjiu),
  PJ(10000058L, "啤酒", FoodBigType.pijiu),
  RY(10000059L, "软饮", FoodBigType.ruanyin),
  ZJ(10000060L, "纸巾", FoodBigType.jiushui),
  CFXC(10000062L, "厨房小吃", FoodBigType.xiaochi),
  GP(10000063L, "果盘", FoodBigType.jiushui),
  AAAAA(10000064L, "AAAAA套  餐AAAAA", FoodBigType.gentai),
  PSYL(10000065L, "配送饮料", FoodBigType.gentai),
  PC(10000066L, "赔偿", FoodBigType.NO),
  GTTC(10000067L, "跟台套餐", FoodBigType.gentai),
  GTPS(10000069L, "跟台配送", FoodBigType.gentai),
  FUF(10000070L, "服务费", FoodBigType.NO),
  XIAOCHI(10000071L, "小吃", FoodBigType.xiaochi),
  CHA(10000072L, "茶", FoodBigType.jiushui),
  GZ(10000073L, "果汁", FoodBigType.jiushui),
  PK(10000074L, "扑克", FoodBigType.NO),
  XJ(10000075L, "雪茄", FoodBigType.NO),
  BQL(10000076L, "冰淇淋", FoodBigType.NO),
  SZSS15(10000077L, "三只松鼠15", FoodBigType.NO),
  QT(10000078L, "其他", FoodBigType.jiushui),
  XXG(10000079L, "小心肝", FoodBigType.jiushui),
  XBYHT(10000080L, "西班牙火腿", FoodBigType.NO), //算业绩不算提成
  DHJ(10000081L, "打火机", FoodBigType.jiushui),
  CFPC(10000082L, "厨房赔偿", FoodBigType.NO),
  PC1(10000083L, "赔偿1", FoodBigType.NO),
  ZMHDJS(10000084L, "周日活动酒水", FoodBigType.jiushui),
  JRHD(10000085L, "节日活动", FoodBigType.jiushui),

  ;

  private Long foodTypeId;
  private String foodTypeName;
  private FoodBigType bigType;

  public static FoodSmallType valueOf(Long smId) {
    if (null == smId) {
      return null;
    } else {
      FoodSmallType[] values = FoodSmallType.values();
      for (FoodSmallType value : values) {
        if (smId.compareTo(value.getFoodTypeId()) == 0) {
          return value;
        }
      }
      return null;
    }
  }

  public static List<FoodSmallType> group(FoodBigType... types) {
    List<FoodSmallType> group = new ArrayList<>();
    for (FoodBigType foodBigType : types) {
      FoodSmallType[] values = FoodSmallType.values();
      for (FoodSmallType value : values) {
        FoodBigType bigType = value.bigType;
        if (bigType.equals(foodBigType)) {
          group.add(value);
        }
      }
    }
    return group;
  }
}
