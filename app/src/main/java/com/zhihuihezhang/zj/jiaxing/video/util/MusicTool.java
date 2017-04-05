package com.zhihuihezhang.zj.jiaxing.video.util;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.zhihuihezhang.zj.jiaxing.AppApplication;
import com.zhihuihezhang.zj.jiaxing.R;

public class MusicTool {
    private SoundPool         soundPool;     // SoundPooll播放池
    private final Context     father;
    HashMap<Integer, Integer> soundMap;      // 声音列表
    private int               loadId = -1;
    public boolean            isPlay = false;
    int                       streamId;

    public MusicTool() {
        this.father = AppApplication.get();
    }

    // 初始化音乐播放
    public void initSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundMap = new HashMap<Integer, Integer>();
        SetRes(0, R.raw.phonecall);
        SetRes(1, R.raw.msg);
    }

    public void SetRes(int index, int resid) {
        loadId = soundPool.load(father, resid, 1);
        soundMap.put(index, loadId);
    }

    public void playSound(int sound, int loop) {

        float volume = 1f;
        if (soundMap == null) {
            initSound();
        }
        streamId = soundPool.play(soundMap.get(sound), volume, volume, 1, loop, 1);
        isPlay = true;
    }

    // 暂停
    public void pauseSound(int sound) {
        soundPool.pause(soundMap.get(sound));
    }

    // 停止播放音乐
    public void stopSound(int sound) {
        if (!isPlay) {
            return;
        }
        soundPool.stop(streamId);
        clear();
        isPlay = false;
    }

    public void clear() {
        if (loadId != -1) {
            soundPool.unload(loadId);
        }
        soundPool.release();
        soundPool = null;
        soundMap.clear();
        soundMap = null;
    }
}
