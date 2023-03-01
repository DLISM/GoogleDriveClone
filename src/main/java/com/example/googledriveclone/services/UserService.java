package com.example.googledriveclone.services;

import com.example.googledriveclone.dto.UserDto;
import com.example.googledriveclone.mapper.UserMapper;
import com.example.googledriveclone.models.Role;
import com.example.googledriveclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    public boolean createUser(UserDto userDto){

        if(userRepository.existsByUsername(userDto.getUsername())){
            return false;
        }

        var user= userMapper.UserDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);

        return true;
    }
}
