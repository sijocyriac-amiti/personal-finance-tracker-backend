package com.example.financetracker.security;

import com.example.financetracker.domain.User;
import com.example.financetracker.repository.UserRepository;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        String normalizedIdentifier = identifier == null ? null : identifier.trim().toLowerCase();
        User user = userRepository.findByUsernameOrEmail(normalizedIdentifier, normalizedIdentifier)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singleton(authority)
        );
    }
}
