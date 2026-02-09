package com.codewithdondamzy.onlinestore.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.jsonwebtoken.Jwts.parser;

@Service
    public class JwtService {

        private static final String secretKey = "vdsbfufdbwefjbsdjkvbsdjvbfdjgbsdyuvsybrjbfdhdvfdmbndjvdsncgcvsgddncb";

        public String generateToken(UserPrincipal userDetails) throws NoSuchAlgorithmException {

            Map<String, Object> claims = new HashMap<>();
            claims.put("emailAddress", userDetails.getEmailAddress());
            claims.put("phoneNumber", userDetails.getPhoneNumber());
            claims.put("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            return Jwts.builder()
                    .claims(claims)
                    .subject(userDetails.getUsername())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 60 * 1000))  // 1 minute
                    .signWith(getKey(), Jwts.SIG.HS512)
                    .compact();
        }


        private SecretKey getKey() {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        }


        public String extractUserName(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public String extractEmail(String token) {
            return extractClaim(token,claims -> claims.get("emailAddress", String.class));

        }
        private<T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }

        private Claims extractAllClaims(String token) {
            return parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

        public boolean validateToken(String token, UserDetails userDetails) {
            final String username = extractUserName(token);
            final  String emailAddress = extractEmail(token);
            return username.equals(userDetails.getUsername());
        }
        private boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }
        private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        public String extractPhoneNumber(String token) {
            return extractClaim(token,claims -> claims.get("phoneNumber", String.class));
        }
    }


