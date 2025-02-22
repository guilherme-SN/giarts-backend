package com.giarts.ateliegiarts.security;

import com.giarts.ateliegiarts.exception.UserNotFoundException;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException(username));
        return new UserDetailsImpl(user);
    }
}
