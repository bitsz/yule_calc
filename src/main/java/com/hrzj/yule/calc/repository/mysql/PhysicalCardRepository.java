package com.hrzj.yule.calc.repository.mysql;

import com.hrzj.yule.calc.pojo.entity.mysql.PhysicalCard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lifengquan
 */
@Repository
public interface PhysicalCardRepository extends CrudRepository<PhysicalCard, Long> {

  public PhysicalCard findPhysicalCardByPhysicalIdAndData(String card, String data);
}
