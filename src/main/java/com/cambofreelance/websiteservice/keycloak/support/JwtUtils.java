package com.cambofreelance.websiteservice.keycloak.support;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public final class JwtUtils {
    private static final ObjectMapper om = new ObjectMapper();
    private JwtUtils(){}

    public static String subject(String bearer) {
        try {
            String tok = bearer.startsWith("Bearer ") ? bearer.substring(7) : bearer;
            String payload = new String(Base64.getUrlDecoder().decode(tok.split("\\.")[1]));
            Map<?,?> map = om.readValue(payload, Map.class);
            Object sub = map.get("sub");
            return sub == null ? "unknown" : sub.toString();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
