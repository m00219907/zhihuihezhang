package com.zhihuihezhang.zj.jiaxing.net;

import java.util.HashMap;
import java.util.Map;


public class HttpMap {
    public enum MapType {
        NORMAL, //标准类型，不对参数做任何处理
        SIGN    //对请求map进行签名（预留）
    }

    /**
     * 根据MapType生成相应的HttMap
     * @param mapType   HttpMap类型
     * @param action    HttpMap操作
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String,String> getMap(MapType mapType,Action action) {
        HashMap<String,String> map = new HashMap<>();
        if(action != null)
            action.addParams(map);
        switch (mapType) {
            case NORMAL:
                break;
            case SIGN:
                break;
        }

        return map;
    }

    /**
     * 生成默认类型STANDARD的HttpMap
     * @param action    HttpMap操作
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String,String> getMap(Action action) {
        return getMap(MapType.NORMAL,action);
    }

    /**
     * 根据MapType生成HttpMap,适用于不带请求参数的情况
     * @param mapType   HttpMap类型
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String,String> getMap(MapType mapType) {
        return getMap(mapType,null);
    }

    /**
     * 生成默认类型STANDARD的HttpMap,适用于不带请求参数的情况
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String,String> getMap() {
        return getMap(MapType.NORMAL,null);
    }



    /**
     * 对HttpMap进行编辑的操作
     */
    public interface Action {
        void addParams(HashMap<String,String> map);
    }
}
