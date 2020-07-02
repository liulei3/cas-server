package com.zzlh.auth.util;

import java.security.Signature;
import java.util.Date;
import java.util.regex.Pattern;
import javax.security.cert.X509Certificate;
import org.springframework.security.crypto.codec.Base64;

/**
 * @Description 工具类
 * @author liulei
 * @date 2018年10月15日 上午9:24:56
 */
public class AssitUtil {
	 /**
	  * @Description 是否简单密码 必须同时包含数字与大小字母
	  * @param password
	  * @return
	  */
    public static boolean isSimple(String password) {
        Pattern pattern = Pattern.compile("(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]*");
        return !pattern.matcher(password).find();
    }

    /**
     * @Description 计算相差多少天
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    public static int getDatePoor(Date start, Date end) {
        long nd = 1000 * 24 * 60 * 60;
        long diff = start.getTime() - end.getTime();
        long day = diff / nd;
        return (int) day;
    }
    
    /**
     * @Description 校验签名
     * @param instance 证书
     * @param data 原始数据
     * @param sign 签名数据
     * @return
     * @throws Exception
     */
    public static boolean verifySign(X509Certificate instance,byte[] data,String sign) throws Exception {
		Signature signature = Signature.getInstance(instance.getSigAlgName());
		signature.initVerify(instance.getPublicKey());
		signature.update(data);
    	return signature.verify(Base64.decode(sign.getBytes()));
    }
}
