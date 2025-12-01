package com.example.rfid.utils;

import android.util.Log;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;
import com.example.rfid.features.auth.dto.Payload;

public class JwtDecoder {
    public static Payload decodeJwt(String token) {
        try {
            JWT jwt = new JWT(token);

            ClaimParser cp = new ClaimParser(jwt);
            return new Payload.Builder()
                    .id(cp.getIntClaim("id"))
                    .email(cp.getStrClaim("email"))
                    .username(cp.getStrClaim("username"))
                    .role(cp.getStrClaim("role"))
                    .department(cp.getStrClaim("department"))
                    .studentNumber(cp.getStrClaim("studentNumber"))
                    .yearLevel(cp.getIntClaim("yearLevel"))
                    .block(cp.getStrClaim("block"))
                    .build();
        } catch (DecodeException e) {
            Log.e("JWT_DECODE", "Failed decoding jwt.", e);
            throw new RuntimeException(e);
        }
    }
    private static class ClaimParser {
        private final JWT jwt;
        public ClaimParser(JWT jwt) {
            this.jwt = jwt;
        }
        public String getStrClaim(String key) {
            return getStrClaim(key, null);
        }
        public String getStrClaim(String key, String defaultVal) {
            String claim = jwt.getClaim(key).asString();
            return claim == null ? defaultVal : claim;
        }
        public Integer getIntClaim(String key) {
            return  getIntClaim(key, null);
        }
        public Integer getIntClaim(String key, Integer defaultVal) {
            Integer claim = jwt.getClaim(key).asInt();
            return claim == null ? defaultVal : claim;
        }
    }
}
