package com.zhihuihezhang.zj.jiaxing.video.Player;

public interface IPlayerListener {
    public void onPlayerTime(IPlayer player, Time time);
    public void onStreamArrived(IPlayer player);
}
