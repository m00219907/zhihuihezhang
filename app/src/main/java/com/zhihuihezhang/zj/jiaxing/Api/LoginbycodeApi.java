package com.zhihuihezhang.zj.jiaxing.Api;

import android.content.Context;

import com.zhihuihezhang.zj.jiaxing.Constant;
import com.zhihuihezhang.zj.jiaxing.net.BaseBean;
import com.zhihuihezhang.zj.jiaxing.net.CallBack;
import com.zhihuihezhang.zj.jiaxing.net.HttpMap;
import com.zhihuihezhang.zj.jiaxing.net.YRequest;

import java.util.HashMap;

public class LoginbycodeApi {

    public void loginbycode(final Context context, final String authcode, CallBack callBack) {
        HashMap<String, String> map = HttpMap.getMap(new HttpMap.Action() {
            @Override
            public void addParams(HashMap<String, String> map) {
                map.put("authcode", authcode);
            }
        });

        YRequest.getInstance().post(context, Constant.BaseAddr + "loginauth.php", UserByCodeBean.class, map, callBack);
    }

    public static class UserByCodeBean extends BaseBean {
        private String userid;
        private String username;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
