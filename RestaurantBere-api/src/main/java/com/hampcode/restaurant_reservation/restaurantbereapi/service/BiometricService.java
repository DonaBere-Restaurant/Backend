package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AuthResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.BiometricLoginDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.RegisterFingerprintDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.security.TokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiometricService {

    private final UserRepository userRepository;
    private final BiometricTokenService biometricTokenService;
    private final TokenProvider tokenProvider;

    @Value("${jwt.validity-in-seconds}")
    private long jwtValidityInSeconds;

    @Transactional
    public AuthResponseDTO registerFingerprint(RegisterFingerprintDTO dto) {
        try {
            log.info("=== INICIANDO REGISTRO DE HUELLA ===");
            log.info("DTO recibido - email: {}, publicKey length: {}", dto.getEmail(), 
                    dto.getPublicKey() != null ? dto.getPublicKey().length() : "NULL");
            
            Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
            
            if (userOptional.isEmpty()) {
                log.error("❌ USUARIO NO ENCONTRADO para email: {}", dto.getEmail());
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            User user = userOptional.get();
            log.info("✓ Usuario encontrado: {}, user_id: {}", user.getEmail(), user.getId());
            log.info("  ANTES: fingerprint_enabled={}, fingerprint_public_key=null?{}", 
                    user.getFingerprintEnabled(), user.getFingerprintPublicKey() == null);
            
            // Guardar la clave pública
            user.setFingerprintPublicKey(dto.getPublicKey());
            user.setFingerprintEnabled(true);
            
            log.info("✓ Atributos seteados en memoria: enabled={}, key_length={}", 
                    user.getFingerprintEnabled(), 
                    user.getFingerprintPublicKey() != null ? user.getFingerprintPublicKey().length() : "NULL");
            
            // Usar saveAndFlush para garantizar que se persista inmediatamente
            log.info("  → Ejecutando saveAndFlush()...");
            User savedUser = userRepository.saveAndFlush(user);
            
            log.info("✓ SAVEANDFLUSH COMPLETADO");
            log.info("  DESPUÉS DE FLUSH: fingerprint_enabled={}, fingerprint_public_key=null?{}", 
                    savedUser.getFingerprintEnabled(), savedUser.getFingerprintPublicKey() == null);
            
            // Verificación post-flush
            Optional<User> userFromDb = userRepository.findByEmail(dto.getEmail());
            if (userFromDb.isPresent()) {
                User dbUser = userFromDb.get();
                log.info("✓ VERIFICACIÓN EN BD INMEDIATA:");
                log.info("  fingerprint_enabled={}, fingerprint_public_key=null?{}", 
                        dbUser.getFingerprintEnabled(), dbUser.getFingerprintPublicKey() == null);
                if (dbUser.getFingerprintPublicKey() != null) {
                    log.info("  ✓ CLAVE PÚBLICA EXISTE - Primera línea: {}", 
                            dbUser.getFingerprintPublicKey().substring(0, Math.min(50, dbUser.getFingerprintPublicKey().length())));
                }
            }
            
            log.info("Huella registrada exitosamente para usuario: {} - PublicKey length: {}", 
                    savedUser.getEmail(), dto.getPublicKey() != null ? dto.getPublicKey().length() : 0);

            // Generar un nuevo token de sesión
            String token = generateSessionToken(savedUser);
            log.info("✓ Token generado exitosamente");

            AuthResponseDTO response = new AuthResponseDTO();
            response.setToken(token);
            return response;
        } catch (Exception e) {
            log.error("❌ ERROR AL REGISTRAR HUELLA: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar huella: " + e.getMessage());
        }
    }

    /**
     * Realiza login biométrico validando el JWT firmado
     */
    @Transactional(readOnly = true)
    public AuthResponseDTO biometricLogin(BiometricLoginDTO dto) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
            
            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            User user = userOptional.get();

            // Verificar que tiene huella habilitada
            if (!user.getFingerprintEnabled() || user.getFingerprintPublicKey() == null) {
                throw new IllegalArgumentException("Usuario no tiene huella registrada");
            }

            // Validar el token biométrico firmado
            Claims claims = biometricTokenService.validateBiometricToken(
                    dto.getBiometricToken(),
                    user.getFingerprintPublicKey()
            );

            // Verificar que el email del token coincide
            if (!claims.getSubject().equals(dto.getEmail())) {
                throw new IllegalArgumentException("Email del token no coincide");
            }

            log.info("Login biométrico exitoso para usuario: {}", user.getEmail());

            // Generar token de sesión
            String sessionToken = generateSessionToken(user);

            AuthResponseDTO response = new AuthResponseDTO();
            response.setToken(sessionToken);
            return response;
        } catch (IllegalArgumentException e) {
            log.warn("Error en login biométrico: {}", e.getMessage());
            throw new RuntimeException("Error en login biométrico: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en login biométrico: {}", e.getMessage());
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Genera un token de sesión JWT
     */
    private String generateSessionToken(User user) {
        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getName())
        );
        var authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities
        );
        return tokenProvider.createAccessToken(authentication);
    }
}
