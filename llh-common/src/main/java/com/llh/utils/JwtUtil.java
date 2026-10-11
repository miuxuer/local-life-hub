package com.llh.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        SecretKey key = getKey(secretKey);

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // jjwt 0.12 写法：claims(...) 装负载、signWith(Key) 签名、expiration(...) 设置过期
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .expiration(exp)
                .compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        SecretKey key = getKey(secretKey);

        // jjwt 0.12 写法：verifyWith(key).build() 构造解析器，parseSignedClaims 解析，getPayload 取负载
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 构造 HS256 密钥。
     * jjwt 0.12 的 signWith(SecretKey) 会按密钥长度推断算法，HS256 要求密钥 ≥256 位(32 字节)，
     * 这里用 SHA-256 把任意长度密钥派生为定长 32 字节，满足强度要求，
     * 签发与校验使用同一派生密钥，行为一致。
     */
    private static SecretKey getKey(String secretKey) {
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(secretKey.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
