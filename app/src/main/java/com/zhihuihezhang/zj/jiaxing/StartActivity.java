package com.zhihuihezhang.zj.jiaxing;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Login_Info_t;

import java.io.File;

public class StartActivity extends Activity{

    private AppApplication mAPP = AppApplication.get();
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 101:
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        mHandler.sendEmptyMessageDelayed(101, 3000);

        new LoginTask_guangdian().execute();
        new File(Constant.appFolder).mkdirs();
    }

    class LoginTask_guangdian extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... arg0) {
            Login_Info_t loginInfo = new Login_Info_t();
            loginInfo.szIp = "101.68.222.50".getBytes();
            String strPort = "9000";
            loginInfo.nPort = Integer.parseInt(strPort);
            loginInfo.szUsername = "zhhz_zjjx".getBytes();
            loginInfo.szPassword = "zhhz123456".getBytes();
            loginInfo.nProtocol = 2;
            return IDpsdkCore.DPSDK_Login(mAPP.getDpsdkCreatHandle(), loginInfo, 30000);
        }

        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            if (result == 0) {
                Log.d("DpsdkLogin success:", result + "");
                IDpsdkCore.DPSDK_SetCompressType(mAPP.getDpsdkCreatHandle(), 0);
                mAPP.setLoginHandler(1);
            } else {
                Log.d("DpsdkLogin failed:",result+"");
                Toast.makeText(getApplicationContext(), "login failed" + result, Toast.LENGTH_SHORT).show();
                mAPP.setLoginHandler(0);
            }
        }
    }
}
