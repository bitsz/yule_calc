package com.hrzj.yule.calc.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RoomType {

  F3_HB(10000022L, "豪包", Floor.F3),
  F3_DB(10000023L, "大包", Floor.F3),
  F3_ZB(10000024L, "中包", Floor.F3),
  F3_XB(10000025L, "小包", Floor.F3),
  F48_VIP(10000027L, "48楼VIP", Floor.F48),
  F48_VIPWL(10000029L, "48楼VIP湾流", Floor.F48),
  F48_VIPLF(10000030L, "48楼VIP拉菲", Floor.F48),
  F3_JST(10000031L, "豪包（金色厅）", Floor.F3),
  F48_XBJ(10000032L, "48楼新包间", Floor.F48),
  F48_LT(10000033L, "48楼拉图", Floor.F48),
  F48_HB(10000034L, "48楼豪包", Floor.F48),
  F48_HB1(10000035L, "48楼豪包1", Floor.F48),
  F48_HB2(10000036L, "48楼豪包2", Floor.F48),
  F4(10000037L, "4楼", Floor.F4),
  F3_XZB(10000038L, "新中包", Floor.F3),
  F3_XDB(10000039L, "新大包", Floor.F3),
  F3_DGL(10000040L, "戴高乐", Floor.F3),
  F3_XHB(10000041L, "新豪包", Floor.F3),
  F48_BC(10000042L, "48楼柏翠", Floor.F48),
  F48_HB3(10000043L, "48楼豪包3", Floor.F48),
  F48_BY(10000044L, "48楼波音", Floor.F48),
  F48_XXB(10000045L, "新小包", Floor.F48),

  ;

  private Long rtId;
  private String rtName;
  private Floor floor;


  public static RoomType valueOf(Long roomId) {
    if (null == roomId) {
      return null;
    } else {
      RoomType[] values = RoomType.values();
      for (RoomType value : values) {
        if (roomId.compareTo(value.getRtId()) == 0) {
          return value;
        }
      }
      return null;
    }
  }
}
