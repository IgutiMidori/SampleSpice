package jp.ac.ttc.webapp.authenticate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.dao.UserDao;

public class AuthenticateService {
    public UserBean authenticateUser(String name, String password, String email) {
        UserBean user = new UserBean();
        // ユーザー認証ロジックをここに実装
        // 例: データベースからユーザー情報を取得し、name, password, emailを検証する
        UserDao userDao = new UserDao();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user = userDao.findByNameAndTel(name, email);
        System.out.println("ユーザー認証サービス内："+user);
        if (user != null && encoder.matches(password, user.getPasswordHash())) {
            System.out.println("パスワード一致");
            user.setRole("USER");
            return user;
        }
            

        return null;
    }
}
