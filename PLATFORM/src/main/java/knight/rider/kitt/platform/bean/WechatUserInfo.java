package knight.rider.kitt.platform.bean;

import java.util.List;

public class WechatUserInfo {

    /**
     * openid : o8-WlwgsFZO
     * nickname : Qi
     * sex : 1
     * language : zh_CN
     * city :
     * province :
     * country : CR
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/zcsuWTsw9I40kbgleDS5LglQkhUw/132
     * privilege : []
     * unionid : od4u51vqyEU6k
     */

    // 普通用户的标识，对当前开发者帐号唯一
    private String openid;
    // 普通用户昵称
    private String nickname;
    // 普通用户性别，1 为男性，2 为女性
    private String sex;
    // 微信客户端当前语言
    private String language;
    // 普通用户个人资料填写的城市
    private String city;
    // 普通用户个人资料填写的省份
    private String province;
    // 国家，如中国为 CN
    private String country;
    // 用户头像，最后一个数值代表正方形头像大小（有 0、46、64、96、132 数值可选，0 代表 640*640 正方形头像），用户没有头像时该项为空
    private String headimgurl;
    // 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的 unionid 是唯一的。
    private String unionid;
    // 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
    private List<String> privilege;

    public WechatUserInfo(String openid, String nickname, int sex, String language, String city, String province, String country, String headimgurl, String unionid, List<String> privilege) {
        this.openid = openid;
        this.nickname = nickname;
        this.sex = (sex == 1 ? "男" : "女");
        this.language = language;
        this.city = city;
        this.province = province;
        this.country = country;
        this.headimgurl = headimgurl;
        this.unionid = unionid;
        this.privilege = privilege;
    }

    public String getOpenid() {
        return openid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSex() {
        return sex;
    }

    public String getLanguage() {
        return language;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public List<String> getPrivilege() {
        return privilege;
    }

    @Override
    public String toString() {
        return "WechatUserInfo{" +
                "openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", language='" + language + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", unionid='" + unionid + '\'' +
                ", privilege=" + privilege +
                '}';
    }
}
