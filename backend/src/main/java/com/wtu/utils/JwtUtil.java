package com.wtu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;
public class JwtUtil {
    public static String createJwt(String secretKey, long ttlMillis, Map<String, Object> claims){//claims中存放需要传入的数据，比如用户id
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //指定过期时间
        long tokenTIme= System.currentTimeMillis()+ttlMillis;
        // 将字符串 secretKey 转换为 SecretKey 对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), signatureAlgorithm.getJcaName());


        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(tokenTIme))
                .signWith(signatureAlgorithm, secretKeySpec);

        return jwtBuilder.compact();

    }

    public static Claims parseJwt(String secretKey, String token){

        // 将字符串 secretKey 转换为 SecretKey 对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());

        return Jwts.parser().setSigningKey(secretKeySpec).parseClaimsJws(token).getBody();
    }
}
