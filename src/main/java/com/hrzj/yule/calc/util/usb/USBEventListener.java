package com.hrzj.yule.calc.util.usb;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MS_PATTERN;
import static cn.hutool.core.text.CharPool.LF;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/10 星期三 10:16
 */
@Component
@Slf4j
public class USBEventListener implements UsbServicesListener {

  // 日志文件路径
  private static final String logFile = "logs\\usb_log.txt";

  private UsbServices usbServices;

  private UsbHub usbHub;

  @Autowired
  private DiskData diskData;

  public USBEventListener() {

    try {
      usbServices = UsbHostManager.getUsbServices();
      usbHub = usbServices.getRootUsbHub();
      // 遍历根USB Hub上的设备
      for (UsbDevice device : (List<UsbDevice>) usbHub.getAttachedUsbDevices()) {
        printDeviceInfo(device);
      }
      usbServices.addUsbServicesListener(this);
      createLog();
      log.info("添加USB监听成功");
    } catch (UsbException e) {
      e.printStackTrace();
    }
  }

  // 创建一个抛出异常的合并函数，用于处理键冲突
  private static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key %s", u));
    };
  }

  private static String deviceId(UsbDevice usbDevice) {
    String id = usbDevice.toString();
    log.info("设备ID：{}", id);
    return id;
  }

  private static void printDeviceInfo(UsbDevice device) {
    log.info("设备: " + device.toString());
    // 如果是USB Hub，则递归遍历其上的设备
    if (device instanceof UsbHub) {
      UsbHub hub = (UsbHub) device;
      for (UsbDevice childDevice : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
        printDeviceInfo(childDevice);
      }
    }
  }

  private static void createLog() {
    File file = new File(logFile);
    try {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      // 使用createNewFile()方法创建文件
      if (file.createNewFile()) {
        log.info("日志文件已成功创建: " + file.getName());
      } else {
        log.info("日志文件已存在: " + file.getName());
      }
    } catch (IOException e) {
      log.info("创建日志文件时发生错误: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // 将事件及相关信息写入日志文件
  private static void writeLog(String time, String logFile, String usbInfo, String event, String fileContent) {
    try {
      log(time, logFile, usbInfo, event, fileContent);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }

  }

  static void log(String time, String logFile, String usbInfo, String event, String fileContent) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
    writer.write(time + " =>>>>事件>>>>>>>>>>>:" + event + System.lineSeparator());
    if (null != usbInfo) {
      writer.write(time + " =>>>>设备ID>>>>>>>>>:" + usbInfo + System.lineSeparator());
    }
    if (null != fileContent) {
      writer.write(time + " =>>>>扫描到的文件>>>:\n" + fileContent + System.lineSeparator() + System.lineSeparator());
    }
    writer.append(LF);
    writer.close();
  }

  static StringBuffer content(Map<String, List<FileInfo>> data) {
    StringBuffer info = new StringBuffer();
    Set set = data.keySet();
    Iterator it = set.iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      List<FileInfo> list = data.get(key);
      for (FileInfo fileInfo : list) {
        String url = fileInfo.getUrl();
        info.append(key + " : " + url + System.lineSeparator());
      }
    }
    return info;
  }

  @Override
  public void usbDeviceAttached(UsbServicesEvent usbServicesEvent) {
    String motherboardSN = HardWareUtils.getMotherboardSN();
    log.info("{} USB 插入事件", motherboardSN);
    UsbDevice usbDevice = usbServicesEvent.getUsbDevice();
    String usbId = deviceId(usbDevice);
    Map<String, List<FileInfo>> mdf = diskData.findMDF();
    // 将事件及相关信息写入日志文件
    writeLog(DateUtil.format(new Date(), NORM_DATETIME_MS_PATTERN), logFile, usbId, motherboardSN + " USB 插入事件",
      content(mdf).toString());

    String maxKey = FindMaxKeyInMap.findMaxKey(mdf);
    List<FileInfo> fileInfos = mdf.get(maxKey);
    if (CollectionUtils.isEmpty(fileInfos)) {
      log.info("未检索到文件");
    } else {
      for (FileInfo fileInfo : fileInfos) {
        log.info(fileInfo.getUrl());
      }
    }
  }

  @Override
  public void usbDeviceDetached(UsbServicesEvent usbServicesEvent) {
    log.info("USB 拔出事件");
    String id = deviceId(usbServicesEvent.getUsbDevice());
    writeLog(DateUtil.format(new Date(), NORM_DATETIME_MS_PATTERN), logFile, id, "USB 拔出事件", null);
  }

  public Map<String, List<FileInfo>> find() {
    Map<String, List<FileInfo>> mdf = diskData.findMDF();
    if (null == mdf || mdf.isEmpty()) {
      log.info("无可用文件");
      return null;
    }
    // 将事件及相关信息写入日志文件
    writeLog(DateUtil.format(new Date(), NORM_DATETIME_MS_PATTERN), logFile, null, "手动查找", content(mdf).toString());
    log.info("找到最新的文件");

    String maxKey = FindMaxKeyInMap.findMaxKey(mdf);
    List<FileInfo> fileInfos = mdf.get(maxKey);
    for (FileInfo fileInfo : fileInfos) {
      log.info(fileInfo.getUrl());
    }
    mdf.clear();
    mdf.put(maxKey, fileInfos);
    return mdf;
  }
}
