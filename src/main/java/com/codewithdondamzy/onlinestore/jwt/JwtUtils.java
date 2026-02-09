//package com.codewithdondamzy.onlinestore.jwt;
//
//import com.codewithdondamzy.onlinestore.Models.UserPrincipal;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.UnsupportedJwtException;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.MalformedKeyException;
//import jakarta.servlet.http.HttpServletRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtUtils {
//    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
//
//    private static final String JWT_SECRET =
//            "M3J5V0pZQ0pNRnFhY0RjUkR6N0JYMWlZN0JzWmZBQ0J3T1J4R3NIRQ==";
//
//    private final long jwtExpirationMs = 86400000;
//
//    public String JwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization Header: {}", bearerToken);
//        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//    public String generateTokenFromUserName(UserDetails userDetails) {
//        String userName = userDetails.getUsername();
//        return Jwts.builder()
//                .setSubject(userName)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date().getTime() + jwtExpirationMs)))
//                .signWith(getKey())
//                .compact();
//    }
//
//    private SecretKey getKey() {
//        byte[] keyBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//    public String generateJwtToken(UserPrincipal userPrincipal) {
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userName", userPrincipal.getUsername());
//        claims.put("phoneNumber", userPrincipal.getPhoneNumber());
//        claims.put("emailAddress",userPrincipal.getEmailAddress());
//        claims.put("authorities",userPrincipal.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//
//        return Jwts.builder()
//                .subject(userPrincipal.getUsername())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(getKey(), Jwts.SIG.HS256)
//                .claims(claims)
//                .compact();
//    }
//
//    public boolean validateJwtToken(String jwt) {
//        try {
//            System.out.println("Validate ");
//            Jwts.parser().verifyWith((SecretKey) getKey()).build().parseSignedClaims(jwt);
//            return true;
//        } catch (MalformedKeyException e) {
//            logger.error("Invalid Jwt Token: {}", e.getMessage());
//        } catch (ExpiredJwtException e) {
//            logger.error("Jwt token is expired: {}",e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            logger.error("Jwt token is unsupported: {}", e.getMessage());
//        } catch (IllegalArgumentException e) {
//            logger.error("Jwt claims string is empty: {}", e.getMessage());
//        }
//        return false;
//
//    }
//
//    public String getUserNameFromJwtToken(String jwt) {
//        return Jwts.parser().verifyWith((SecretKey) getKey()).build().parseSignedClaims(jwt)
//                .getPayload().getSubject();
//    }
//}
