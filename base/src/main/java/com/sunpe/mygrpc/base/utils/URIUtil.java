package com.sunpe.mygrpc.base.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class URIUtil {

    public static Optional<Map<String, String>> queryParams(URI uri) {
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return Optional.empty();
        }
        String[] pairs = query.split("&");
        Map<String, String> params = new HashMap<>(pairs.length);
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length != 2) {
                continue;
            }
            String key = kv[0];
            String value = kv[1];
            params.put(key, value);
        }
        return Optional.of(params);
    }
}
