package knight.rider.kitt.platform.login;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import knight.rider.kitt.platform.bean.QQLoginResp;

public abstract class QQLoginListener implements IUiListener {

    @Override
    public final void onComplete(Object o) {

        String json = o.toString();

        QQLoginResp resp = new QQLoginResp();

        try {
            JSONObject jsonObject = new JSONObject(json);
            int ret = jsonObject.optInt("ret", 0);
            String openid = jsonObject.optString("openid", "");
            String access_token = jsonObject.optString("access_token", "");
            String pay_token = jsonObject.optString("pay_token", "");
            int expires_in = jsonObject.optInt("expires_in", 0);
            String pf = jsonObject.optString("pf", "");
            String pfkey = jsonObject.optString("pfkey", "");
            String msg = jsonObject.optString("msg", "");
            int login_cost = jsonObject.optInt("login_cost", 0);
            int authority_cost = jsonObject.optInt("authority_cost", 0);
            int query_authority_cost = jsonObject.optInt("query_authority_cost", 0);
            long expires_time = jsonObject.optLong("expires_time", 0);

            resp.setAccess_token(access_token);
            resp.setAuthority_cost(authority_cost);
            resp.setExpires_in(expires_in);
            resp.setExpires_time(expires_time);
            resp.setMsg(msg);
            resp.setOpenid(openid);
            resp.setLogin_cost(login_cost);
            resp.setPay_token(pay_token);
            resp.setPf(pf);
            resp.setPfkey(pfkey);
            resp.setQuery_authority_cost(query_authority_cost);
            resp.setRet(ret);

            onLoginComplete(resp);

        } catch (JSONException e) {
            onError(new UiError(-1, "登录信息解析出错", "转换json字符串发生异常"));
        }
    }

    @Override
    public final void onError(UiError uiError) {
        onLoginError(uiError);
    }

    @Override
    public final void onCancel() {
        onLoginCancel();
    }

    public abstract void onLoginComplete(QQLoginResp resp);

    public abstract void onLoginError(UiError uiError);

    public abstract void onLoginCancel();
}
