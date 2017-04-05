package com.zhihuihezhang.zj.jiaxing;

import android.os.Environment;

public class Constant {
    public static boolean isLogin = false;
    public static boolean isLogByCode = false;
    public static boolean bReloadUrl = false;

    public static String userid = "";
    public static String username = "";
    public static String userfullname = "";
    public static String usermobile = "";
    public static String usergroup = "";
    public static String videorights = "";
    public static String accessToken="";

    public static final String SHAREPREFERENCE_FILENAME = "zhihuihezhng2017";// sharepreference

    public static  String BaseAddr = "http://jiaxing.zhihuihezhang.com/m/";
    public static String appFolder =  Environment.getExternalStorageDirectory().getPath() + "/zhihuihezhang";
}
