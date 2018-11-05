package cn.eejing.colorflower.presenter;

/**
 * 网络 URL 地址接口
 */

public interface Urls {
    String VERSION_UPDATE =
            "http://www.eejing.cn/index.php/index/versionUpgrade/newVersionUpdate";    // 新版本更新
    String DOWN_LOAD_APK =
            "https://eejing.oss-cn-beijing.aliyuncs.com/eejing_download/eejing.apk";   // 下载APK地址

    String BASE_URL = "http://www.eejing.cn/index.php/index/api/";                     // 域名公共部分

    String REGISTER = BASE_URL + "register";                                           // 注册
    String LOGIN = BASE_URL + "login";                                                 // 登录
    String PWD_FIND = BASE_URL + "pwdFind";                                            // 密码找回
    String SEND_MSG = BASE_URL + "sendMsg";                                            // 发送短信

    String QUERY_DEV_MAC = BASE_URL + "queryDeviceMac";                                // 查询设备 Mac 地址
    String DEVICE_LIST = BASE_URL + "device_list";                                     // 获取用户设备列表
    String ADD_DEVICE = BASE_URL + "add_device";                                       // 用户添加设备
    String RM_DEVICE = BASE_URL + "rm_device";                                         // 用户删除设备
    String MATERIAL_INFO = BASE_URL + "materialInfo";                                  // 扫码获取料包信息
    String WAIT_USE_STATUS = BASE_URL + "addMaterial";                                 // 加料——标记为待使用状态
    String END_USE_STATUS = BASE_URL + "changeMaterialStatus";                         // 加料——标记为已使用状态
    String NO_USE_STATUS = BASE_URL + "cancel_Mstatus";                                // 加料——标记为未使用状态

    String ADD_GROUP = BASE_URL + "add_group";                                         // 用户新建设备组
    String GET_DEVICE_GROUP_LIST = BASE_URL + "getDeviceGroupList";                    // 获取设备用户组
    String GO_EDIT_DEVICE_TO_GROUP = BASE_URL + "goEditDeviceToGroup";                 // 修改设备组内设备——获取设备页面
    String ADD_DEVICE_TO_GROUP = BASE_URL + "addDeviceToGroup";                        // 添加设备到用户组
    String RM_GROUP = BASE_URL + "rm_group";                                           // 删除用户组
    String RENAME_GROUP = BASE_URL + "renameGroup";                                    // 设备组重命名

    String GOODS_LIST = BASE_URL + "goodsList";                                        // 商品列表
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
