package com.kidd.joy.service.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class PromoModel {

    //id
    private Integer id;

    //活动名称
    private String promoName;

    //秒杀活动状态 1表示未开始 2 表示进行中 3表示已结束
    private Integer status;

    //开始时间
    private DateTime startDate;

    //结束时间
    private DateTime endDate;

    //商品Id
    private Integer itemId;

    //活动时商品单价
    private BigDecimal promoItemPrice;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPromoItemPrice() {
        return promoItemPrice;
    }

    public void setPromoItemPrice(BigDecimal promoItemPrice) {
        this.promoItemPrice = promoItemPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
