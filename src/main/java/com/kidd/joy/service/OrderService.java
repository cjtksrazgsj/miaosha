package com.kidd.joy.service;

import com.kidd.joy.error.BusinessException;
import com.kidd.joy.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId,  Integer amount) throws BusinessException;
}
