package com.zhihuihezhang.zj.jiaxing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


public class DialogShow {

    public static void dialogShow1(Context context, boolean bForce, String updateInfo, String serverVersion, final ICheckedCallBack callBack) {

        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.dialog_update);
            View update_divide_line = window.findViewById(R.id.update_divide_line);
            View negative_text = window.findViewById(R.id.negative_text);
            View positive_text = window.findViewById(R.id.positive_text);
            TextView update_serverversion = (TextView) window.findViewById(R.id.update_serverversion);
            update_serverversion.setText("最新版本：" + serverVersion);
            TextView update_currentversion = (TextView) window.findViewById(R.id.update_currentversion);
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                if (info != null) {
                    update_currentversion.setText("当前版本：" + info.versionName);
                }
            } catch (Exception e) {
            }
            TextView message = (TextView) window.findViewById(R.id.message);
            message.setText(updateInfo);
            if(bForce){
                update_divide_line.setVisibility(View.GONE);
                negative_text.setVisibility(View.GONE);
            }

            negative_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            positive_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCheckedCallBackDispath(true);
                    alertDialog.dismiss();
                }
            });

        } catch (Exception e) {
        }
    }

    public static void dialogShow2(Context context, String tittle, String buttonText, final IloginClick callBack) {

        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            window.setContentView(R.layout.regin_click);
            final EditText login_code_edit = (EditText)window.findViewById(R.id.login_code_edit);
            TextView login_code_tittle = (TextView)window.findViewById(R.id.login_code_tittle);
            login_code_tittle.setText(tittle);
            TextView login_code_postive = (TextView)window.findViewById(R.id.login_code_postive);
            login_code_postive.setText(buttonText);
            View login_code_cancel = window.findViewById(R.id.login_code_cancel);
            login_code_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            login_code_postive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnClick(login_code_edit.getText().toString());
                    alertDialog.dismiss();
                }
            });

        } catch (Exception e) {
        }
    }


    public interface ICheckedCallBack {
        public void OnCheckedCallBackDispath(boolean bSucceed);
    }

    public interface IloginClick {
        public void OnClick(String passcode);
    }
}
