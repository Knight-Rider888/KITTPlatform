package knight.rider.kitt.platform.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ShareThumbUtil {

    private static final String TAG = "SDK_Sample.Util";

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {

        if (path != null && !path.equals("") && height > 0 && width > 0) {

            BitmapFactory.Options options = new BitmapFactory.Options();

            try {
                options.inJustDecodeBounds = true;
                Bitmap tmp = BitmapFactory.decodeFile(path, options);
                if (tmp != null) {
                    tmp.recycle();
                    tmp = null;
                }

                Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);

                int outHeight = options.outHeight;
                int outWidth = options.outWidth;


                final double beY = outHeight * 1.0 / height;
                final double beX = outWidth * 1.0 / width;
                Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
                options.inSampleSize = (int) (crop ? (Math.min(beY, beX)) : (Math.max(beY, beX)));
                if (options.inSampleSize <= 1) {
                    options.inSampleSize = 1;
                }

                // NOTE: out of memory error
                while (outHeight * outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                    options.inSampleSize++;
                }

                int newHeight = height;
                int newWidth = width;
                if (crop) {
                    if (beY > beX) {
                        newHeight = (int) (newWidth * 1.0 * outHeight / outWidth);
                    } else {
                        newWidth = (int) (newHeight * 1.0 * outWidth / outHeight);
                    }
                } else {
                    if (beY < beX) {
                        newHeight = (int) (newWidth * 1.0 * outHeight / outWidth);
                    } else {
                        newWidth = (int) (newHeight * 1.0 * outWidth / outHeight);
                    }
                }

                options.inJustDecodeBounds = false;

                Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + outWidth + "x" + outHeight + ", sample=" + options.inSampleSize);
                Bitmap bm = BitmapFactory.decodeFile(path, options);
                if (bm == null) {
                    Log.e(TAG, "bitmap decode failed");
                    return null;
                }

                Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
                final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
                if (scale != null) {
                    bm.recycle();
                    bm = scale;
                }

                if (crop) {
                    final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                    if (cropped == null) {
                        return bm;
                    }

                    bm.recycle();
                    bm = cropped;
                    Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
                }
                return bm;

            } catch (final OutOfMemoryError e) {
                Log.e(TAG, "decode bitmap failed: " + e.getMessage());
                options = null;
            }
        }

        return null;
    }
}
