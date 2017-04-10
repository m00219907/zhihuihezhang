package com.zhihuihezhang.zj.jiaxing.videofragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zhihuihezhang.zj.jiaxing.DialogUtils;
import com.zhihuihezhang.zj.jiaxing.R;
import com.zhihuihezhang.zj.jiaxing.video.groupTree.GroupListAdapter;
import com.zhihuihezhang.zj.jiaxing.video.groupTree.GroupListGetTask;
import com.zhihuihezhang.zj.jiaxing.video.groupTree.GroupListManager;
import com.zhihuihezhang.zj.jiaxing.video.groupTree.bean.TreeNode;

public class TabVideofragment extends Fragment {

    private Activity activity;

    private ListView list_guangdian;

    private GroupListAdapter adapter_guangdian;

    // 获取实例
    private GroupListManager mGroupListManager = null;

    // 获取的树信息
    private TreeNode root = null;

    public static final int MSG_GROUPLIST_START = 1652;

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_list_fragment, null);
        list_guangdian = (ListView) view.findViewById(R.id.list_guangdian);

        adapter_guangdian = new GroupListAdapter(activity);
        list_guangdian.setAdapter(adapter_guangdian);

        mGroupListManager = GroupListManager.getInstance();
        getGroupList();
        mHandler.sendEmptyMessage(1038);

        return view;
    }

    private void getGroupList() {
        root = mGroupListManager.getRootNode();

        if (mGroupListManager.getTask() != null) {
            mGroupListManager.setGroupListGetListener(mIOnSuccessListener);
        }
        if (mGroupListManager.isFinish() && root != null) {
            if (root.getChildren().size() == 0) {
                mGroupListManager.startGroupListGetTask();
            }
        } else if (root == null) {
            if (mGroupListManager.getTask() == null) {
                mGroupListManager.startGroupListGetTask();
                mGroupListManager.setGroupListGetListener(mIOnSuccessListener);
            }
        }
    }

    Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GROUPLIST_START:
                    getGroupList();
                    break;
                case 1038:
                    if (!DialogUtils.isShowWaitDialog()) {
                        DialogUtils.showWaitDialog(activity, "加载中...", -1);
                    }
                    break;
                case 1039:
                    if (DialogUtils.isShowWaitDialog()) {
                        DialogUtils.dismissDialog();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    GroupListGetTask.IOnSuccessListener mIOnSuccessListener = new GroupListGetTask.IOnSuccessListener() {
        @Override
        public void onSuccess(final boolean success, final int errCode) {

            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // 清空任务
                    mGroupListManager.setTask(null);
                    mHandler.sendEmptyMessage(1039);

                    if (success) {
                        root = mGroupListManager.getRootNode();
                        if (root != null) {
                            adapter_guangdian.addNode(root);
                            adapter_guangdian.notifyDataSetChanged();
                        }
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
    }
}
