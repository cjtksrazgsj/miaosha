package com.kidd.joy.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {

    //校验结果是否有错
    private boolean hasErorr = false;

    //存放错误的信息
    private Map<String, String> errorMsgMap = new HashMap<>();

    public boolean isHasErorr() {
        return hasErorr;
    }

    public void setHasErorr(boolean hasErorr) {
        this.hasErorr = hasErorr;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    //实现通过格式化字符串信息获得错误结果的msg方法
    public String getErrorMsg(){
        return StringUtils.join(errorMsgMap.values().toArray(), "，");
    }
}
