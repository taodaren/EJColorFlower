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

    String DEVICE_LIST = BASE_URL + "device_list";                                     // 获取用户设备列表
    String GET_MATERIAL_INFO = BASE_URL_V2 + "getMaterialInfo";                           // 扫码获取料包信息
    String WAIT_USE_STATUS = BASE_URL_V2 + "modifyWaitingForUseStatus";                   // 修改为待使用状态
    String END_USE_STATUS = BASE_URL_V2 + "modifyUseCompletedStatus";                     // 修改为已使用状态

    String GET_GOODS_LIST = BASE_URL_V2 + "getGoodsList";                                 // 商品列表
    String GOODS_DETAILS = BASE_URL + "goodsDetails";                                  // 商品详情
    String CONFIRM_ORDER = BASE_URL + "confirmOrder";                                  // 确认订单
    String ADDRESS_LIST = BASE_URL + "addressList";                                    // 用户地址列表
    String ADDRESS_ADD = BASE_URL + "addressAdd";                                      // 用户地址填加
    String PROVINCES = BASE_URL + "provinces";                                         // 省接口
    String CITYS = BASE_URL + "citys";                                                 // 市接口
    String AREAS = BASE_URL + "areas";                                                 // 县接口
    String ADDRESS_UPDATE = BASE_URL + "addressUpdate";                                // 用户地址更改
    String ADDRESS_DEF = BASE_URL + "addressDef";                                      // 用户设置默认地址
    String ADDRESS_DEL = BASE_URL + "addressDel";                                      // 用户地址删除
    String PAY = BASE_URL + "pay";                                                     // 支付
    String CALL_BACK_CONFIRM = BASE_URL + "callBackConfirm";                           // 确认支付结果

    String WAIT_GOODS = BASE_URL + "waitGoods";                                        // 待发货
    String ALREADY_GOODS = BASE_URL + "alreadyGodos";                                  // 已发货
    String COMPLETED = BASE_URL + "Completed";                                         // 已完成
    String ORDER_DETAILS = BASE_URL + "orderDetails";                                  // 订单详情
    String COLLECT_GOODS = BASE_URL + "collectGoods";                                  // 确认收货(待收货)
    String DEL_COMPLETED = BASE_URL + "delCompleted";                                  // 删除已完成订单(已完成)
    String PWD_UPDATE = BASE_URL + "pwdUpdate";                                        // 修改密码
    String FEED_BACK = BASE_URL + "feedBack";                                          // 意见反馈
    String ABOUT_LINK = BASE_URL + "aboutLink";                                        // 关于我们
}
