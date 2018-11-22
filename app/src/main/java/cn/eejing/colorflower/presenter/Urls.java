package cn.eejing.colorflower.presenter;

/**
 * 网络 URL 地址接口
 */

public interface Urls {
    String DOWN_LOAD_APK =
            "https://eejing.oss-cn-beijing.aliyuncs.com/eejing_download/eejing.apk";   // 下载APK地址

    String BASE_URL = "http://www.eejing.cn/index.php/index/api/";                     // 域名公共部分
    String BASE_URL_V2 = "http://v2.eejing.cn/api/v2.0/";                              // 2.0版本

    String NEW_VERSION_UPDATE = BASE_URL_V2 + "newVersionUpdate";                         // 版本更新
    String SEND_MSG           = BASE_URL_V2 + "sendMsg";                                  // 发送短信
    String REGISTER           = BASE_URL_V2 + "register";                                 // 普通用户注册
    String CHANGE_PWD         = BASE_URL_V2 + "changePassword";                           // 修改密码
    String LOGIN              = BASE_URL_V2 + "login";                                    // 用户登录
    String GET_DEVICE_MAC     = BASE_URL_V2 + "getDeviceMac";                             // 设备ID获取MAC地址

    String GET_MATERIAL_INFO  = BASE_URL_V2 + "getMaterialInfo";                          // 扫码获取料包信息
    String WAIT_USE_STATUS    = BASE_URL_V2 + "modifyWaitingForUseStatus";                // 修改为待使用状态
    String END_USE_STATUS     = BASE_URL_V2 + "modifyUseCompletedStatus";                 // 修改为已使用状态

    String GET_GOODS_LIST     = BASE_URL_V2 + "getGoodsList";                             // 商品列表
    String GOODS_DETAIL       = BASE_URL_V2 + "goodsDetail";                              // 商品详情
    String CONFIRM_ORDER      = BASE_URL_V2 + "confirmOrder";                             // 确认订单页面展示
    String SET_ORDER          = BASE_URL_V2 + "setOrder";                                 // 用户提交订单并生成订单
    String ADDRESS_LIST       = BASE_URL_V2 + "addressList";                              // 收货地址列表
    String CREATE_ADDRESS     = BASE_URL_V2 + "createAddress";                            // 添加收货地址
    String AREA_SELECT        = BASE_URL_V2 + "areaSelect";                               // 地区选择
    String EDIT_ADDRESS       = BASE_URL_V2 + "editAddress";                              // 修改收货地址
    String SET_DEF_ADDRESS    = BASE_URL_V2 + "setDefaultAddress";                        // 设置默认地址
    String DEL_ADDRESS        = BASE_URL_V2 + "delAddress";                               // 删除收货地址
    String A_LI_PAY           = BASE_URL_V2 + "aLiPay";                                   // 支付宝支付
    String WE_CHAT_PAY        = BASE_URL_V2 + "weChatPay";                                // 微信支付
    String CALL_BACK_CONFIRM  = BASE_URL_V2 + "payCallBackConfirm";                       // 商品订单支付结果确认

    String WAIT_GOODS = BASE_URL + "waitGoods";                                        // 待发货
    String ALREADY_GOODS = BASE_URL + "alreadyGodos";                                  // 已发货
    String COMPLETED = BASE_URL + "Completed";                                         // 已完成
    String ORDER_DETAILS = BASE_URL + "orderDetails";                                  // 订单详情
    String COLLECT_GOODS = BASE_URL + "collectGoods";                                  // 确认收货(待收货)
    String DEL_COMPLETED = BASE_URL + "delCompleted";                                  // 删除已完成订单(已完成)
    String PWD_UPDATE = BASE_URL + "pwdUpdate";                                        // 修改密码
    String FEED_BACK = BASE_URL_V2 + "feedback";                                          // 意见反馈
    String ABOUT_US = BASE_URL_V2 + "aboutUs";                                            // 关于我们
    String SET_PAY_PWD = BASE_URL_V2 + "setPayPwd";                                       // 设置支付密码
}
