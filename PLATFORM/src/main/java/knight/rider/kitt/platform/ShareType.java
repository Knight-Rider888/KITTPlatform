package knight.rider.kitt.platform;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

/**
 * 分享类型
 */
public enum ShareType {

    // qq -1 qzone -2
    QQ(-1), QZONE(-2),
    // 朋友 0  朋友圈 1  收藏 2
    WECHAT_FRIEND(SendMessageToWX.Req.WXSceneSession), WECHAT_CIRCLES(SendMessageToWX.Req.WXSceneTimeline), WECHAT_COLLECTION(SendMessageToWX.Req.WXSceneFavorite);


    // 发送目标场景
    private int scene;

    ShareType(int scene) {
        this.scene = scene;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }
}
