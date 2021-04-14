package knight.rider.kitt.platform.login;


import knight.rider.kitt.platform.bean.WechatUserInfo;

public interface IWechatUserListener {

    // 获取信息开始前
    void onStart();

    // 获取失败
    void onFail(String errMsg);

    // 获取成功
    void onSuccess(WechatUserInfo info);
}
