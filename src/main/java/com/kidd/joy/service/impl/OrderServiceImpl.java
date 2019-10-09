package com.kidd.joy.service.impl;

import com.kidd.joy.dao.ItemStockDOMapper;
import com.kidd.joy.dao.OrderInfoDOMapper;
import com.kidd.joy.dao.SequenceDOMapper;
import com.kidd.joy.dataobject.OrderInfoDO;
import com.kidd.joy.dataobject.SequenceDO;
import com.kidd.joy.error.BusinessException;
import com.kidd.joy.error.EmBusinessError;
import com.kidd.joy.service.ItemService;
import com.kidd.joy.service.OrderService;
import com.kidd.joy.service.UserService;
import com.kidd.joy.service.model.ItemModel;
import com.kidd.joy.service.model.OrderModel;
import com.kidd.joy.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private OrderInfoDOMapper orderInfoDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {

        //1.校验下单状态， 下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }

        UserModel userModel = userService.getUserById(userId);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户不存在");
        }

        if (amount <= 0 || amount> 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品数量不正确");
        }

        //校验活动信息
        if (promoId != null){
            //（1）校验活动是否存在于这个商品
            if (promoId != itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            } else if (itemModel.getPromoModel().getStatus() != 2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动并未开始");
            }
        }

        //2.落单减库存
        int decreaseRet = itemStockDOMapper.decreaseStock(itemId, amount);
        if (decreaseRet < 1){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH, "库存不足");
        }

        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setPromoId(promoId);
        if (promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));
        orderModel.setId(this.generateOrderNo());
            OrderInfoDO orderInfoDO = convertFromOrderModel(orderModel);
        orderInfoDOMapper.insertSelective(orderInfoDO);

        //4. 增加销量
        itemService.increaseSales(itemId, amount);

        //5. 返回前端
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo(){
        //订单号有16位
        //前8位为时间信息
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ISO_DATE).replaceAll("-", "");
        stringBuilder.append(dateStr);
        //中间6位为自增序列
        SequenceDO sequenceDO
                = sequenceDOMapper.getSequenceByName("order_info");

        int sequence = 0;
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i< 6 - sequenceStr.length(); i ++){
            stringBuilder.append("0");
        }
        stringBuilder.append(sequenceStr);
        //最后2位为分库表位
        stringBuilder.append("00");
        return stringBuilder.toString();
    }

    private OrderInfoDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderInfoDO orderInfoDO = new OrderInfoDO();
        BeanUtils.copyProperties(orderModel, orderInfoDO);
        orderInfoDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderInfoDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderInfoDO;
    }


}
