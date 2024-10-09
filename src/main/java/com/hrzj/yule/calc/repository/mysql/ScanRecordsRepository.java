package com.hrzj.yule.calc.repository.mysql;

import com.hrzj.yule.calc.pojo.entity.mysql.ScanRecords;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lifengquan
 */
@Repository
public interface ScanRecordsRepository extends CrudRepository<ScanRecords, Long> {

  ScanRecords findByOrOrderId(String orderId);

  ScanRecords findByDirAndDay(String dir, String day);
}
