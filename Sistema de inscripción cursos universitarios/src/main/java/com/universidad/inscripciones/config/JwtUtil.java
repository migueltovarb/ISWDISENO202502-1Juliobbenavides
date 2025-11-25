package com.universidad.inscripciones.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${security.jwt.secret:change-me-secret}")
    private String secret;

    public String generate(String subject, long ttlSeconds) {
        long iat = Instant.now().getEpochSecond();
        long exp = iat + ttlSeconds;
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}", escape(subject), iat, exp);
        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String data = header + "." + payload;
        String sig = hmacSha256(data, secret);
        return data + "." + sig;
    }

    public String getSubjectIfValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String data = parts[0] + "." + parts[1];
            String expected = hmacSha256(data, secret);
            if (!constantTimeEquals(expected, parts[2])) return null;
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String sub = extract(payloadJson, "\"sub\":\"", "\"");
            String expStr = extract(payloadJson, "\"exp\":", ",");
            if (expStr == null) expStr = extract(payloadJson, "\"exp\":", "}");
            long exp = Long.parseLong(expStr.trim());
            if (Instant.now().getEpochSecond() >= exp) return null;
            return sub;
        } catch (Exception e) {
            return null;
        }
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(raw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int res = 0;
        for (int i = 0; i < a.length(); i++) res |= a.charAt(i) ^ b.charAt(i);
        return res == 0;
    }

    private String extract(String json, String start, String end) {
        int i = json.indexOf(start);
        if (i < 0) return null;
        int s = i + start.length();
        int e = end == null ? json.length() : json.indexOf(end, s);
        if (e < 0) e = json.length();
        return json.substring(s, e);
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

