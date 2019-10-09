package com.kidd.joy.dao;

import com.kidd.joy.dataobject.OrderInfoDO;

public interface OrderInfoDOMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderInfoDO record);

    int insertSelective(OrderInfoDO record);

    OrderInfoDO selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderInfoDO record);

    int updateByPrimaryKey(OrderInfoDO record);
}