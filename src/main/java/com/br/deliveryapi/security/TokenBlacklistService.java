package com.br.deliveryapi.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
