package jp.ac.ttc.webapp.authenticate;

import jp.ac.ttc.webapp.bean.UserBean;


public abstract class NonLoginUserService{
    public static Object getNonLoginUser(){
        UserBean nonLoginUser = new UserBean();
        nonLoginUser.setUserId(-1);
        nonLoginUser.setUserName("ゲスト");
        return nonLoginUser;
    }
}