package com.zslin.business.wx.tools;

import com.zslin.business.wx.dao.IWxAccountDao;
import com.zslin.core.common.NormalTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 钟述林 393156105@qq.com on 2017/3/1 11:07.
 */
@Component
public class WxAccountTools {

    @Autowired
    private IWxAccountDao wxAccountDao;

    /**
     * 类型
     * 0-游客；
     * 1-学生；
     * 2-学生家长；
     * 5-公司员工；
     * 10-超级管理人员
     */
    public static final String CUSTOMER = "0"; //游客
    public static final String WORKER = "1"; //学生
    public static final String MANAGER = "2"; //学生家长
    public static final String PARTNER = "5"; //公司员工
    public static final String ADMIN = "10"; //超级管理员

    /**
     * 判断type是否为股东或超级管理员
     * @param type 用户类型
     * @return
     */
    public static boolean isPartner(String type) {
        if(PARTNER.equalsIgnoreCase(type) || ADMIN.equalsIgnoreCase(type)) {return true;}
        return false;
    }

    /**
     * 获取所有用户的openid
     * @return
     */
    public List<String> getAllOpenids() {
        return getOpenid(CUSTOMER, WORKER, MANAGER, PARTNER, ADMIN);
    }

    public List<String> getOpenid(String... types) {
        List<String> result = new ArrayList<>();
        for(String type : types) {
            List<String> temp = wxAccountDao.findOpenid(type);
            for(String openid : temp) {
                if(!result.contains(openid)) {result.add(openid);}
            }
        }
        return result;
    }

    /**
     * 判断是否为管理员
     * @param type
     * @return
     */
    public static boolean isAdmin(String type) {
        return ADMIN.equalsIgnoreCase(type);
    }

    /**
     * 生成用户人数相关的事件推送字符串
     * @return
     */
    public String buildAccountStr() {
        Integer allCount = wxAccountDao.findAllCount();
        Integer followCount = wxAccountDao.findFollowCount();
        StringBuffer sb = new StringBuffer();
        sb.append("用户总数：").append(allCount).append(" 人")
                .append("\\n").append("关注人数：").append(followCount).append(" 人")
                .append("\\n").append("今日关注：").
                append(wxAccountDao.findFollowCountByDay(NormalTools.curDate())).append(" 人")
                .append("\\n已取消：").append((allCount-followCount)).append(" 人");
        return sb.toString();
    }
}
