package com.ljx.file.util;

import com.ljx.file.config.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Date;

/**
 * @Author: ljx
 * @Date: 2023/12/20 13:29
 */
@Component
public class JwtUtil {
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 加密key
     * @return
     */
    private SecretKey generalKey()  {
        byte[] encodeKey = Base64.decodeBase64(jwtProperties.getSecret());
        return new SecretKeySpec(encodeKey,0, encodeKey.length, "AES");
    }

    /**
     * 创建jwt
     * @param subject
     * @return
     * @throws Exception
     */
    public String createJwt(String subject) throws Exception{
        // 获取时间戳
        long now = System.currentTimeMillis();
        Date nowDate = new Date(now);
        // 生成签名的时候使用的秘钥secret
        SecretKey key = generalKey();

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scriptEngine = manager.getEngineByName("js");
        int expireTime = 0;
        try {
            expireTime =(int) scriptEngine.eval(jwtProperties.getPayload().getRegisterClaims().getExp());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        // 为payload添加各种标准声明和私有声明
        DefaultClaims defaultClaims = new DefaultClaims();
        defaultClaims.setIssuer(jwtProperties.getPayload().getRegisterClaims().getIss());
        defaultClaims.setExpiration(new Date(System.currentTimeMillis() + expireTime));
        defaultClaims.setSubject(subject);
        defaultClaims.setAudience(jwtProperties.getPayload().getRegisterClaims().getAud());

        JwtBuilder builder = Jwts.builder() // 表示new一个JwtBuilder，设置jwt的body
                .setClaims(defaultClaims)
                .setIssuedAt(nowDate) // iat(issuedAt)：jwt的签发时间
                .signWith(SignatureAlgorithm.forName(jwtProperties.getHeader().getAlg()), key); // 设置签名，使用的是签名算法和签名使用的秘钥

        return builder.compact();
    }

    /**
     * 解析jwt
     * @param jwt
     * @return
     * @throws Exception
     */
    public Claims parseJWT(String jwt) throws Exception {
        SecretKey key = generalKey(); // 签名秘钥，和生成的签名的秘钥一模一样
        Claims claims = Jwts.parser() // 得到DefaultJwtParser
                .setSigningKey(key) // 设置签名的秘钥
                .parseClaimsJws(jwt).getBody(); // 设置需要解析的jwt
        return claims;
    }
}
