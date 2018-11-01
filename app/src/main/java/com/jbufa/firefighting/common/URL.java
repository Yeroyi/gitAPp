package com.jbufa.firefighting.common;

public class URL {

    //服务器地址  测试：47.104.159.154:9081 生产：http://47.105.58.30:9081
    public static final String HTTP_DOMAIN = "http://47.105.58.30:9081/app/";
    //注册
    public static final String REGISTER = HTTP_DOMAIN + "user/register";
    //发生验证码
    public static final String MESSAGE = HTTP_DOMAIN + "user/message";
    //修改密码
    public static final String RESETPWD = HTTP_DOMAIN + "user/resetPwd";
    //登录接口
    public static final String LOGIN = HTTP_DOMAIN + "user/login";
    //获取房间列表
    public static final String SELECTPLACES = HTTP_DOMAIN + "place/selectPlacesByUser";
    //添加场地
    public static final String ADDPLACE = HTTP_DOMAIN + "place/add";
    //添加房间
    public static final String ADDHOME = HTTP_DOMAIN + "room/addRoom";
    //房间列表
    public static final String ROOMLIST = HTTP_DOMAIN + "room/showRoom";
    //绑定设备
    public static final String ROOMBINDDEVICE = HTTP_DOMAIN + "room/roomBindDevice";
    //获取房间设备列表
    public static final String SHOWDEVICELIST = HTTP_DOMAIN + "room/showDeviceList";
    //获取设备历史消息
    public static final String QUERYHISTORY = HTTP_DOMAIN + "devDataHistory/queryHistoryByUser";
    //获取场地联系人
    public static final String SHOWLINKMAN = HTTP_DOMAIN + "place/showLinkMan";
    //添加场地联系人
    public static final String ADDLINKMAN = HTTP_DOMAIN + "place/addLinkMan";
    //删除场地联系人
    public static final String DELETELINKMA = HTTP_DOMAIN + "place/deleteLinkMan";
    //删除房间
    public static final String DELETEROOM = HTTP_DOMAIN + "room/deleteRoom";
    //房间解绑设备
    public static final String UNBINDDEVICE = HTTP_DOMAIN + "room/roomUnBindDevice";
    //设备名称编辑
    public static final String EDITDEVICENAME = HTTP_DOMAIN + "device/editDeviceName";
    //房间编辑
    public static final String EDITROOM = HTTP_DOMAIN + "room/editRoom";
    //场地编辑
    public static final String PLACEEDIT = HTTP_DOMAIN + "place/edit";
    //场地删除
    public static final String PLACEDELETE = HTTP_DOMAIN + "place/delete";
    //忘记密码
    public static final String FORGETPASSWORD = HTTP_DOMAIN + "user/forgetPassword";
    //忘记密码验证码
    public static final String MESSAGECODE = HTTP_DOMAIN + "user/messageCode";
    //设备当前状态
    public static final String DEVCURRENTSTATUS = HTTP_DOMAIN + "device/getDevCurrentStatus";
    //app启动页
    public static final String APPAD = HTTP_DOMAIN + "message/advertisementDisplay/detail";
    //用户名
    public static final String APPUSERNAME = HTTP_DOMAIN + "user/appUserName";
    //修改用户名
    public static final String UPDATEAPPUSERNAME = HTTP_DOMAIN + "user/updateAppUserName";


}
