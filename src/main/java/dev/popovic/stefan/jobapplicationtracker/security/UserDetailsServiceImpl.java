package dev.popovic.stefan.jobapplicationtracker.security;

import dev.popovic.stefan.jobapplicationtracker.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .map(user -> new User(user.getEmail(), user.getPasswordHash(), List.of()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
