package com.cambofreelance.websiteservice.keycloak.filter;

import com.cambofreelance.websiteservice.keycloak.client.KeycloakResourceClient;
import com.cambofreelance.websiteservice.logger.contants.Constants;
import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.exceptions.AppException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final KeycloakResourceClient keycloakResourceClient;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwt = parseJwt(request);

        if (StringUtils.isNotBlank(jwt)) {
            try {
                var user = keycloakResourceClient.introspectToken(jwt);
                if (StringUtils.isNotEmpty(user.getSub())) {
                    // Create a mutable request so we can inject headers
                    MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
                    mutableRequest.putHeader(Constants.USER_ID, user.getSub());
                    mutableRequest.putHeader(Constants.USERNAME, user.getUsername());
                    mutableRequest.putHeader(Constants.IP, extractClientIp(request));
                    filterChain.doFilter(mutableRequest, response);
                    return;
                } else {
                    log.error("Token introspection failed: user is null");
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }
            } catch (Exception e) {
                log.error("Error introspecting token", e);
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        // If no JWT provided, continue normally
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(Constants.AUTHORIZATION);
        if (org.springframework.util.StringUtils.hasText(headerAuth) && headerAuth.startsWith(Constants.BEARER)) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(Constants.X_FORWARDED_FOR);
        if (StringUtils.isNotBlank(forwardedFor)) {
            // Take first IP in the list
            return forwardedFor.split(",")[0].trim();
        }
        return Optional.ofNullable(request.getRemoteAddr()).orElse(Constants.UNKNOWN);
    }
}
