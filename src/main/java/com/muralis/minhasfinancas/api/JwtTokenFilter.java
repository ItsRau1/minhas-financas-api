package com.muralis.minhasfinancas.api;

import com.muralis.minhasfinancas.service.JwtService;
import com.muralis.minhasfinancas.service.impl.SecurityUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityUserDetailsService userDetailsService;

    public JwtTokenFilter(JwtService jwtService, SecurityUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer")) {
            String token = authorization.split(" ")[1];
            boolean isTokenValid = jwtService.isValidToken(token);

            if(isTokenValid) {
                String login = jwtService.getLoginUser(token);
                UserDetails userAuthenticated = userDetailsService.loadUserByUsername(login);
                UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userAuthenticated, null, userAuthenticated.getAuthorities());
                user.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(user);
            }
        }
        filterChain.doFilter(request, response);
    }

}
