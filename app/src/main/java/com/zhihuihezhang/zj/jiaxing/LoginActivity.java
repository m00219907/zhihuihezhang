package com.zhihuihezhang.zj.jiaxing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuihezhang.zj.jiaxing.Api.LoginApi;
import com.zhihuihezhang.zj.jiaxing.Api.LoginbycodeApi;
import com.zhihuihezhang.zj.jiaxing.net.CallBack;

public class LoginActivity extends Activity implements View.OnClickListener {

    EditText river_fragment_user, river_fragment_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        river_fragment_user = (EditText) findViewById(R.id.river_fragment_user);
        river_fragment_password = (EditText) findViewById(R.id.river_fragment_password);

        findViewById(R.id.login_activity_login).setOnClickListener(this);
        findViewById(R.id.login_activity_logwithcode).setOnClickListener(this);
        findViewById(R.id.login_activity_management).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_activity_login:
                login();
                break;
            case R.id.login_activity_logwithcode:
                DialogShow.dialogShow2(this, "请输入8位委托码", "登录", new DialogShow.IloginClick() {
                    @Override
                    public void OnClick(String passcode) {
                        loginbycode(passcode);
                    }
                });
                break;
        }
    }

    public void login() {
        final String username = river_fragment_user.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final String userpassword = river_fragment_password.getText().toString();
        if (userpassword.isEmpty()) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoginApi().login(this, username, userpassword, new CallBack<LoginApi.UserInfoBean>(this) {
            @Override
            public void onResultOk(LoginApi.UserInfoBean result) {
                super.onResultOk(result);
                Constant.userid = result.getUserid();
                Constant.username = result.getUsername();
                Constant.userfullname = result.getUserfullname();
                Constant.usergroup = result.getUsergroup();
            }

            @Override
            public void onNull() {
                super.onNull();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginbycode(String safecode) {
        if (safecode.isEmpty()) {
            Toast.makeText(this, "委托码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoginbycodeApi().loginbycode(this, safecode, new CallBack<LoginbycodeApi.UserByCodeBean>(this) {
            @Override
            public void onResultOk(LoginbycodeApi.UserByCodeBean result) {
                super.onResultOk(result);
                Constant.userid = result.getUserid();
                Constant.username = result.getUsername();
            }

            @Override
            public void onNull() {
                super.onNull();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}