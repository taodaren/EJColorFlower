package cn.eejing.ejcolorflower.app;

/**
 * 网络 URL 地址接口
 */

public interface Urls {
    String BASE_URL = "http://www.eejing.cn/index.php/index/api/";                     // 域名公共部分

    String REGISTER = BASE_URL + "register";                                           // 注册
    String LOGIN = BASE_URL + "login";                                                 // 登录
    String PWD_FIND = BASE_URL + "pwdFind";                                            // 密码找回
    String SEND_MSG = BASE_URL + "sendMsg";                                            // 发送短信
    String DEVICE_LIST = BASE_URL + "device_list";                                     // 获取用户设备列表
    String ADD_DEVICE = BASE_URL + "add_device";                                       // 用户添加设备
    String RM_DEVICE = BASE_URL + "rm_device";                                         // 用户删除设备
    String ADD_GROUP = BASE_URL + "add_group";                                         // 用户新建设备组
    String GET_DEVICE_GROUP_LIST = BASE_URL + "getDeviceGroupList";                    // 获取设备用户组
    String EDIT_HIGH = BASE_URL + "editHigh";                                          // 编辑控制高度上传
    String GO_EDIT_DEVICE_TO_GROUP = BASE_URL + "goEditDeviceToGroup";                 // 修改设备组内设备——获取设备页面
    String ADD_DEVICE_TO_GROUP = BASE_URL + "addDeviceToGroup";                        // 添加设备到用户组
    String RM_GROUP = BASE_URL + "rm_group";                                           // 删除用户组
    String RENAME_GROUP = BASE_URL + "renameGroup";                                    // 设备组重命名
    String MATERIAL_INFO = BASE_URL + "materialInfo";                                  // 扫码获取料包信息
    String ADD_MATERIAL = BASE_URL + "addMaterial";                                    // 设备加料——标记为待使用状态
    String CHANG_MATERIAL_STATUS = BASE_URL + "changMaterialStatus";                   // 料包标记为已使用状态
    String PWD_UPDATE = BASE_URL + "pwdUpdate";                                        // 修改密码
    String FEED_BACK = BASE_URL + "feedBack";                                          // 意见反馈
    String ABOUT_LINK = BASE_URL + "aboutLink";                                        // 关于我们

    interface Mall {
        String GOODS_LIST = BASE_URL + "goodsList";                                    // 商品列表

    }

}
