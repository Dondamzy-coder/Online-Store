package com.codewithdondamzy.onlinestore.config;
import com.codewithdondamzy.onlinestore.Service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final ApplicationContext applicationContext;

    public JwtFilter(JwtService jwtService, ApplicationContext applicationContext) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/OnlineStore/customerLogin")
                || path.startsWith("/OnlineStore/createCustomer")
                || path.startsWith("/OnlineStore/createProduct")
                || path.startsWith("/OnlineStore/createCategory")
                || path.startsWith("/OnlineStore/createPayment")
                || path.startsWith("/OnlineStore/createImage")) {
            filterChain.doFilter(request, response);
            return;
        }
//        To get the "Authorization" header from the request
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String emailAddress = null;
        String phoneNumber = null;

//        checks if the header starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // this takes the token and leaves out the word bearer
            username = jwtService.extractUserName(token);// to read username from token
            emailAddress = jwtService.extractEmail(token);// to read email from token
            phoneNumber = jwtService.extractPhoneNumber(token); // to read phone number from a token

        }

        // if the username is gotten and nobody is logged in yet,check if the token is real and still valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            Get the user details from the database
            UserDetails userDetails = applicationContext.getBean(CustomUserDetailsService.class).loadUserByUsername(username);

//            UserDetails userDetails2 = null;
//            if (emailAddress != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                userDetails2 = applicationContext.getBean(MyUserDetailsService.class).loadUserByUsername(emailAddress);
//            }
            System.out.println("JwtFilter triggered, token: " + authHeader);

//            check if the token is still valid, not expired or fake
            if (jwtService.validateToken(token, userDetails)) {
//                create an authentication ticket for this user
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
//                Add request details to the new token
                authToken.setDetails(new WebAuthenticationDetails(request));
//                tell spring security that this user is now logged in
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
//        let the request continue to the next filter or controller
        filterChain.doFilter(request, response);
    }
}
