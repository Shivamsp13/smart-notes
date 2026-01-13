package com.shivam.smartnotes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomerUserDetailsService customerUserDetailsService;

    public JWTAuthenticationFilter(
            JWTUtil jwtUtil,
            CustomerUserDetailsService customerUserDetailsService
            )
    {
        this.jwtUtil=jwtUtil;
        this.customerUserDetailsService=customerUserDetailsService;
    }



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        final String authHeader= request.getHeader("Authorization");

        String username=null;
        String token=null;

        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7);

            try{
                if(jwtUtil.validateToken(token)){
                    username= jwtUtil.extractUsername(token);
                }
            }
            catch(Exception e){

            }
        }

        if(username!=null &&
                SecurityContextHolder.getContext().getAuthentication()!=null){

            UserDetails userDetails=
                    customerUserDetailsService.loadUserByUsername(username);



            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );



            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );


            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request,response);

        }

    }
}
