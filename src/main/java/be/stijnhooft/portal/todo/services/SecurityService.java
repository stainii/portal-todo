package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.security.JwtConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Validates a JWT token.
     * Throws an exception if the JWT token is not valid.
     */
    public void validateJtwToken(String jwtToken) throws JwtException {
        Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
