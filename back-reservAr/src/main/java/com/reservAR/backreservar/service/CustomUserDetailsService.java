package com.reservAR.backreservar.service;

import com.reservAR.backreservar.exception.UserNotFoundException;
import com.reservAR.backreservar.model.CustomUserDetails;
import com.reservAR.backreservar.model.User;
import com.reservAR.backreservar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = username.trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(normalized)
                .orElseThrow(()->new UserNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }

}
