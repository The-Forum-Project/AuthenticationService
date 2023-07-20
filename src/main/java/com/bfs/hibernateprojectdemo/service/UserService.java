package com.bfs.hibernateprojectdemo.service;

import com.bfs.hibernateprojectdemo.dao.UserDao;
import com.bfs.hibernateprojectdemo.domain.User;
import com.bfs.hibernateprojectdemo.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.loadUserByEmail(username);

        if (!userOptional.isPresent()){
            throw new UsernameNotFoundException("User email does not exist");
        }

        User user = userOptional.get(); // database user

        return AuthUserDetail.builder() // spring security's userDetail
                .id(user.getUserId())
                .username(user.getEmail())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .authorities(getAuthoritiesFromUser(user))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    private List<GrantedAuthority> getAuthoritiesFromUser(User user){
        List<GrantedAuthority> userAuthorities = new ArrayList<>();

        if (user.getType() == 0){
            userAuthorities.add(new SimpleGrantedAuthority("super"));
            userAuthorities.add(new SimpleGrantedAuthority("admin"));
            userAuthorities.add(new SimpleGrantedAuthority("normal"));
            userAuthorities.add(new SimpleGrantedAuthority("unverified"));
        } else if (user.getType() == 1) {
            userAuthorities.add(new SimpleGrantedAuthority("admin"));
            userAuthorities.add(new SimpleGrantedAuthority("normal"));
            userAuthorities.add(new SimpleGrantedAuthority("unverified"));
        } else if (user.getType() == 2) {
            userAuthorities.add(new SimpleGrantedAuthority("normal"));
            userAuthorities.add(new SimpleGrantedAuthority("unverified"));
        } else {
            userAuthorities.add(new SimpleGrantedAuthority("unverified"));
        }

        return userAuthorities;
    }

    @Transactional
    public boolean addUser(User user) {
        if (userDao.loadUserByEmail(user.getEmail()).isPresent()){
            return false;
        } else {
            userDao.addUser(user);
            return true;
        }

    }
}
