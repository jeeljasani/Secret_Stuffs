package secretstuffs.application.helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtHelper {

    private static final long TOKEN_VALIDITY_MILLISECONDS = 1000L * 60 * 60 * 10; // 10 hours

    private final Key signingKey;

    public JwtHelper(@Value("${jwt.secret}") String secretKey) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Extracts the subject (typically username) from the token.
     *
     * @param token the JWT token
     * @return the subject extracted from the token
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the token using the provided resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver a function to resolve a specific claim from the token
     * @param <T>            the type of the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the token.
     *
     * @param token the JWT token
     * @return all claims in the token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * Checks if the token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true; // Assume expired if parsing fails
        }
    }

    /**
     * Validates the token by checking its expiration and extracting the subject.
     *
     * @param token    the JWT token
     * @param username the username to match with the token's subject
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, String username) {
        try {
            String extractedSubject = extractSubject(token);
            return extractedSubject.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a token for the given subject with no additional claims.
     *
     * @param subject the subject for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(String subject) {
        return generateTokenWithClaims(Map.of(), subject);
    }

    /**
     * Generates a token for the given subject with additional claims.
     *
     * @param claims  the additional claims to include in the token
     * @param subject the subject for which the token is generated
     * @return the generated JWT token
     */
    public String generateTokenWithClaims(Map<String, Object> claims, String subject) {
        return buildToken(claims, subject, getCurrentDate(), getExpirationDate());
    }

    /**
     * Builds the token using the provided data.
     *
     * @param claims     the additional claims to include
     * @param subject    the subject of the token
     * @param issuedAt   the issued-at timestamp
     * @param expiration the expiration timestamp
     * @return the generated JWT token
     */
    private String buildToken(Map<String, Object> claims, String subject, Date issuedAt, Date expiration) {
        JwtBuilder jwtBuilder =  Jwts.builder();
        jwtBuilder.setClaims(claims);
        jwtBuilder.setSubject(subject);
        jwtBuilder.setIssuedAt(issuedAt);
        jwtBuilder.setExpiration(expiration);
        jwtBuilder.signWith(signingKey, SignatureAlgorithm.HS512);

        return jwtBuilder.compact();
    }

    /**
     * Helper method to get the current date.
     *
     * @return the current date
     */
    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Helper method to calculate the expiration date based on the token validity.
     *
     * @return the expiration date
     */
    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + TOKEN_VALIDITY_MILLISECONDS);
    }

    /**
     * Returns whether the token is valid based on its expiration.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the subject only if the token is valid.
     *
     * @param token the JWT token
     * @return the subject, or null if the token is invalid
     */
    public String getValidSubject(String token) {
        return isTokenValid(token) ? extractSubject(token) : null;
    }
}