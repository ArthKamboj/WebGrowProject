package com.example.webgrow.configuration;

import com.example.webgrow.Service.JwtService;
import com.example.webgrow.models.Role;
import com.example.webgrow.models.User;
import com.example.webgrow.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserDetailsService hostDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestPath = request.getServletPath();
        if (requestPath.equals("/api/v1/auth/register") || requestPath.equals("/api/v1/auth/validate") || requestPath.equals("/api/v1/auth/register-host") || requestPath.equals("/api/v1/auth/validate-host")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;


            if (requestPath.startsWith("/api/events")) {
                userDetails = this.hostDetailsService.loadUserByUsername(userEmail);
            } else {
                userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            }

            if(jwtService.isTokenValid(jwt, userDetails)) {

                Optional<User> userOptional=userRepository.findByEmail(userEmail);
                if(userOptional.isPresent()) {
                    User user = userOptional.get();
                    Role role =user.getRole();
                    if (request.getServletPath().startsWith("/api/events") && !role.equals(Role.HOST)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Access Denied: Only HOSTs are allowed to access this endpoint.");
                        return;
                    }
                }
                UsernamePasswordAuthenticationToken authToken  = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
