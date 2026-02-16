package jp.ac.ttc.webapp.util;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import jp.ac.ttc.webapp.bean.AddressBean;
import jp.ac.ttc.webapp.bean.CartItemBean;

public class MailUtility {

    public static boolean sendOTP(String toEmail, int otp) {
        String host = "smtp.gmail.com";
        final String user = "spiceectester1@gmail.com"; 
        final String password = "oqmwmrpdjledmfpm"; 

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Use jakarta.mail.Authenticator explicitly to avoid confusion
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            // Use jakarta.mail.internet.MimeMessage
            MimeMessage message = new MimeMessage(session);
            
            message.setFrom(new InternetAddress(user, "SpiceEC Support"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Email Change Verification Code");
            message.setText("Your verification code is: " + otp);

            Transport.send(message);
            return true;

        } catch (MessagingException | java.io.UnsupportedEncodingException e) { 
            e.printStackTrace();
            return false;
        }
    }
    
    // 注文完了メール送信メソッド
    public boolean sendOrderConfirmationMail(String toEmail, String userName, AddressBean address, int orderId, java.util.List<CartItemBean> cartItems, int totalPrice) {
        String host = "smtp.gmail.com";
        final String user = "spiceectester1@gmail.com"; 
        final String password = "oqmwmrpdjledmfpm"; 

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user, "SpiceEC Support"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            
            message.setSubject("【SpiceEC】ご注文完了のお知らせ（注文番号：" + orderId + "）", "UTF-8");

            // 本文の組み立て
            StringBuilder body = new StringBuilder();
            body.append(userName).append(" 様\n\n"); // ログインユーザー名
            body.append("この度はSpiceECをご利用いただき、誠にありがとうございます。\n");
            body.append("以下の内容でご注文を承りましたので、ご確認ください。\n\n");
            
            body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            body.append(" ■ ご注文情報\n");
            body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            body.append(" [ 注文番号 ] ").append(orderId).append("\n\n");
            
            body.append(" ■ お届け先\n");
            body.append(" [ お名前 ] ").append(address.getReceiverName()).append(" 様\n"); // お届け先の名前
            body.append(" [ 住所 ] ").append(address.getPostalCode()).append("\n");
            body.append("          ").append(address.getDeliveryAddress()).append("\n");
            body.append(" [ 電話番号 ] ").append(address.getPhoneNumber()).append("\n\n");
            
            body.append(" ■ ご注文明細\n");
            for (CartItemBean item : cartItems) {
                body.append(" ・").append(item.getProduct().getProductName())
                    .append(" (").append(item.getProduct().getPrice()).append("円)")
                    .append(" × ").append(item.getProductQuantity()).append("\n");
            }
            body.append("\n [ 合計金額 ] ").append(totalPrice).append(" 円 (税込)\n");
            
            message.setText(body.toString(), "UTF-8");

            Transport.send(message);
            return true;

        } catch (Exception e) { 
            e.printStackTrace();
            return false;
        }
    }
}