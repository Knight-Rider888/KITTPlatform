package knight.rider.kitt.platform.login;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import knight.rider.kitt.platform.bean.WechatUserInfo;

/**
 * 获取用户信息
 */
class WechatUserInfoAsync extends AsyncTask<String, Integer, String> {

    private IWechatUserListener listener;

    public WechatUserInfoAsync(IWechatUserListener listener) {
        this.listener = listener;
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
            String openid = jsonObject.optString("openid", "");
            String nickname = jsonObject.optString("nickname", "");
            int sex = jsonObject.optInt("sex", 1);
            String language = jsonObject.optString("language", "");
            String city = jsonObject.optString("city", "");
            String province = jsonObject.optString("province", "");
            String country = jsonObject.optString("country", "");
            String headimgurl = jsonObject.optString("headimgurl", "");
            String unionid = jsonObject.optString("unionid", "");
            JSONArray privilege = jsonObject.optJSONArray("privilege");

            // 获取错误信息
            String errmsg = jsonObject.optString("errmsg", "");

            if (TextUtils.isEmpty(errmsg)) {

                List<String> privilegeList = new ArrayList<>();

                try {

                    if (privilege != null && privilege.length() > 0) {

                        for (int i = 0; i < privilege.length(); i++) {
                            Object o = privilege.get(i);
                            if (o != null) {
                                privilegeList.add((String) o);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                WechatUserInfo wechatUserInfo = new WechatUserInfo(
                        openid, nickname, sex, language, city, province, country, headimgurl, unionid, privilegeList);

                if (listener != null)
                    listener.onSuccess(wechatUserInfo);

            } else {

                if (listener != null)
                    listener.onFail(errmsg);

            }

        } catch (JSONException e) {
            if (listener != null)
                listener.onFail("获取微信用户信息时，转换json字符串发生异常");
        }
    }
}
