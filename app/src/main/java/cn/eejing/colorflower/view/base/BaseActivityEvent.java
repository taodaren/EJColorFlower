package cn.eejing.colorflower.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.eejing.colorflower.model.event.AddrAddEvent;
import cn.eejing.colorflower.model.event.AddrSelectEvent;
import cn.eejing.colorflower.model.event.DevConnEvent;
import cn.eejing.colorflower.util.LogUtil;

public abstract class BaseActivityEvent extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /** 蓝牙连接状态 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBleConn(DevConnEvent event) {
    }

    /** 地址添加 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAddrAdd(AddrAddEvent event) {
    }

    /** 地址选择 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAddrSelect(AddrSelectEvent event) {
    }
}
