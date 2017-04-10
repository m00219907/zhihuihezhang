package com.zhihuihezhang.zj.jiaxing.video.groupTree;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zhihuihezhang.zj.jiaxing.R;
import com.dh.DpsdkCore.IDpsdkCore;
import com.zhihuihezhang.zj.jiaxing.video.Demo.TestDpsdkCoreActivity;
import com.zhihuihezhang.zj.jiaxing.AppApplication;
import com.zhihuihezhang.zj.jiaxing.video.groupTree.bean.ChannelInfoExt;
import com.zhihuihezhang.zj.jiaxing.video.groupTree.bean.TreeNode;
import com.zhihuihezhang.zj.jiaxing.video.util.AppDefine;
import com.zhihuihezhang.zj.jiaxing.video.view.PullDownListView;


public class GroupListActivity extends Activity implements PullDownListView.OnRefreshListioner,SearchChannelsAdapter.IOnSearchChannelsClick{

    // 打印标签
    private static final String TAG = "GroupListActivity";

    // 组织树控件
    private ListView mGroupsLv;

    // 搜索播放控件
    private Button mConfirmBtn = null;

    // 搜索框adapter
    private GroupListAdapter mGroupListAdapter = null;

    // 获取实例
    private GroupListManager mGroupListManager = null;

    // 选中的nodes
    private List<TreeNode> selectNnodes = null;

    // 下拉刷新对象
    private PullDownListView mPullDownView = null;

    // 获取的树信息
    private TreeNode root = null;

    // 消息对象
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GROUPLIST_UPDATELIST:
                    // 处理更新列表
                    handleUpdateList();
                    break;
                case MSG_GROUP_TO_PLAYBACK:
                default:
                    break;
            }
        };
    };

    // 等待对话框
    private ProgressBar mWattingPb = null;

    // 从哪个页面过来 1： 从实时预览进入组织列表 2：从回放进入组织列:3: 从电子地图进入组织列表
    private int comeFrom = 0;
    
    /** 更新列表消息(const value:1000) */
    public static final int MSG_GROUPLIST_UPDATELIST = 1000;
    
    /** 点击进入回放消息 */
    public static final int MSG_GROUP_TO_PLAYBACK = 1005;

    private String deviceName;
    private String[] dialogList;
    private LinearLayout layLogout;
    private String mDeviceId;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_fragment);

        // 查找控件
        findViews();

        // 设置监听器
        setListener();

        // 初始化数据
        initDate();
    }
    
    private void findViews() {
        //layLogout= (LinearLayout)findViewById(R.id.title_lay);
    }

    private void setListener() {
        layLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int nPDLLHandle = AppApplication.get().getDpsdkHandle();
                int nRet = IDpsdkCore.DPSDK_Logout(nPDLLHandle, 30000);
                if(nRet == 0) {
                    Toast.makeText(GroupListActivity.this, getResources().getString(R.string.logout), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroupListActivity.this, getResources().getString(R.string.logout_fail), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), TestDpsdkCoreActivity.class);
                startActivity(intent);
                GroupListActivity.this.finish();
            }
        });
        // 下拉刷新
        mPullDownView.setRefreshListioner(this);
    }

    private void initDate() {
        mGroupsLv = mPullDownView.mListView;
        mGroupListManager = GroupListManager.getInstance();
        mGroupListAdapter = new GroupListAdapter(this);
        updateSelectChannels();
        
        mGroupsLv.setAdapter(mGroupListAdapter);
        getGroupList();

    }

    private void getGroupList() {
        root = mGroupListManager.getRootNode();
        if (root == null) {
            mWattingPb.setVisibility(View.VISIBLE);
        }

        if (mGroupListManager.getTask() != null) {
            mGroupListManager.setGroupListGetListener(mIOnSuccessListener);
        }
        if (mGroupListManager.isFinish() && root != null) {
            if (root.getChildren().size() == 0) {
                 mGroupListManager.startGroupListGetTask();
            }
            mHandler.sendEmptyMessage(MSG_GROUPLIST_UPDATELIST);
            return;
        } else if (root == null) {
            if (mGroupListManager.getTask() == null) {
                mGroupListManager.startGroupListGetTask();
                mGroupListManager.setGroupListGetListener(mIOnSuccessListener);
            }
        }

    }

    private void handleUpdateList() {
        root = mGroupListManager.getRootNode();
        mGroupListManager.setOnSuccessListener(mIOnSuccessListener);
        // 这里表示刷新处理完成后把上面的加载刷新界面隐藏
        mPullDownView.onRefreshComplete();
        if (mWattingPb != null) {
            mWattingPb.setVisibility(View.GONE);
        }
        mGroupListAdapter.clearDate();
        mGroupListAdapter.addNode(root);
        // 设置默认展开级别
        mGroupListAdapter.notifyDataSetChanged();
    }

    GroupListGetTask.IOnSuccessListener mIOnSuccessListener = new GroupListGetTask.IOnSuccessListener() {
        @Override
        public void onSuccess(final boolean success, final int errCode) {
            
            mHandler.post(new Runnable() {
                
                @Override
                public void run() {
                    // 清空任务
                    mGroupListManager.setTask(null);

                    if (mWattingPb != null) {
                        mWattingPb.setVisibility(View.GONE);
                    }
                    if (success) {
                        root = mGroupListManager.getRootNode();
                        if (root != null) {
                            mGroupListAdapter.clearDate();
                            mGroupListAdapter.addNode(root);
                            // 设置默认展开级别
                            mGroupListAdapter.notifyDataSetChanged();
                        } else {
                            mGroupListAdapter.clearDate();
                            mGroupListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(GroupListActivity.this, getResources().getString(R.string.grouplist_getgroup_fail), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGroupListManager.getRootNode() != null) {
            mGroupListManager.setRootNode(null);
        }
        finish();
    }

    private void updateSelectChannels() {
        if (selectNnodes.size() > 32) {
            Toast.makeText(GroupListActivity.this, getResources().getString(R.string.select_channel_limit_tv), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        getGroupList();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mGroupListManager.getRootNode() != null) {
                mGroupListManager.setRootNode(null);
            }
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onSearchChannelsClick(ChannelInfoExt channelInfoExt, boolean flag) {
        if (!flag) {
            return;
        }
        switch (comeFrom) {
            case AppDefine.FROM_LIVE_TO_GROUPLIST:

                Intent mIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean(AppDefine.FROM_GROUPLIST, true);
                bundle.putBoolean(AppDefine.NEED_PLAY, true);
                bundle.putSerializable(AppDefine.SELECTED_CHANNEL, channelInfoExt);
                mIntent.putExtras(bundle);
                setResult(RESULT_FIRST_USER, mIntent);
                finish();
                break;
            case AppDefine.FROM_PLAYBACK_TO_GROUPLIST:
                break;
            case AppDefine.FROM_GIS_TO_GROUPLIST:
                Intent mIntent2 = new Intent();
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable(AppDefine.SELECTED_CHANNEL, channelInfoExt);
                mIntent2.putExtras(bundle2);
                setResult(RESULT_OK, mIntent2);
                finish();
                break;
            default:
                break;
        }
    }

}
