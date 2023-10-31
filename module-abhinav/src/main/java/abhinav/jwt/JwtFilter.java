package abhinav.jwt;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import abhinav.modal.User;
import abhinav.repo.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

/**
 * The JwtFilter class is responsible for intercepting incoming HTTP requests to validate JWT tokens
 * and set up Spring Security's authentication mechanism for authorized users.
 * It extends OncePerRequestFilter from Spring Security.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    private Claims claims;

    private String userName;


    /**
     * This method filters incoming HTTP requests. If the request is for login or signup, it is allowed to pass
     * without token validation. For other requests, it extracts the JWT token from the "Authorization" header,
     * validates it, and sets up Spring Security's authentication context if the token is valid.
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain for handling the request.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().matches("/user/login|/user/signup")) {
            filterChain.doFilter(request, response);
        } else {
            try {
                String authorizationHeader = request.getHeader("Authorization");

                String token = null;

                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    token = authorizationHeader.substring(7);
                    userName = jwtUtils.extractUsername(token);
                    claims = jwtUtils.extractAllClaims(token);
                }

                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = myUserDetailsService.loadUserByUsername(userName);

                    if(jwtUtils.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(token,null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }

                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Token has expired.\"}");
            } catch (SignatureException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Malformed or invalid token.\"}");
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"No Token Found or invalid token.\"}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Checks if the user associated with the current JWT token is an admin.
     *
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }


    /**
     * Checks if the user associated with the current JWT token is a regular user.
     *
     * @return True if the user is a regular user, false otherwise.
     */
    public boolean isUser() {
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }


    /**
     * Retrieves the username of the current user associated with the JWT token.
     *
     * @return The username of the current user.
     */
    public String getCurrentUser() {
        return userName;
    }

}