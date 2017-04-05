package com.zhihuihezhang.zj.jiaxing;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihuihezhang.zj.jiaxing.Api.ChangePasswordApi;
import com.zhihuihezhang.zj.jiaxing.net.BaseBean;
import com.zhihuihezhang.zj.jiaxing.net.CallBack;


public class ChangePasswordActivity extends Activity implements View.OnClickListener {

    EditText change_pwd_input_oldpwd, change_pwd_input_newpwd;
    ImageView change_pwd_miwen1, change_pwd_miwen2;
    boolean bForce;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_password);
        bForce = getIntent().getBooleanExtra("bForce", false);

        change_pwd_input_oldpwd = (EditText)findViewById(R.id.change_pwd_input_oldpwd);
        change_pwd_input_newpwd = (EditText)findViewById(R.id.change_pwd_input_newpwd);
        change_pwd_miwen1 = (ImageView)findViewById(R.id.change_pwd_miwen1);
        change_pwd_miwen1.setTag("1");
        change_pwd_miwen1.setOnClickListener(this);
        change_pwd_miwen2 = (ImageView)findViewById(R.id.change_pwd_miwen2);
        change_pwd_miwen2.setTag("1");
        change_pwd_miwen2.setOnClickListener(this);
        findViewById(R.id.change_pwd_back).setOnClickListener(this);
        findViewById(R.id.change_pwd_commit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_pwd_back:
                if(bForce){
                    Toast.makeText(this, "必须修改密码!",Toast.LENGTH_SHORT).show();
                }else {
                    finish();
                }
                break;
            case R.id.change_pwd_commit:
                String oldpwd = change_pwd_input_oldpwd.getText().toString();
                String newpwd = change_pwd_input_newpwd.getText().toString();
                if(oldpwd.isEmpty()){
                    Toast.makeText(this, "旧密码不能为空!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newpwd.isEmpty()){
                    Toast.makeText(this, "新密码不能为空!",Toast.LENGTH_SHORT).show();
                    return;
                }
                String userpassword = SharePreferenceDataUtil.getSharedStringData(this, "userpassword");
                if(!userpassword.equals(oldpwd)){
                    Toast.makeText(this, "旧密码有误!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(oldpwd.equals(newpwd)){
                    Toast.makeText(this, "新旧密码相同!",Toast.LENGTH_SHORT).show();
                    return;
                }
                changepwd(oldpwd, newpwd);
                break;
            case R.id.change_pwd_miwen1:
                if(change_pwd_miwen1.getTag().equals("1")) {
                    change_pwd_miwen1.setImageResource(R.drawable.miwen);
                    change_pwd_miwen1.setTag("0");
                    change_pwd_input_oldpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    change_pwd_miwen1.setImageResource(R.drawable.mingwen);
                    change_pwd_miwen1.setTag("1");
                    change_pwd_input_oldpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                change_pwd_input_oldpwd.setSelection(change_pwd_input_oldpwd.getText().length());
                break;
            case R.id.change_pwd_miwen2:
                if(change_pwd_miwen2.getTag().equals("1")) {
                    change_pwd_miwen2.setImageResource(R.drawable.miwen);
                    change_pwd_miwen2.setTag("0");
                    change_pwd_input_newpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    change_pwd_miwen2.setImageResource(R.drawable.mingwen);
                    change_pwd_miwen2.setTag("1");
                    change_pwd_input_newpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                change_pwd_input_newpwd.setSelection(change_pwd_input_newpwd.getText().length());
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            if(bForce){
                Toast.makeText(this, "必须修改密码!",Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void changepwd(String oldpwd, final String newpwd) {

        new ChangePasswordApi().changePassword(this, oldpwd, newpwd, new CallBack<BaseBean>(this) {
            @Override
            public void onResultOk(BaseBean result) {
                super.onResultOk(result);
                Toast.makeText(ChangePasswordActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNull() {
                super.onNull();
                Toast.makeText(ChangePasswordActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
