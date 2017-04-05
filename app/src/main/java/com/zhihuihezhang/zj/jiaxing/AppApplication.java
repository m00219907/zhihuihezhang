package com.zhihuihezhang.zj.jiaxing;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dh.DpsdkCore.Device_Info_Ex_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.InviteVtCallParam_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.RingInfo_t;
import com.dh.DpsdkCore.dpsdk_constant_value;
import com.dh.DpsdkCore.fDPSDKInviteVtCallParamCallBack;
import com.dh.DpsdkCore.fDPSDKRingInfoCallBack;
import com.dh.DpsdkCore.fDPSDKStatusCallback;
import com.zhihuihezhang.zj.jiaxing.video.activity.AutoVtActivity;
import com.zhihuihezhang.zj.jiaxing.video.groupTree.GroupListManager;

import java.io.File;
import java.util.List;

public class AppApplication extends Application {

    private static final String TAG = "AppApplication";

    private static AppApplication _instance;
    private int m_loginHandle = 0;
    private int m_nLastError = 0;
    private Return_Value_Info_t m_ReValue = new Return_Value_Info_t();

    public static synchronized AppApplication get() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        initApp();
    }

    public void initApp() {

        int nType = 1;
        m_nLastError = IDpsdkCore.DPSDK_Create(nType, m_ReValue);

        CommonTools.deleteDir(new File(Constant.appFolder+ "/dhsdk"));
        new File(Constant.appFolder+ "/dhsdk").mkdirs();
        IDpsdkCore.DPSDK_SetSaveGroupFilePath(m_ReValue.nReturnValue, (Constant.appFolder + "/dhsdk").getBytes());
        IDpsdkCore.DPSDK_SetDPSDKStatusCallback(m_ReValue.nReturnValue, new fDPSDKStatusCallback() {

            @Override
            public void invoke(int nPDLLHandle, int nStatus) {
                Log.v("fDPSDKStatusCallback", "nStatus = " + nStatus);
            }
        });

        IDpsdkCore.DPSDK_SetRingCallback(m_ReValue.nReturnValue, new fDPSDKRingInfoCallBack() {

            @Override
            public void invoke(int nPDLLHandle, RingInfo_t param) {
                Intent intent = new Intent(AppApplication.this, AutoVtActivity.class);
                Bundle bundle = new Bundle();
                bundle.putByteArray("szUserId", param.szUserId);
                bundle.putInt("callId", param.callId);
                bundle.putInt("dlgId", param.dlgId);
                bundle.putInt("tid", param.tid);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        IDpsdkCore.DPSDK_SetVtCallInviteCallback(m_ReValue.nReturnValue, new fDPSDKInviteVtCallParamCallBack() {

            @Override
            public void invoke(int nPDLLHandle, InviteVtCallParam_t param) {

                String strcallnum = new String(param.szUserId).trim();

                List<Device_Info_Ex_t> devlist = GroupListManager.getInstance().getDeviceExList();
                Device_Info_Ex_t deviceInfoEx;
                byte[] szId = new byte[dpsdk_constant_value.DPSDK_CORE_DEV_ID_LEN];
                String channelname = "";

                for(int i=0;i<devlist.size();i++){
                    deviceInfoEx = devlist.get(i);
                    String szCallNum = new String(deviceInfoEx.szCallNum).trim();
                    if(strcallnum.equals(szCallNum)){
                        byte[] bt = (new String(deviceInfoEx.szId).trim()+"$1$0$0").getBytes();
                        System.arraycopy(bt, 0, szId, 0, bt.length);
                        channelname = new String(szId).trim();
                    }
                }

                Intent intent = new Intent(AppApplication.this, AutoVtActivity.class);
                Bundle bundle = new Bundle();
                bundle.putByteArray("szUserId", param.szUserId);
                bundle.putInt("audioType", param.audioType);
                bundle.putInt("audioBit", param.audioBit);
                bundle.putInt("sampleRate", param.sampleRate);
                bundle.putByteArray("rtpServIP", param.rtpServIP);
                bundle.putInt("rtpAPort", param.rtpAPort);
                bundle.putInt("rtpVPort", param.rtpVPort);
                bundle.putInt("nCallType", param.nCallType);
                bundle.putInt("tid", param.tid);
                bundle.putInt("callId", param.callId);
                bundle.putInt("dlgId", param.dlgId);
                bundle.putByteArray("channelid", szId);
                bundle.putByteArray("channelname", szId);

                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }



    public int getDpsdkHandle(){
        if(m_loginHandle == 1)
            return m_ReValue.nReturnValue;
        else
            return 0;
    }

    public int getDpsdkCreatHandle(){  //浠呯敤浜庤幏鍙朌PSDK_login鐨勫彞鏌�
        return m_ReValue.nReturnValue;
    }

    public void setLoginHandler(int loginhandler){
        this.m_loginHandle = loginhandler;
    }

    public int getLoginHandler(){
        return this.m_loginHandle;
    }

}
