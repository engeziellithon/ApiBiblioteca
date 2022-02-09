package com.erp.zup.service.user;

import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.repository.IRoleRepository;
import com.erp.zup.repository.IUserRepository;
import com.erp.zup.service.auth.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service @Transactional
public class UserService implements IUserService, IAuthService {

    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private IRoleRepository roleRepo;

    private PasswordEncoder passwordEncoder;

    @Override
    public User SaveUser(User user) {

        passwordEncoder = new BCryptPasswordEncoder();
        user.EncodePassword(passwordEncoder.encode(user.getPassword()));

        return userRepo.save(user);
    }

    @Override
    public User GetUserById(Long id) {
        Optional<User> userFind = userRepo.findById(id);
        return userFind.orElse(null);
    }

    @Override
    public UserDetails AuthUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmailIgnoreCase(email);
        if (user == null)
           throw new UsernameNotFoundException("User not found");

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public Role SaveRole(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public void SaveRoleToUser(String email,String roleName) {
        User user = userRepo.findByEmailIgnoreCase(email);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User GetUsersByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email);
    }

    @Override
    public List<User> ListUsers() {
        return userRepo.findAll();
    }
}
