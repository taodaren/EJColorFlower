package cn.eejing.colorflower.view.base;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.eejing.colorflower.model.event.AddrAddEvent;
import cn.eejing.colorflower.model.event.AddrSelectEvent;
import cn.eejing.colorflower.model.event.DevConnEvent;

public abstract class BaseActivityEvent extends BaseActivity {

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
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
