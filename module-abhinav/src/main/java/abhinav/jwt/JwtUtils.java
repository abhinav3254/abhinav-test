package abhinav.jwt;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * The JwtUtils class is responsible for handling JSON Web Tokens (JWT) in the
 * application. It provides methods to generate, validate, and extract
 * information from JWT tokens.
 */
@Service
public class JwtUtils {
    private String SECRET = "abhinav";

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }


    /**
     * Checks if a JWT token is expired.
     *
     * @param token The JWT token.
     * @return True if the token is expired, false otherwise.
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates a JWT token by comparing it to the user details.
     *
     * @param token       The JWT token.
     * @param userDetails The user details.
     * @return True if the token is valid for the user, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    /**
     * Generates a JWT token for a given username and role.
     *
     * @param username The username to include in the token.
     * @param role     The user's role to include in the token.
     * @return The generated JWT token.
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    /**
     * Extracts claims from a JWT token using a given claims resolver.
     *
     * @param token         The JWT token.
     * @param claimsResolver The claims resolver function.
     * @param <T>           The type of claims to extract.
     * @return The extracted claims.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)) // 1 month validity
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

    }

    /**
     * Extracts claims from a JWT token using the provided claims resolver function.
     *
     * @param token          The JWT token.
     * @param claimsResolver The claims resolver function.
     * @param <T>            The type of claims to extract.
     * @return The extracted claims of the specified type.
     */
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    /**
     * Parses all claims from a JWT token's body.
     *
     * @param token The JWT token.
     * @return All claims extracted from the token's body.
     */
    public Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();

    }
}