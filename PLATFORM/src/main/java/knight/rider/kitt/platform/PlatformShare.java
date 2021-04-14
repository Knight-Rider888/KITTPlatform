package knight.rider.kitt.platform;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knight.rider.kitt.platform.util.ShareThumbUtil;

public class PlatformShare {

    /**
     * 是否是微信分享的回调
     */
    public static boolean isWechatShareCallBack(BaseResp baseResp) {
        return baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX;
    }

    /**
     * 分享微信小程序（目前仅能分享到微信会话）
     *
     * @param miniProgramId 小程序原始id
     * @param webUrl        分享的网页链接地址（兼容老版本微信）
     * @param imgUrl        缩略图地址（本地、网络）
     * @param title         分享的标题
     * @param pagePath      小程序页面的地址
     */
    public static void onWXMiniProgramShare(String miniProgramId, String webUrl, String imgUrl, String title, String pagePath) {

        if (TextUtils.isEmpty(miniProgramId)) {
            Toast.makeText(KittPlatform.getContext(), "分享失败(小程序Id不能为空)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(webUrl)) {
            Toast.makeText(KittPlatform.getContext(), "分享失败(网址不能为空)", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(TextUtils.isEmpty(imgUrl) ? "" : imgUrl);

        final WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.webpageUrl = webUrl; // 兼容低版本的网页链接
        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;// 正式版:0，测试版:1，体验版:2
        miniProgramObj.userName = miniProgramId;// 小程序原始id

        if (!TextUtils.isEmpty(pagePath)) {
            miniProgramObj.path = pagePath;//小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
        }

        final WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = title;// 小程序消息title

        if (file.exists()) {

            try {
                Bitmap thumbBmp = getBitmapFromFile(imgUrl);
                msg.thumbData = bitmap2Bytes(thumbBmp, 128);// 小程序消息封面图片，小于128k
                thumbBmp.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("miniProgram");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前只支持会话

            // 调用api接口，发送数据到微信
            IWXAPI wxapi = WXAPIFactory.createWXAPI(KittPlatform.getContext(), KittPlatform.getAppIdWechat(), true);
            wxapi.registerApp(KittPlatform.getAppIdWechat());
            wxapi.sendReq(req);

        } else {

            new WechatThumbnailAsync(new WechatThumbnailListener() {
                @Override
                public void thumbBmp(Bitmap bitmap) {

                    try {
                        msg.thumbData = bitmap2Bytes(bitmap, 128);// 小程序消息封面图片，小于128k
                        bitmap.recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //构造一个Req
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("miniProgram");
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前只支持会话

                    //调用api接口，发送数据到微信
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(KittPlatform.getContext(), KittPlatform.getAppIdWechat(), true);
                    wxapi.registerApp(KittPlatform.getAppIdWechat());
                    wxapi.sendReq(req);

                }
            }).execute(imgUrl);
        }
    }

    /**
     * 媒体消息分享(用的最多的方式)
     *
     * @param context     上下文对象
     * @param titleStr    分享标题
     * @param description 分享内容
     * @param webUrl      分享的地址链接
     * @param imgUrl      缩略图(QQ空间必须用网络图片)
     * @param type        分享类型
     * @param listener    qq分享监听(不需要监听传null,需要监听 需要在onActivityResult()调用onQQActivityResult()方法)
     */
    public static void onMediaMessageShare(final Activity context, String titleStr, String description, String webUrl, final String imgUrl, final ShareType type, IQQShareListener listener) {

        String urlStartHttp = "http:" + File.separator + File.separator;
        String urlStartHttps = "https:" + File.separator + File.separator;

        if (TextUtils.isEmpty(titleStr)) {
            Toast.makeText(KittPlatform.getContext(), "分享失败(标题不能为空)", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type) {

            case QQ:
                if (TextUtils.isEmpty(webUrl) || (!webUrl.startsWith(urlStartHttp) && !webUrl.startsWith(urlStartHttps))) {
                    Toast.makeText(KittPlatform.getContext(), "分享失败(网址格式不正确)", Toast.LENGTH_SHORT).show();
                    return;
                }

                Tencent mTencent = Tencent.createInstance(KittPlatform.getAppIdQq(), context);

                final Bundle qqParams = new Bundle();
                qqParams.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                // 这条分享消息被好友点击后跳转URL（必填）
                qqParams.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webUrl);
                // 分享的标题，最长30个字符（必填）
                qqParams.putString(QQShare.SHARE_TO_QQ_TITLE, titleStr);
                // 摘要 （选填）
                qqParams.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
                // 分享的图片 （选填）
                if (!TextUtils.isEmpty(imgUrl)) {
                    qqParams.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
                }

                mTencent.shareToQQ(context, qqParams, listener);
                break;


            case QZONE:
                if (TextUtils.isEmpty(webUrl) || (!webUrl.startsWith(urlStartHttp) && !webUrl.startsWith(urlStartHttps))) {
                    Toast.makeText(KittPlatform.getContext(), "分享失败(网址格式不正确)", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(imgUrl) || (!imgUrl.startsWith(urlStartHttp) && !imgUrl.startsWith(urlStartHttps))) {
                    Toast.makeText(KittPlatform.getContext(), "分享失败(需要网络图片)", Toast.LENGTH_SHORT).show();
                    return;
                }

                Tencent mTencents = Tencent.createInstance(KittPlatform.getAppIdQq(), context);

                final Bundle qzoneParams = new Bundle();

                qzoneParams.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                // 分享的标题，最长30个字符（必填）
                qzoneParams.putString(QzoneShare.SHARE_TO_QQ_TITLE, titleStr);
                // 这条分享消息被好友点击后跳转URL（必填）
                qzoneParams.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, webUrl);
                // 摘要 （选填）
                qzoneParams.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);
                // 分享的图片
                ArrayList<String> arraylist = new ArrayList<>();
                Collections.addAll(arraylist, imgUrl);
                qzoneParams.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arraylist);

                mTencents.shareToQzone(context, qzoneParams, listener);
                break;


            case WECHAT_FRIEND:
            case WECHAT_CIRCLES:
            case WECHAT_COLLECTION:

                if (TextUtils.isEmpty(webUrl)) {
                    Toast.makeText(KittPlatform.getContext(), "分享失败(网址不能为空)", Toast.LENGTH_SHORT).show();
                    return;
                }

                //初始化一个WXWebpageObject，填写url
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = webUrl;

                //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
                final WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = titleStr;
                msg.description = description;

                File file = new File(TextUtils.isEmpty(imgUrl) ? "" : imgUrl);

                if (file.exists()) {

                    try {
                        Bitmap thumbBmp = getBitmapFromFile(imgUrl);
                        msg.thumbData = bitmap2Bytes(thumbBmp, 32);
                        thumbBmp.recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //构造一个Req
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("webpage");
                    req.message = msg;


                    req.scene = type.getScene();

                    //调用api接口，发送数据到微信
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(context, KittPlatform.getAppIdWechat(), true);
                    wxapi.registerApp(KittPlatform.getAppIdWechat());
                    wxapi.sendReq(req);

                } else {

                    new WechatThumbnailAsync(new WechatThumbnailListener() {
                        @Override
                        public void thumbBmp(Bitmap bitmap) {

                            try {
                                msg.thumbData = bitmap2Bytes(bitmap, 32);
                                bitmap.recycle();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //构造一个Req
                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.transaction = buildTransaction("webpage");
                            req.message = msg;


                            req.scene = type.getScene();

                            //调用api接口，发送数据到微信
                            IWXAPI wxapi = WXAPIFactory.createWXAPI(context, KittPlatform.getAppIdWechat(), true);
                            wxapi.registerApp(KittPlatform.getAppIdWechat());
                            wxapi.sendReq(req);

                        }
                    }).execute(imgUrl);
                }
                break;
        }

    }


    /**
     * 分享视频
     *
     * @param context     上下文对象
     * @param titleStr    分享标题
     * @param description 分享内容
     * @param videoUrl    分享的视频地址链接
     * @param imgUrl      缩略图 (QQ空间必须用网络图片)
     * @param type        分享类型
     * @param listener    qq分享监听(不需要监听传null,需要监听 需要在onActivityResult()调用onQQActivityResult()方法)
     */
    public static void onVideoShare(final Activity context, String titleStr, String description, String videoUrl, final String imgUrl, final ShareType type, IQQShareListener listener) {

        switch (type) {

            case QQ:
                onMediaMessageShare(context, titleStr, description, videoUrl, imgUrl, ShareType.QQ, listener);
                break;
            case QZONE:
                onMediaMessageShare(context, titleStr, description, videoUrl, imgUrl, ShareType.QZONE, listener);
                break;

            case WECHAT_FRIEND:
            case WECHAT_CIRCLES:
            case WECHAT_COLLECTION:

                if (TextUtils.isEmpty(videoUrl)) {
                    Toast.makeText(KittPlatform.getContext(), "分享失败(视频地址不能为空)", Toast.LENGTH_SHORT).show();
                    return;
                }


                //初始化一个WXVideoObject，填写url
                WXVideoObject video = new WXVideoObject();
                // 不能为空 掉不起来微信
                video.videoUrl = videoUrl;

                //用 WXVideoObject 对象初始化一个 WXMediaMessage 对象
                final WXMediaMessage msg = new WXMediaMessage(video);
                msg.title = titleStr;
                msg.description = description;

                File file = new File(TextUtils.isEmpty(imgUrl) ? "" : imgUrl);

                if (file.exists()) {

                    try {
                        Bitmap thumbBmp = getBitmapFromFile(imgUrl);
                        msg.thumbData = bitmap2Bytes(thumbBmp, 32);
                        thumbBmp.recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //构造一个Req
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("video");
                    req.message = msg;
                    req.scene = type.getScene();

                    //调用api接口，发送数据到微信
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(context, KittPlatform.getAppIdWechat(), true);
                    wxapi.registerApp(KittPlatform.getAppIdWechat());
                    wxapi.sendReq(req);

                } else {

                    new WechatThumbnailAsync(new WechatThumbnailListener() {
                        @Override
                        public void thumbBmp(Bitmap bitmap) {

                            try {
                                msg.thumbData = bitmap2Bytes(bitmap, 32);
                                bitmap.recycle();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //构造一个Req
                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.transaction = buildTransaction("video");
                            req.message = msg;
                            req.scene = type.getScene();

                            //调用api接口，发送数据到微信
                            IWXAPI wxapi = WXAPIFactory.createWXAPI(context, KittPlatform.getAppIdWechat(), true);
                            wxapi.registerApp(KittPlatform.getAppIdWechat());
                            wxapi.sendReq(req);

                        }
                    }).execute(imgUrl);
                }
                break;
        }

    }


    /**
     * 分享图片
     *
     * @param context  上下文对象
     * @param imgUrl   图片的链接
     * @param type     分享类型
     * @param listener qq分享监听(不需要监听传null,需要监听 需要在onActivityResult()调用onQQActivityResult()方法)
     */
    public static void onImageShare(final Activity context, final String imgUrl, final ShareType type, IQQShareListener listener) {

        if (TextUtils.isEmpty(imgUrl)) {
            Toast.makeText(KittPlatform.getContext(), "分享失败(图片地址不能为空)", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(TextUtils.isEmpty(imgUrl) ? "" : imgUrl);

        if (!file.exists() && type == ShareType.QQ) {
            Toast.makeText(KittPlatform.getContext(), "分享失败(本地图片不存在)", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type) {

            case QQ:
                Tencent mTencent = Tencent.createInstance(KittPlatform.getAppIdQq(), context);
                Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                // 必须是本地图片
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, copyFile(context, imgUrl));
                mTencent.shareToQQ(context, params, listener);
                break;
            case QZONE:
                Tencent mTencents = Tencent.createInstance(KittPlatform.getAppIdQq(), context);
                Bundle qZoneParams = new Bundle();
                qZoneParams.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                ArrayList<String> arraylist = new ArrayList<>();
                // 只能加载本地图片，非本地可以打开说说，不报错
                Collections.addAll(arraylist, copyFile(context, imgUrl));
                qZoneParams.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arraylist);
                mTencents.publishToQzone(context, qZoneParams, listener);
                break;

            case WECHAT_FRIEND:
            case WECHAT_CIRCLES:
            case WECHAT_COLLECTION:

                if (file.exists()) {

                    Bitmap thumbBmp = getBitmapFromFile(imgUrl);
                    //初始化 WXImageObject 和 WXMediaMessage 对象
                    WXImageObject imgObj = new WXImageObject(thumbBmp);
                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = imgObj;

                    try {
                        msg.thumbData = bitmap2Bytes(thumbBmp, 32);
                        thumbBmp.recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //构造一个Req
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("img");
                    req.message = msg;
                    req.scene = type.getScene();

                    //调用api接口，发送数据到微信
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(context, KittPlatform.getAppIdWechat(), true);
                    wxapi.registerApp(KittPlatform.getAppIdWechat());
                    wxapi.sendReq(req);

                } else {

                    new WechatThumbnailAsync(new WechatThumbnailListener() {
                        @Override
                        public void thumbBmp(Bitmap bitmap) {

                            //初始化 WXImageObject 和 WXMediaMessage 对象
                            WXImageObject imgObj = new WXImageObject(bitmap);
                            WXMediaMessage msg = new WXMediaMessage();
                            msg.mediaObject = imgObj;

                            try {
                                msg.thumbData = bitmap2Bytes(bitmap, 32);
                                bitmap.recycle();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //构造一个Req
                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.transaction = buildTransaction("img");
                            req.message = msg;
                            req.scene = type.getScene();

                            //调用api接口，发送数据到微信
                            IWXAPI wxapi = WXAPIFactory.createWXAPI(context, KittPlatform.getAppIdWechat(), true);
                            wxapi.registerApp(KittPlatform.getAppIdWechat());
                            wxapi.sendReq(req);

                        }
                    }).execute(imgUrl);
                }
                break;
        }

    }

    private static String copyFile(Activity context, String filePath) {

        String cachePath = context.getCacheDir().getAbsolutePath();

        if (filePath.startsWith(cachePath)) {

            File file = new File(filePath);
            File copyFile = new File(context.getExternalCacheDir() + "/" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = null;
            InputStream ins = null;
            try {
                fos = new FileOutputStream(copyFile);
                ins = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = ins.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();

                return copyFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                return filePath;
            } finally {
                try {
                    if (fos != null)
                        fos.close();

                    if (ins != null)
                        ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            return filePath;
        }
    }

    /**
     * 构建一个唯一标志
     */
    private static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();

    }

    private static Bitmap getBitmapFromFile(String filePath) {
        return ShareThumbUtil.extractThumbNail(filePath, 1000, 1000, false);
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     */
    private static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }

    // TODO QQ在onActivityResult调用此方法
    public static void onQQShareActivityResult(int requestCode, int resultCode, Intent data, IQQShareListener listener) {
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
    }

    // TODO 解决QQ在fragment不回调(在Activity的onActivityResult调用此方法)
    public static <T extends Fragment> void onQQSendFragmentActivityResult(AppCompatActivity activity, Class<T> targetFragmentClass, int requestCode, int resultCode, Intent data) {
        FragmentManager fm = activity.getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if (fragments.size() > 0) {
            for (Fragment f : fragments) {
                if (f.getClass() == targetFragmentClass)
                    f.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
