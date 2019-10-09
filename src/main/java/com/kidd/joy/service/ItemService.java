package com.kidd.joy.service;

import com.kidd.joy.error.BusinessException;
import com.kidd.joy.service.model.ItemModel;

import java.util.List;

public interface ItemService {

    ItemModel create(ItemModel itemModel) throws BusinessException;

    List<ItemModel> listItem();

    ItemModel getItemById(Integer id);

    boolean increaseSales(Integer itemId, Integer amount);
}
