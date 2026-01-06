package com.br.deliveryapi.security;

import com.br.deliveryapi.entity.Admin;
import com.br.deliveryapi.entity.Client;
import com.br.deliveryapi.service.AdminService;
import com.br.deliveryapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientService clientService;
    private final AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Admin admin = adminService.findByEmail(email);
        if (admin != null) {
            return AuthUserDetails.fromUser(admin);
        }

        Client client = clientService.findByEmail(email);
        if (client != null) {
            return AuthUserDetails.fromUser(client);
        }

        throw new UsernameNotFoundException("User Not Found!");
    }
}
