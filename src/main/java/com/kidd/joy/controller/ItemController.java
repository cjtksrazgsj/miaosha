package com.kidd.joy.controller;

import com.kidd.joy.controller.viewobject.ItemVO;
import com.kidd.joy.error.BusinessException;
import com.kidd.joy.response.CommonReturnType;
import com.kidd.joy.service.ItemService;
import com.kidd.joy.service.model.ItemModel;
import com.kidd.joy.service.model.PromoModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("item")
public class ItemController extends BaseController{

    @Autowired
    ItemService itemService;

    @RequestMapping(value = "create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType create(
            @RequestParam String title,
            @RequestParam BigDecimal price,
            @RequestParam Integer stock,
            @RequestParam(required = false) String description,
            @RequestParam String imgUrl
            ) throws BusinessException {
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);

        ItemModel ret = itemService.create(itemModel);
        return CommonReturnType.create(ret);
    }

    @RequestMapping(value = "listItem", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem(
    ){

        List<ItemModel> items = itemService.listItem();
        if (items == null || items.size() == 0){
            return CommonReturnType.create(null);
        }

        List<ItemVO> itemVOS =items.stream().map(
            itemModel -> {
                ItemVO itemVO = convertModelToVo(itemModel);
                return itemVO;
            }
        ).collect(Collectors.toList());
        return CommonReturnType.create(itemVOS);
    }

    @RequestMapping(value = "get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType get(
            @RequestParam Integer id
    ){

        ItemModel itemModel = itemService.getItemById(id);
        if (itemModel == null ){
            return CommonReturnType.create(null);
        }
        ItemVO itemVO = convertModelToVo(itemModel);
        return CommonReturnType.create(itemVO);
    }


    private ItemVO convertModelToVo(ItemModel itemModel) {
        if (itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        PromoModel promoModel = itemModel.getPromoModel();
        if (promoModel != null){
            itemVO.setPromoStatus(promoModel.getStatus());
            itemVO.setPromoId(promoModel.getId());
            itemVO.setPromoPrice(promoModel.getPromoItemPrice());
            itemVO.setStartDate(promoModel.getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }

}
