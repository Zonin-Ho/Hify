package com.example.demo.utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    // 过期时间30天
    public static final long TOKEN_EXPIRED = 30L * 24 * 60 * 60 * 1000;

    public static final String jwtId = "jwtId";

    public static final String jwtSecret = "jwtSecret12345678910111213141516171819";

    public static String createToken(Map<String, Object> claims) {
        long expMillis = System.currentTimeMillis() + TOKEN_EXPIRED;
        Date exp = new Date(expMillis);

        //生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(key)
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .claims(claims)
                // 设置过期时间
                .expiration(exp);
        return builder.compact();
    }

    public static Claims parseJWT(String token) {

        //生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // 得到DefaultJwtParser
        Jws<Claims> jws = null;
        Claims claims;
        try {
            JwtParser jwtParser = Jwts.parser()
                    // 设置签名的秘钥
                    .verifyWith(key)
                    .build();
            jws = jwtParser.parseSignedClaims(token);
            claims = jws.getPayload();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
}
