package knight.rider.kitt.platform;

import android.app.Application;
import android.text.TextUtils;

public class KittPlatform {

    private static String APP_ID_QQ;
    private static String APP_ID_WECHAT;
    private static String APP_SECRET_WECHAT;
    private static Application mContext;

    // 需要在Application进行全局初始化
    public static void init(Application context, String QQ_APP_ID, String WECHAT_APP_ID, String WECHAT_APP_SECRET) {
        mContext = context;
        APP_ID_QQ = QQ_APP_ID;
        APP_ID_WECHAT = WECHAT_APP_ID;
        APP_SECRET_WECHAT = WECHAT_APP_SECRET;
    }


    public static String getAppIdQq() {

        if (TextUtils.isEmpty(APP_ID_QQ) || TextUtils.isEmpty(APP_ID_WECHAT) || TextUtils.isEmpty(APP_SECRET_WECHAT) || mContext == null)
            throw new RuntimeException("请先调用KittPlatform.init()进行初始化");

        return APP_ID_QQ;
    }

    public static String getAppIdWechat() {

        if (TextUtils.isEmpty(APP_ID_QQ) || TextUtils.isEmpty(APP_ID_WECHAT) || TextUtils.isEmpty(APP_SECRET_WECHAT) || mContext == null)
            throw new RuntimeException("请先调用KittPlatform.init()进行初始化");

        return APP_ID_WECHAT;
    }

    public static String getAppSecretWechat() {

        if (TextUtils.isEmpty(APP_ID_QQ) || TextUtils.isEmpty(APP_ID_WECHAT) || TextUtils.isEmpty(APP_SECRET_WECHAT) || mContext == null)
            throw new RuntimeException("请先调用KittPlatform.init()进行初始化");

        return APP_SECRET_WECHAT;
    }

    public static Application getContext() {

        if (TextUtils.isEmpty(APP_ID_QQ) || TextUtils.isEmpty(APP_ID_WECHAT) || TextUtils.isEmpty(APP_SECRET_WECHAT) || mContext == null)
            throw new RuntimeException("请先调用KittPlatform.init()进行初始化");

        return mContext;
    }
}
