package com.hrzj.yule.calc.util.usb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import cn.hutool.core.util.StrUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.CharPool.BACKSLASH;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/10 星期三 13:36
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "exclusion")
public class DiskData {

  private final static String suffix = ".MDF";

  @Setter
  private List<String> disk;

  public static Map<String, List<FileInfo>> findFilesWithSuffix(File disk, String fileSuffix, Map<String,
    List<FileInfo>> map) {
    File[] files = disk.listFiles();
    if (null == files) {
      return null;
    }

    // 遍历目录中的文件
    for (File file : disk.listFiles()) {
      Boolean windows = file.getAbsolutePath().contains("WINDOWS");
      int level = file.getPath().split(StrUtil.BACKSLASH + StrUtil.BACKSLASH).length;
      if (level > 6 || windows) {
        continue;
      }
      // 如果是文件而不是目录
      if (file.isFile()) {
        // 检查文件是否具有指定的后缀
        if (file.getName().endsWith(fileSuffix)) {
          String parent = file.getParent();
          log.info("所在文件夹: " + parent);
          // 要搜索的目录
          File directory = new File(parent);
          if (directory.getAbsolutePath().contains("收银系统数据库备份")) {
            continue;
          }
          File[] fileBucket = directory.listFiles();
          String day = parent.substring(parent.lastIndexOf(BACKSLASH) + 1);
          log.info("日期:" + parent.substring(parent.lastIndexOf(BACKSLASH) + 1));
          List<FileInfo> list = new ArrayList<>();
          for (File item : fileBucket) {
            if (item.getAbsolutePath().endsWith("LDF") || item.getAbsolutePath().endsWith("MDF")) {
              FileInfo fileInfo = new FileInfo(item.getAbsolutePath(), item);
              list.add(fileInfo);
              log.info("找到文件:" + item.getAbsolutePath());
            }
          }
          if (!map.keySet().contains(day)) {
            map.put(day, list);
          }
          log.info("-------------------------------------------{}-------------------------------------------------",
            day);
        }
      }
      // 如果是目录，则递归调用此函数
      else if (file.isDirectory()) {
        findFilesWithSuffix(file, fileSuffix, map);
      }
    }
    return map;
  }

  public boolean isExclude(String path) {
    for (String s : disk) {
      if (path.startsWith(s)) {
        return true;
      }
    }
    return false;
  }

  public Map<String, List<FileInfo>> findMDF() {

    Map<String, List<FileInfo>> map = new HashMap<String, List<FileInfo>>();

    FileSystemView sys = FileSystemView.getFileSystemView();
    File[] files = File.listRoots();
    for (int i = 0; i < files.length; i++) {
      String systemTypeDescription = sys.getSystemTypeDescription(files[i]);
      boolean exclude = isExclude(files[i].getPath());
      if (!exclude) {
        log.info("在{}检索", files[i] + systemTypeDescription);
        log.info("DiskName: " + files[i].getPath());
        log.info("总磁盘空间: " + files[i].getTotalSpace() / 1024 / 1024 / 1024 + " GB");
        log.info("可用磁盘空间: " + files[i].getFreeSpace() / 1024 / 1024 / 1024 + " GB");
        log.info("========================================{}=====================================================",
          files[i] + " -- " + systemTypeDescription);
        findFilesWithSuffix(files[i], suffix, map);
      } else {
        log.info("跳过{}", files[i] + systemTypeDescription);
      }
    }
    Map<String, List<FileInfo>> filterMap = filter(map);
    return filterMap;
  }

  public Map<String, List<FileInfo>> filter(Map<String, List<FileInfo>> stringListMap) {
    Map<String, List<FileInfo>> map = new HashMap<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    Iterator<String> dayIterator = stringListMap.keySet().iterator();
    String day = null;
    while (dayIterator.hasNext()) {
      try {
        day = dayIterator.next();
        List<FileInfo> fileInfos = stringListMap.get(day);

        // 尝试将键解析为日期
        dateFormat.parse(day);
        day = day.substring(0, 8);
        if (day.length() > 8) {
          dayIterator.remove();
        } else {
          map.put(day, fileInfos);
        }
      } catch (ParseException e) {
        dayIterator.remove();
      }
    }
    return map;
  }

}
