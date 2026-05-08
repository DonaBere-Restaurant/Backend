package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class BiometricTokenService {

    /**
     * Valida un JWT firmado con una clave pública RSA
     * @param token JWT firmado
     * @param publicKeyPem Clave pública en formato PEM
     * @return Claims del token si es válido
     */
    public Claims validateBiometricToken(String token, String publicKeyPem) {
        try {
            PublicKey publicKey = loadPublicKeyFromPem(publicKeyPem);
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Validar que no esté expirado
            if (claims.getExpiration().before(new Date())) {
                log.warn("Token biométrico expirado");
                throw new IllegalArgumentException("Token expirado");
            }

            return claims;
        } catch (SignatureException e) {
            log.error("Firma inválida en token biométrico: {}", e.getMessage());
            throw new IllegalArgumentException("Firma inválida");
        } catch (Exception e) {
            log.error("Error validando token biométrico: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido: " + e.getMessage());
        }
    }

    /**
     * Carga una clave pública desde formato PEM
     */
    private PublicKey loadPublicKeyFromPem(String publicKeyPem) throws Exception {
        // Remover headers PEM
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        // Decodificar Base64
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);

        // Crear especificación de clave X509
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}
