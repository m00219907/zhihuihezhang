package com.zhihuihezhang.zj.jiaxing.Api;

import android.content.Context;

import com.zhihuihezhang.zj.jiaxing.Constant;
import com.zhihuihezhang.zj.jiaxing.net.BaseBean;
import com.zhihuihezhang.zj.jiaxing.net.CallBack;
import com.zhihuihezhang.zj.jiaxing.net.HttpMap;
import com.zhihuihezhang.zj.jiaxing.net.YRequest;

import java.util.HashMap;

public class ChangePasswordApi {

    public void changePassword(final Context context, final String userpassword, final String userpasswordnew, CallBack callBack) {
        HashMap<String, String> map = HttpMap.getMap(new HttpMap.Action() {
            @Override
            public void addParams(HashMap<String, String> map) {
                map.put("userid", Constant.userid);
                map.put("userpassword", userpassword);
                map.put("userpasswordnew", userpasswordnew);
            }
        });

        YRequest.getInstance().post(context, Constant.BaseAddr + "userpwdchg.php", BaseBean.class, map, callBack);
    }

}
