package com.hrzj.yule.calc.util.usb;


import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/10 星期三 15:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {

  private String url;

  private File file;
}
