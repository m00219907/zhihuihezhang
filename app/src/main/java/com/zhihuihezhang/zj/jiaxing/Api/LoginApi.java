package com.zhihuihezhang.zj.jiaxing.Api;


import android.content.Context;

import com.zhihuihezhang.zj.jiaxing.CommonTools;
import com.zhihuihezhang.zj.jiaxing.Constant;
import com.zhihuihezhang.zj.jiaxing.net.BaseBean;
import com.zhihuihezhang.zj.jiaxing.net.CallBack;
import com.zhihuihezhang.zj.jiaxing.net.HttpMap;
import com.zhihuihezhang.zj.jiaxing.net.YRequest;

import java.util.HashMap;

public class LoginApi {

    public void login(final Context context, final String username, final String userpassword, CallBack callBack) {
        HashMap<String, String> map = HttpMap.getMap(new HttpMap.Action() {
            @Override
            public void addParams(HashMap<String, String> map) {
                map.put("username", username);
                map.put("userpassword", userpassword);
                map.put("from", android.os.Build.MODEL);
                map.put("deviceid", CommonTools.getMacAddress(context));
            }
        });

        YRequest.getInstance().post(context, Constant.BaseAddr + "login.php", UserInfoBean.class, map, callBack);
    }

    public static class UserInfoBean extends BaseBean{
        private String userid;
        private String username;
        private String userfullname;
        private String usermobile;
        private String usergroup;
        private String videorights;

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

        public String getUserfullname() {
            return userfullname;
        }

        public void setUserfullname(String userfullname) {
            this.userfullname = userfullname;
        }

        public String getUsermobile() {
            return usermobile;
        }

        public void setUsermobile(String usermobile) {
            this.usermobile = usermobile;
        }

        public String getUsergroup() {
            return usergroup;
        }

        public void setUsergroup(String usergroup) {
            this.usergroup = usergroup;
        }

        public String getVideorights() {
            return videorights;
        }

        public void setVideorights(String videorights) {
            this.videorights = videorights;
        }
    }
}
