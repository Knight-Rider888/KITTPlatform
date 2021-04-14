package knight.rider.kitt.platform;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.net.URL;

/**
 * 微信缩略图异步
 */
class WechatThumbnailAsync extends AsyncTask<String, Integer, Bitmap> {


    private WechatThumbnailListener listener;

    public WechatThumbnailAsync(WechatThumbnailListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap thumbBmp = null;

        try {
            thumbBmp = BitmapFactory.decodeStream(new URL(params[0]).openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thumbBmp;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        if (listener != null)
            listener.thumbBmp(result);
    }
}
