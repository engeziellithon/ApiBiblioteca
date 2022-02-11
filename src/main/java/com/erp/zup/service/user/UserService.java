package com.erp.zup.service.user;

import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.repository.IRoleRepository;
import com.erp.zup.repository.IUserRepository;
import jflunt.notifications.Notifiable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService extends Notifiable implements IUserService {

    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private IRoleRepository roleRepo;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User create(User user) {
        checkUserRegistered(user);
        if (!isValid())
            return user;

        return userRepo.save(user);
    }

    @Override
    public User update(User user) {
        checkUserRegistered(user);
        if (!isValid())
            return null;

        return userRepo.save(user);
    }


    @Override
    public void delete(Long id) {
        findById(id);
        if (isValid())
            userRepo.deleteById(id);
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isEmpty())
            addNotification("User", "Não encontrado");

        return user.orElse(null);
    }


    @Override
    public Role createRole(Role role) {
        Role roleBD = roleRepo.findByName(role.getName());

        if (roleBD != null && !role.getId().equals(roleBD.getId())) {
            addNotification("Role", "Função já cadastrada");
            return role;
        }

        return roleRepo.save(role);
    }

    @Override
    public void saveRoleToUser(String email, String roleName) {
        User user = userRepo.findByEmailIgnoreCase(email);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    //
    private void checkUserRegistered(User user) {
        User userBD = findUserByEmail(user.getEmail());

        if (userBD != null && !user.getId().equals(userBD.getId())) {
            addNotification("User", "Usuário já cadastrado para o email informado");
            return;
        }

        if (user.getPassword() != null)
            user.EncodePassword(passwordEncoder.encode(user.getPassword()));

        for (Role role : user.getRoles()) {
            Role roleDB = roleRepo.findByName(role.getName());
            if (roleDB != null)
                role.setId(roleDB.getId());
        }
    }
}
