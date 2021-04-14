package knight.rider.kitt.platform.login;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取token 并获取用户信息
 */
class WechatTokenAsync extends AsyncTask<String, Integer, String> {

    private IWechatUserListener listener;

    public WechatTokenAsync(IWechatUserListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (listener != null)
            listener.onStart();
    }

    @Override
    protected String doInBackground(String... params) {

        String json = "";

        try {
            //1. URL
            URL url = new URL(params[0]);
            //2. HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置10秒的响应超时
            conn.setConnectTimeout(10000);
            //3. POST
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) { //获取状态码 200表示连接成功
                //获取输入流
                InputStream in = conn.getInputStream();
                //读取输入流
                byte[] b = new byte[1024 * 512]; //定义一个byte数组读取输入流
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); //定义缓存流来保存输入流的数据
                int len = 0;
                while ((len = in.read(b)) > -1) {  //每次读的len>-1 说明是是有数据的
                    baos.write(b, 0, len);  //三个参数  输入流byte数组   读取起始位置  读取终止位置
                }
                json = baos.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }


    @Override
    protected void onPostExecute(String result) {

        try {

            // 转换成jsonObject
            JSONObject jsonObject = new JSONObject(result);

            // 获取成功的信息
            String access_token = jsonObject.optString("access_token", "");
            String openid = jsonObject.optString("openid", "");

            // 获取失败信息
            String errmsg = jsonObject.optString("errmsg", "");

            if (TextUtils.isEmpty(errmsg)) {
                // 获取token成功，请求用户信息
                String userInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
                new WechatUserInfoAsync(listener).execute(userInfo);
            } else {
                if (listener != null)
                    listener.onFail(errmsg);
            }

        } catch (JSONException e) {
            if (listener != null)
                listener.onFail("获取微信用户token时，转换json字符串发生异常");
        }
    }
}
