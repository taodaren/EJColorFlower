package cn.eejing.colorflower.presenter;

/**
 * 网络 URL 地址接口
 */

public interface Urls {
    String DOWN_LOAD_APK = "https://eejing.oss-cn-beijing.aliyuncs.com/eejing_download/eejing.apk";

    String BASE_URL = "http://v2.eejing.cn/api/v2.1/";                      // 域名公共部分 2.0 版本

    String NEW_VERSION_UPDATE  = BASE_URL + "newVersionUpdate";             // 版本更新
    String SEND_MSG            = BASE_URL + "sendMsg";                      // 发送短信
    String REGISTER            = BASE_URL + "register";                     // 普通用户注册
    String CHANGE_PWD          = BASE_URL + "changePassword";               // 修改密码
    String LOGIN               = BASE_URL + "login";                        // 用户登录

    String GET_DEVICE_MAC      = BASE_URL + "getDeviceMac";                 // 设备 ID 获取 MAC 地址
    String GET_MATERIAL_INFO   = BASE_URL + "getMaterialInfo";              // 扫码获取料包信息
    String WAIT_USE_STATUS     = BASE_URL + "modifyWaitingForUseStatus";    // 修改为待使用状态
    String END_USE_STATUS      = BASE_URL + "modifyUseCompletedStatus";     // 修改为已使用状态
    String VIDEO_LIST          = BASE_URL + "videoList";                    // 视频列表

    String GET_GOODS_LIST      = BASE_URL + "getGoodsList";                 // 商品列表
    String GOODS_DETAIL        = BASE_URL + "goodsDetail";                  // 商品详情
    String CONFIRM_ORDER       = BASE_URL + "confirmOrder";                 // 确认订单页面展示
    String SET_ORDER           = BASE_URL + "setOrder";                     // 用户提交订单并生成订单
    String ADDRESS_LIST        = BASE_URL + "addressList";                  // 收货地址列表
    String CREATE_ADDRESS      = BASE_URL + "createAddress";                // 添加收货地址
    String AREA_SELECT         = BASE_URL + "areaSelect";                   // 地区选择
    String EDIT_ADDRESS        = BASE_URL + "editAddress";                  // 修改收货地址
    String SET_DEF_ADDRESS     = BASE_URL + "setDefaultAddress";            // 设置默认地址
    String DEL_ADDRESS         = BASE_URL + "delAddress";                   // 删除收货地址
    String A_LI_PAY            = BASE_URL + "aLiPay";                       // 支付宝支付
    String WE_CHAT_PAY         = BASE_URL + "weChatPay";                    // 微信支付
    String CALL_BACK_CONFIRM   = BASE_URL + "payCallBackConfirm";           // 商品订单支付结果确认

    String TO_UPGRADE_VIP      = BASE_URL + "toUpgradeVIP";                 // 进入升级 VIP 页面
    String TO_BE_VIP           = BASE_URL + "toBeVip";                      // 升级为 VIP
    String ORDER_LIST          = BASE_URL + "orderList";                    // 订单列表
    String ORDER_DETAIL        = BASE_URL + "orderDetail";                  // 订单详情
    String CONFIRM_RECEIPT     = BASE_URL + "confirmReceipt";               // 确认收货
    String DEL_ORDER           = BASE_URL + "delOrder";                     // 删除订单
    String FEED_BACK           = BASE_URL + "feedback";                     // 意见反馈
    String ABOUT_US            = BASE_URL + "aboutUs";                      // 关于我们
    String SET_PAY_PWD         = BASE_URL + "setPayPwd";                    // 设置支付密码
    String SET_UPGRADE_ORDER   = BASE_URL + "setUpgradeOrder";              // 生成升级 VIP 订单【暂不用】
    String SHOW_UNDERLING_VIP  = BASE_URL + "showUnderlingVip";             // 获取下游 vip 列表
    String SET_VIP_DISCOUNT    = BASE_URL + "setVipDiscount";               // VVIP 设置 vip 价格折扣
    String SET_VIP_RERMARK     = BASE_URL + "setVipRermark";                // VVIP 设置 vip 备注
    String SALES_RECORD        = BASE_URL + "salesRecord";                  // 销售记录
    String APPLY_FOR_WITH_DRAW = BASE_URL + "applyForWithdraw";             // 提现申请
    String OPEN_WALLET         = BASE_URL + "openWallet";                   // 打开钱包
    String WALLET_LOG          = BASE_URL + "walletLog";                    // 钱包变更记录
}
