package com.zhihuihezhang.zj.jiaxing.video.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.company.PlaySDK.IPlaySDK;
import com.dh.DpsdkCore.Get_RecordStream_File_Info_t;
import com.dh.DpsdkCore.Get_RecordStream_Time_Info_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Query_Record_Info_t;
import com.dh.DpsdkCore.Record_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.fMediaDataCallback;
import com.zhihuihezhang.zj.jiaxing.AppApplication;
import com.zhihuihezhang.zj.jiaxing.R;
import com.zhihuihezhang.zj.jiaxing.video.Player.Err;
import com.zhihuihezhang.zj.jiaxing.video.playback.controlbar.PlayBackControlBar;
import com.zhihuihezhang.zj.jiaxing.video.playback.controlbar.RemoteControlBar;
import com.zhihuihezhang.zj.jiaxing.video.playback.controlbar.RemotePortaitControlBar;

public class BackPlayActivity extends Activity{
    //根view
    private LinearLayout mRootView;
    private TextView tv_ret;
    private TextView et_cam_id;
    private Button bt_query_video;
    private Button bt_playback_file;
    private Button bt_playback_time;
    private Button bt_playback_close;
    //抓图按钮
    private Button btnCaptureImg;
    public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/snapshot/";
    public final static String PHOTO_END  = ".jpg";
    
    private TextView chooseStartTxt;
    private TextView chooseEndTxt;
    private Button chooseStartBtn;
    private Button chooseEndBtn;
    private DatePicker dp;
    private TimePicker tp;
    private Calendar startCalendar;
    private Calendar endCalendar;
    
    Get_RecordStream_File_Info_t m_RecordFileInfo = new Get_RecordStream_File_Info_t();
    Return_Value_Info_t m_nPlaybackSeq = new Return_Value_Info_t();
    SurfaceView m_svPlayer = null;
    private int m_nRecordSource = 3;
    private int m_pDLLHandle = 0;
    private int m_nPort = 0;
    
    //媒体数据回调
    private fMediaDataCallback fm;
    private String channelId;
    private static final int PicFormat_JPEG = 1;
    private RemotePortaitControlBar timeBar;
    private PlayBackControlBar mPortCtrl;
    private Record_Info_t records;
    private static final int DEVICERECORD = 2;
    private static final int PLATFORMRECORD = 3;
    //保存时间控件临时变量
    private Calendar tempCalendar;
    private int nTimeOut = 30*1000;
    private static final String TAG = "BackPlayActivity";
    private Query_Record_Info_t queryRecordInfo;
    private static final int STATE_STOP = 1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_playback);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        tempCalendar = Calendar.getInstance();
        m_pDLLHandle = AppApplication.get().getDpsdkHandle();
        mContext = this.getApplicationContext();
         // 查找控件
        findViews();
        // 设置监听器
        setListener();
        // 设置默认时间 
        setTextDate();
        makeSpinner();
        initCtrl();
        
        et_cam_id.setText(getIntent().getStringExtra("channelName"));
        channelId = getIntent().getStringExtra("channelId");
        m_nPort = IPlaySDK.PLAYGetFreePort();
        SurfaceHolder holder = m_svPlayer.getHolder();
        holder.addCallback(new Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                    IPlaySDK.InitSurface(m_nPort, m_svPlayer);
             }
             
             public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
             }

             public void surfaceDestroyed(SurfaceHolder holder) {
             }
        });
        fm = new fMediaDataCallback() {
                
                @Override
                public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
                        byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {
                     IPlaySDK.PLAYInputData(m_nPort, szData, nDataLen);
                }
            };
    }

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case STATE_STOP:
                Calendar seekCalendar = Calendar.getInstance();
                Calendar tempCalendar = Calendar.getInstance();
                if(RemoteControlBar.scrollStopCalendar == null) {
                    seekCalendar = startCalendar;
                } else {
                    seekCalendar = RemoteControlBar.scrollStopCalendar;
                    tempCalendar.set(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), 
                            seekCalendar.get(Calendar.HOUR_OF_DAY), seekCalendar.get(Calendar.MINUTE), seekCalendar.get(Calendar.SECOND)); 
                }
                int videoNum = getVideoNumByTime(tempCalendar);
                try {
                    m_RecordFileInfo.szCameraId = channelId.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                    m_RecordFileInfo.nFileIndex = records.pSingleRecord[videoNum].nFileIndex;
                    m_RecordFileInfo.nMode = 1;
                    m_RecordFileInfo.nRight = 0;
                    m_RecordFileInfo.uBeginTime = records.pSingleRecord[videoNum].uBeginTime;
                    m_RecordFileInfo.uEndTime = records.pSingleRecord[videoNum].uEndTime;
                playBackByFile();
                break;
            default:
                break;
            }
        }
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        setTextDate();
    }

    private void findViews(){
        mRootView = (LinearLayout)findViewById(R.id.main_fragment_playback);
        timeBar = (RemotePortaitControlBar)findViewById(R.id.portait_control);
        tv_ret = (TextView)findViewById(R.id.tv_excute_result);
        et_cam_id = (TextView)findViewById(R.id.et_cam_id);
        bt_query_video = (Button)findViewById(R.id.bt_query_video);
        bt_playback_file = (Button)findViewById(R.id.bt_playback_by_file);
        bt_playback_time = (Button)findViewById(R.id.bt_playback_by_time); 
        bt_playback_close = (Button)findViewById(R.id.bt_close_playback); 
        //选择时间
        chooseEndTxt = (TextView)findViewById(R.id.choose_end_time_txt);
        chooseStartTxt = (TextView)findViewById(R.id.choose_start_time_txt);
        chooseEndBtn = (Button)findViewById(R.id.choose_end_time_btn);
        chooseStartBtn = (Button)findViewById(R.id.choose_start_time_btn);
        m_svPlayer = (SurfaceView)findViewById(R.id.sv_player);
        btnCaptureImg = (Button)findViewById(R.id.capture_bitmap);
    }
    private void setTextDate(){
        Calendar c = Calendar.getInstance();
        chooseStartTxt.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" + String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
                + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "日" + "00"+ "时" + "00" + "分");
        chooseEndTxt.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" + String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
                + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "日" + "23"+ "时" + "59" + "分");
        
        startCalendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        endCalendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                23, 59, 0);
        
    }
    /**
     * 初始化视图
     */
    private void initCtrl() {
        mPortCtrl = (PlayBackControlBar)findViewById(R.id.portait_control);
    }

    
    class chooseTimeListener implements OnClickListener{
        @Override
        public void onClick(final View v) {
            
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View vPopWindow =  inflater.inflate(R.layout.popwindow_choose_time, null, false);
            final PopupWindow popWindow = new PopupWindow(vPopWindow, android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            dp = (DatePicker)vPopWindow.findViewById(R.id.datePicker);
            tp = (TimePicker)vPopWindow.findViewById(R.id.timePicker);
            dp.setMaxDate(tempCalendar.getTimeInMillis());
          
            vPopWindow.findViewById(R.id.finish_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    
                    String year = String.valueOf(dp.getYear());
                    String month = String.valueOf(dp.getMonth() + 1);
                    String dayofmonth = String.valueOf(dp.getDayOfMonth());
                    String hour = String.valueOf(tp.getCurrentHour());
                    String minute = String.valueOf(tp.getCurrentMinute());
                    
                    //判断点击的是开始还是结束按钮
                    if(v.getId() == chooseStartBtn.getId()){
                        chooseStartTxt.setText(year + "年" + month + "月" + dayofmonth + "日" + hour + "时" + minute + "分");
                        startCalendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(),
                                tp.getCurrentHour(), tp.getCurrentMinute(), 0);
                    }else{
                        chooseEndTxt.setText(year + "年" + month + "月" + dayofmonth + "日" + hour + "时" + minute + "分");
                        endCalendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(),
                                    tp.getCurrentHour(), tp.getCurrentMinute(), 0);
                    }
                    tempCalendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(),
                            tp.getCurrentHour(), tp.getCurrentMinute(), 0);
                    popWindow.dismiss();
                }
            });
            popWindow.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
        }
        
    }
    
    private void setListener(){
        
        final Calendar c = Calendar.getInstance();
        //c.set(year, month, day, hourOfDay, minute, second)
        //选择日期控件
        chooseStartBtn.setOnClickListener(new chooseTimeListener());
        chooseEndBtn.setOnClickListener(new chooseTimeListener());
        btnCaptureImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                captureBitmap();
            }
        });
        
        bt_query_video.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    queryVideo(startCalendar);
                }
            });
            
        bt_playback_file.setOnClickListener(new Button.OnClickListener() {
                
                 @Override
                 public void onClick(View arg0) {
                     playBackByFile();
            
                 }
             });        

            bt_playback_time.setOnClickListener(new Button.OnClickListener() {
                
                 @Override
                 public void onClick(View arg0) {
                     playBackByTime(startCalendar);
                 }
             });  

            
            bt_playback_close.setOnClickListener(new Button.OnClickListener() {
                
                 @Override
                 public void onClick(View arg0) {
                     closeVideo();    
                 }
             }); 
        
    }

    private void closeVideo() {
        
        int nPlaybackReq = m_nPlaybackSeq.nReturnValue;
        int ret = IDpsdkCore.DPSDK_CloseRecordStreamBySeq(m_pDLLHandle, nPlaybackReq, nTimeOut);
        if(ret == 0){
            Log.e("xss","DPSDK_CloseRecordStreamBySeq success!");
        }else{
            Log.e("xss","DPSDK_CloseRecordStreamBySeq failed!");
        }
        StopRealPlay();    
    }
    private int getVideoNumByTime(Calendar calendar) {
        int num = 0;
        for(int i = 0; i < records.nCount; i ++){
            long beginTime = records.pSingleRecord[i].uBeginTime * 1000;
            long endTime = records.pSingleRecord[i].uEndTime * 1000;

            Calendar cBeCalendar = Calendar.getInstance();
            cBeCalendar.setTimeInMillis(beginTime);
            Log.i(TAG, "cBeCalendar" + cBeCalendar.get(Calendar.YEAR) + cBeCalendar.get(Calendar.MONTH)+ cBeCalendar.get(Calendar.DAY_OF_MONTH) +
                     "hour" + cBeCalendar.get(Calendar.HOUR_OF_DAY) + "minute" + cBeCalendar.get(Calendar.MINUTE) + "second" + cBeCalendar.get(Calendar.SECOND));
            Calendar cEndCalendar = Calendar.getInstance();
            cEndCalendar.setTimeInMillis(endTime);
            Log.i(TAG, "cEndCalendar" + cEndCalendar.get(Calendar.YEAR) + cEndCalendar.get(Calendar.MONTH)+ cEndCalendar.get(Calendar.DAY_OF_MONTH) +
                     "hour" + cEndCalendar.get(Calendar.HOUR_OF_DAY) + "minute" + cEndCalendar.get(Calendar.MINUTE) + "second" + cEndCalendar.get(Calendar.SECOND));
            if(beginTime < calendar.getTimeInMillis() && calendar.getTimeInMillis() < endTime) {
                num = i;
             } 
        }
        
        Log.i(TAG, "tempCalendar" + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH)+ calendar.get(Calendar.DAY_OF_MONTH) +
                 "hour" + calendar.get(Calendar.HOUR_OF_DAY) + "minute" + calendar.get(Calendar.MINUTE) + "second" + calendar.get(Calendar.SECOND) +
                 "time in millis" + calendar.getTimeInMillis());
        return num;
    }
    private void queryVideo(Calendar startCalendar) {
        
        byte[] szCameraId = null;
        try {
            szCameraId = channelId.getBytes("utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        int nRet = 0;
        String strRet = "";
        Return_Value_Info_t nRecordCount = new Return_Value_Info_t();
        queryRecordInfo = new Query_Record_Info_t();
        System.arraycopy(szCameraId, 0, queryRecordInfo.szCameraId, 0, szCameraId.length);
        queryRecordInfo.nRight = 0;
        queryRecordInfo.nRecordType = 0;
        queryRecordInfo.nSource = m_nRecordSource;
        if(startCalendar != null){
            queryRecordInfo.uBeginTime = startCalendar.getTimeInMillis()/1000;
        }
        if(endCalendar != null){
            queryRecordInfo.uEndTime = endCalendar.getTimeInMillis()/1000;
        }

        if (startCalendar.compareTo(endCalendar) == 1) {
            Log.e("BackPlayActivity", "please reset time");
        } else {
            nRet = IDpsdkCore.DPSDK_QueryRecord(m_pDLLHandle, queryRecordInfo, nRecordCount, nTimeOut);
            int nCount = nRecordCount.nReturnValue;
            
            if (nRet == 0) {
                records = new Record_Info_t(nCount);
                try {
                    records.szCameraId = channelId.getBytes("utf-8");
                    m_RecordFileInfo.szCameraId = channelId.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                records.nBegin = 0;
                IDpsdkCore.DPSDK_GetRecordInfo(m_pDLLHandle, records);
                if(records.nCount > 0) {
                    m_RecordFileInfo.nFileIndex = records.pSingleRecord[0].nFileIndex;
                    m_RecordFileInfo.nMode = 1;
                    m_RecordFileInfo.nRight = 0;
                    m_RecordFileInfo.uBeginTime = records.pSingleRecord[0].uBeginTime;
                    m_RecordFileInfo.uEndTime = records.pSingleRecord[0].uEndTime;
                }
            }
            
            //设置有录像的时间段为绿色   
            ArrayList<Pair<Calendar, Calendar>> timeArray = getTimeSlices(records);
            setTimeSlices(timeArray);
        }
        
    }
    
   public ArrayList<Pair<Calendar, Calendar>> getTimeSlices(Record_Info_t records) {

        if (records == null) {
            return null;
        }

        int retCount = records.nCount;//  records.nRetCount;
        ArrayList<Pair<Calendar, Calendar>> result = new ArrayList<Pair<Calendar, Calendar>>();

        for (int i = 0; i < retCount; i++) {
            long begin = records.pSingleRecord[i].uBeginTime;
            long end = records.pSingleRecord[i].uEndTime;
            Calendar bc = Millis2Calendar(begin * 1000);
            Calendar ec = Millis2Calendar(end * 1000);
            result.add(new Pair<Calendar, Calendar>(bc, ec));
        }

        return result;
    }

   protected void setTimeSlices(ArrayList<Pair<Calendar, Calendar>> timeArray) {
        if (timeArray != null) {
            mPortCtrl.setTimeSlices(timeArray);
        }
    }

   private Calendar Millis2Calendar(long millis) {
       Calendar caledar = Calendar.getInstance();
       caledar.setTimeInMillis(millis);
       return caledar;
   }

    private void playBackByFile() {
        
        if(!StartRealPlay()){
            return;
        }
        int nRet = IDpsdkCore.DPSDK_GetRecordStreamByFile(m_pDLLHandle, m_nPlaybackSeq, m_RecordFileInfo, fm, 20*1000);
        if (nRet == 0) {
            Log.e("xss","DPSDK_GetRecordStreamByFile success!");
        } else {
            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
            Log.e("xss","DPSDK_GetRecordStreamByFile failed!");
        }
    }
    
    private void playBackByTime(Calendar calendar) {
        
        if(!StartRealPlay()){
            Log.e("xss", "StartRealPlay failed!");
            return;
        }
        int nRet = 0;
            Get_RecordStream_Time_Info_t getRecordStreamTimeInfo = new Get_RecordStream_Time_Info_t();
            try {
            getRecordStreamTimeInfo.szCameraId = channelId.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            getRecordStreamTimeInfo.nMode = 1;
            getRecordStreamTimeInfo.nRight = 0;
            getRecordStreamTimeInfo.nSource = m_nRecordSource;
            getRecordStreamTimeInfo.uBeginTime = calendar.getTimeInMillis()/1000;
            getRecordStreamTimeInfo.uEndTime = endCalendar.getTimeInMillis()/1000;
            nRet = IDpsdkCore.DPSDK_GetRecordStreamByTime(m_pDLLHandle, m_nPlaybackSeq, getRecordStreamTimeInfo, fm, 40*1000);
            
        if(nRet == 0){
            Log.e("xss","DPSDK_GetRecordStreamByTime success!");
        }else{
            Log.e("xss","DPSDK_GetRecordStreamByTime failed!" + nRet);
        }    
    }

    private void captureBitmap() {
        
        String IMGSTR = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + PHOTO_END;
        String path = IMGSTR + IMAGE_PATH;
        //先创建一个文件夹
        File dir = new File(IMAGE_PATH);
        File file = new File(IMAGE_PATH, IMGSTR);
        if(!dir.exists()) {
            dir.mkdir();
        } else {
            if(file.exists()) {
                file.delete();
            }
        }
        
        int result = IPlaySDK.PLAYCatchPicEx(m_nPort, IMAGE_PATH, PicFormat_JPEG);
        if (result == Err.OK) {
            showToast(R.string.capture_success);
        } else {
            showToast(R.string.capture_fail);
        }
    }
    private void showToast(int str){
        Toast.makeText(getApplicationContext(), getResources().getString(str), Toast.LENGTH_SHORT).show();
    }

    public void StopRealPlay()
    {
        try {
            IPlaySDK.PLAYStopSoundShare(m_nPort);
            IPlaySDK.PLAYStop(m_nPort);          
            IPlaySDK.PLAYCloseStream(m_nPort);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean StartRealPlay()
    { 
        if(m_svPlayer == null)
            return false;
        
        boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPort,null,0,1500*1024) == 0? false : true;
        if(bOpenRet)
        {
            boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPort, m_svPlayer) == 0 ? false : true;
            if(bPlayRet)
            {
                boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPort) == 0 ? false : true;
                if(!bSuccess)
                {
                    IPlaySDK.PLAYStop(m_nPort);
                    IPlaySDK.PLAYCloseStream(m_nPort);
                    return false;
                }
            }
            else
            {
                IPlaySDK.PLAYCloseStream(m_nPort);
                return false;
            }
        }
        else
        {
            return false;
        }
        
        return true;
    }
    
    private void makeSpinner(){
        
        Resources res = getResources();
        String[] spType = res.getStringArray(R.array.video_type);
        String[] spSource = res.getStringArray(R.array.video_source);
        String[] spSize = res.getStringArray(R.array.video_size);
        ArrayAdapter<String> aaType = new ArrayAdapter<String>(getApplicationContext(), 
                android.R.layout.simple_spinner_item, spType);
        //改变字体颜色
        aaType.setDropDownViewResource(R.layout.spinner_item);
        
        ArrayAdapter<String> aaSource = new ArrayAdapter<String>(getApplicationContext(), 
                android.R.layout.simple_spinner_item, spSource);
        aaSource.setDropDownViewResource(R.layout.spinner_item);
        
        ArrayAdapter<String> aaSize = new ArrayAdapter<String>(getApplicationContext(), 
                android.R.layout.simple_spinner_item, spSize);
        aaSize.setDropDownViewResource(R.layout.spinner_item);
        
        Spinner spinnerType = (Spinner)findViewById(R.id.sp_video_type);
        Spinner spinnerSource = (Spinner)findViewById(R.id.sp_source);
        spinnerType.setAdapter(aaType);
        spinnerSource.setAdapter(aaSource);
        spinnerSource.setSelection(1,true);  //设置默认为平台录像
        spinnerSource.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(0 == arg2) {
                    m_nRecordSource = DEVICERECORD;   //设备录像
                } else {
                    m_nRecordSource = PLATFORMRECORD;    //平台录像
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                m_nRecordSource = PLATFORMRECORD;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("LOGININFO", 0);
        Editor ed = sp.edit();
        ed.putString("ISFINISH", "false");
        ed.commit();
    }

}
