package knight.rider.kitt.platform.bean;

/**
 * QQ登录的用户信息
 */
public class QQUserInfo {

    /**
     * ret : 0
     * msg :
     * is_lost : 0
     * nickname :
     * gender : 男
     * province :
     * city :
     * year : 1988
     * constellation :
     * figureurl : http://qzapp.qlogo.cn/qzapp/101795546/69A7A3BFAEC9EE547B3499C36494ED60/30
     * figureurl_1 : http://qzapp.qlogo.cn/qzapp/101795546/69A7A3BFAEC9EE547B3499C36494ED60/50
     * figureurl_2 : http://qzapp.qlogo.cn/qzapp/101795546/69A7A3BFAEC9EE547B3499C36494ED60/100
     * figureurl_qq_1 : http://thirdqq.qlogo.cn/g?b=oidb&k=OobC0jDNOMdobzKGZ5rMfQ&s=40&t=1555297882
     * figureurl_qq_2 : http://thirdqq.qlogo.cn/g?b=oidb&k=OobC0jDNOMdobzKGZ5rMfQ&s=100&t=1555297882
     * figureurl_qq : http://thirdqq.qlogo.cn/g?b=oidb&k=OobC0jDNOMdobzKGZ5rMfQ&s=140&t=1555297882
     * figureurl_type : 1
     * is_yellow_vip : 0
     * vip : 0
     * yellow_vip_level : 0
     * level : 0
     * is_yellow_year_vip : 0
     */

    private int ret;
    private String openid;
    private String msg;
    private int is_lost;
    private String nickname;
    private String gender;
    private String province;
    private String city;
    private String year;
    private String constellation;
    private String figureurl;
    private String figureurl_1;
    private String figureurl_2;
    private String figureurl_qq_1;
    private String figureurl_qq_2;
    private String figureurl_qq;
    private String figureurl_type;
    private String is_yellow_vip;
    private String vip;
    private String yellow_vip_level;
    private String level;
    private String is_yellow_year_vip;
    private String unionId;

    public QQUserInfo(int ret, String openid, String msg, int is_lost, String nickname, String gender, String province, String city, String year, String constellation, String figureurl, String figureurl_1, String figureurl_2, String figureurl_qq_1, String figureurl_qq_2, String figureurl_qq, String figureurl_type, String is_yellow_vip, String vip, String yellow_vip_level, String level, String is_yellow_year_vip, String unionId) {
        this.ret = ret;
        this.openid = openid;
        this.msg = msg;
        this.is_lost = is_lost;
        this.nickname = nickname;
        this.gender = gender;
        this.province = province;
        this.city = city;
        this.year = year;
        this.constellation = constellation;
        this.figureurl = figureurl;
        this.figureurl_1 = figureurl_1;
        this.figureurl_2 = figureurl_2;
        this.figureurl_qq_1 = figureurl_qq_1;
        this.figureurl_qq_2 = figureurl_qq_2;
        this.figureurl_qq = figureurl_qq;
        this.figureurl_type = figureurl_type;
        this.is_yellow_vip = is_yellow_vip;
        this.vip = vip;
        this.yellow_vip_level = yellow_vip_level;
        this.level = level;
        this.is_yellow_year_vip = is_yellow_year_vip;
        this.unionId = unionId;
    }

    public int getRet() {
        return ret;
    }

    public String getOpenid() {
        return openid;
    }

    public String getMsg() {
        return msg;
    }

    public int getIs_lost() {
        return is_lost;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGender() {
        return gender;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getYear() {
        return year;
    }

    public String getConstellation() {
        return constellation;
    }

    public String getFigureurl() {
        return figureurl;
    }

    public String getFigureurl_1() {
        return figureurl_1;
    }

    public String getFigureurl_2() {
        return figureurl_2;
    }

    public String getFigureurl_qq_1() {
        return figureurl_qq_1;
    }

    public String getFigureurl_qq_2() {
        return figureurl_qq_2;
    }

    public String getFigureurl_qq() {
        return figureurl_qq;
    }

    public String getFigureurl_type() {
        return figureurl_type;
    }

    public String getIs_yellow_vip() {
        return is_yellow_vip;
    }

    public String getVip() {
        return vip;
    }

    public String getYellow_vip_level() {
        return yellow_vip_level;
    }

    public String getLevel() {
        return level;
    }

    public String getIs_yellow_year_vip() {
        return is_yellow_year_vip;
    }

    public String getUnionId() {
        return unionId;
    }

    @Override
    public String toString() {
        return "QQUserInfo{" +
                "ret=" + ret +
                ", openid='" + openid + '\'' +
                ", msg='" + msg + '\'' +
                ", is_lost=" + is_lost +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", year='" + year + '\'' +
                ", constellation='" + constellation + '\'' +
                ", figureurl='" + figureurl + '\'' +
                ", figureurl_1='" + figureurl_1 + '\'' +
                ", figureurl_2='" + figureurl_2 + '\'' +
                ", figureurl_qq_1='" + figureurl_qq_1 + '\'' +
                ", figureurl_qq_2='" + figureurl_qq_2 + '\'' +
                ", figureurl_qq='" + figureurl_qq + '\'' +
                ", figureurl_type='" + figureurl_type + '\'' +
                ", is_yellow_vip='" + is_yellow_vip + '\'' +
                ", vip='" + vip + '\'' +
                ", yellow_vip_level='" + yellow_vip_level + '\'' +
                ", level='" + level + '\'' +
                ", is_yellow_year_vip='" + is_yellow_year_vip + '\'' +
                ", unionId='" + unionId + '\'' +
                '}';
    }
}
