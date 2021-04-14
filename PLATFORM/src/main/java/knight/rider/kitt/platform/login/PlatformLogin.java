package knight.rider.kitt.platform.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import knight.rider.kitt.platform.KittPlatform;

public class PlatformLogin {

    /**
     * 微信登录方法
     */
    public static void onWechatLogin(Activity activity) {

        // 将该app注册到微信
        final IWXAPI wxapi = WXAPIFactory.createWXAPI(activity, KittPlatform.getAppIdWechat(), true);
        wxapi.registerApp(KittPlatform.getAppIdWechat());

        if (!wxapi.isWXAppInstalled()) {
            Toast.makeText(activity, "您尚未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        wxapi.sendReq(req);

    }


    /**
     * 是否是微信登录的回调
     */
    public static boolean isWechatLoginCallBack(BaseResp baseResp) {
        return baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH;
    }


    /**
     * 信获取用户信息（需要在onWechatLogin()回调里取）
     */
    public static void onGetWechatUserInfo(BaseResp baseResp, IWechatUserListener listener) {

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (isWechatLoginCallBack(baseResp)) {
                    String tokenUrl = " https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + KittPlatform.getAppIdWechat() + "&secret=" +
                            KittPlatform.getAppSecretWechat() + "&code=" + ((SendAuth.Resp) baseResp).code + "&grant_type=authorization_code";

                    new WechatTokenAsync(listener).execute(tokenUrl);
                } else {
                    if (listener != null)
                        listener.onFail("获取token失败(可能不是登录获取的用户信息)");
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (listener != null)
                    listener.onFail("用户取消授权，获取信息失败");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                if (listener != null)
                    listener.onFail("用户拒绝授权，获取信息失败");
                break;
            default:
                if (listener != null)
                    listener.onFail("授权失败");

        }
    }


    /**
     * QQ登录方法
     */
    public static void onQQLogin(Activity activity, QQLoginListener listener) {
        Tencent mTencent = Tencent.createInstance(KittPlatform.getAppIdQq(), activity);
        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, "all", listener);
        }
    }


    /**
     * QQ在onActivityResult调用此方法
     */
    public static void onQQLoginActivityResult(int requestCode, int resultCode, Intent data, QQLoginListener listener) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
    }


    /**
     * QQ获取用户信息（需要在IQQListener里通过QQLoginResp取OPENID，ACCESS_TOKEN）
     *
     * @param ACCESS_TOKEN  IQQListener里通过QQLoginResp取ACCESS_TOKEN
     * @param OPENID        IQQListener里通过QQLoginResp取OPENID
     * @param isNeedUnionId 是否需要获取unionId
     */
    public static void onGetQQUserInfo(String ACCESS_TOKEN, String OPENID, boolean isNeedUnionId, IQQUserListener listener) {

        if (isNeedUnionId) {
            String unionUrl = "https://graph.qq.com/oauth2.0/me?access_token=" + ACCESS_TOKEN + "&unionid=1";
            new QQUnionIdAsync(listener, ACCESS_TOKEN, OPENID).execute(unionUrl);
        } else {
            onGetQQUserInfoFinal(ACCESS_TOKEN, OPENID, "", false, listener);
        }

    }


    /**
     * QQ获取用户信息（内部调用 不对外提供访问）
     *
     * @param isHasRequestUnionId 是否获取过unionId
     */
    protected static void onGetQQUserInfoFinal(String ACCESS_TOKEN, String OPENID, String UNIONID, boolean isHasRequestUnionId, IQQUserListener listener) {
        String infoUrl = "https://graph.qq.com/user/get_user_info?access_token=" + ACCESS_TOKEN + "&oauth_consumer_key=" + KittPlatform.getAppIdQq() + "&openid=" + OPENID;
        new QQUserInfoAsync(listener, OPENID, UNIONID, isHasRequestUnionId).execute(infoUrl);
    }
}
