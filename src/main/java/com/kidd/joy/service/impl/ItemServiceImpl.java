package com.kidd.joy.service.impl;

import com.kidd.joy.dao.ItemDOMapper;
import com.kidd.joy.dao.ItemStockDOMapper;
import com.kidd.joy.dataobject.ItemDO;
import com.kidd.joy.dataobject.ItemStockDO;
import com.kidd.joy.error.BusinessException;
import com.kidd.joy.error.EmBusinessError;
import com.kidd.joy.service.ItemService;
import com.kidd.joy.service.PromoService;
import com.kidd.joy.service.model.ItemModel;
import com.kidd.joy.service.model.PromoModel;
import com.kidd.joy.validator.ValidationResult;
import com.kidd.joy.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemDOMapper itemDOMapper;

    @Autowired
    ItemStockDOMapper itemStockDOMapper;

    @Autowired
    PromoService promoService;

    @Autowired
    ValidatorImpl validator;

    @Override
    @Transactional
    public ItemModel create(ItemModel itemModel) throws BusinessException {

        //校验入参
        ValidationResult validationResult = validator.validate(itemModel);
        if (validationResult.isHasErorr()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrorMsg());
        }

        //转化model为DO
        ItemDO itemDO = convertItemDOFromModel(itemModel);
        itemDO.setPrice(itemModel.getPrice().doubleValue());

        //存入数据库
        itemDOMapper.insertSelective(itemDO);

        ItemStockDO itemStockDO = convertStockDOFromModel(itemModel);
        itemStockDO.setItemId(itemDO.getId());

        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建完成的对象
        return itemModel;
    }

    private ItemStockDO convertStockDOFromModel(ItemModel itemModel) {
        ItemStockDO itemStockDO = new ItemStockDO();
        BeanUtils.copyProperties(itemModel, itemStockDO);
        return itemStockDO;
    }

    private ItemDO convertItemDOFromModel(ItemModel itemModel) {
        if (itemModel == null){
            return null;
        }

        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        return itemDO;
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> list = itemDOMapper.listItem();
        if (list == null || list.size() == 0){
            return null;
        }

        List<ItemModel> itemModels
                = list.stream().map(
                        itemDO -> {
                            ItemModel itemModel = convertDOToModel(itemDO);
                            if (itemModel == null){
                                return null;
                            }
                            ItemStockDO itemStockDO
                                    = itemStockDOMapper.selectByItemId(itemDO.getId());
                            itemModel.setStock(itemStockDO.getStock());
                            return itemModel;
                        }
        ).collect(Collectors.toList());
        return itemModels;
    }

    private ItemModel convertDOToModel(ItemDO itemDO) {
        if (itemDO == null){
            return null;
        }

        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        return itemModel;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);

        if (itemDO == null){
            return null;
        }

        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(id);

        ItemModel itemModel = convertToModel(itemDO, itemStockDO);

        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());

        if (promoModel != null && promoModel.getStatus() != 3){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    public boolean increaseSales(Integer itemId, Integer amount) {
        int affectedRow = itemDOMapper.increaseSales(itemId, amount);
        return affectedRow > 0;
    }


    private ItemModel convertToModel(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        if (itemStockDO != null){
            itemModel.setStock(itemStockDO.getStock());
        }
        return itemModel;
    }
}
