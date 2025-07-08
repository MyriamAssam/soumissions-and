package com.example.app_soumission.utils;
import com.example.app_soumission.utils.JwtUtils;

import android.util.Base64;
import org.json.JSONObject;

public class JwtUtils {
    public static JSONObject decodeJWT(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Token JWT invalide");
            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE);
            return new JSONObject(new String(decodedBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
