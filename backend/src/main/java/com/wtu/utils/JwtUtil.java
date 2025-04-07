package com.wtu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;
public class JwtUtil {
    public static String createJwt(String secretkey, long ttlMillis, Map<String, Object> claims){//claims中存放需要传入的数据，比如用户id
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //指定过期时间
        long tokenTIme= System.currentTimeMillis()+ttlMillis;

        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(tokenTIme))
                .signWith(signatureAlgorithm, secretkey);

        return jwtBuilder.compact();

    }

    public static Claims parseJwt(String secretkey, String token){

        return Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody();
    }
}
