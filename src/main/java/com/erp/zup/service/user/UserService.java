package com.erp.zup.service.user;

import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.repository.IRoleRepository;
import com.erp.zup.repository.IUserRepository;
import jflunt.notifications.Notifiable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Optional<User> create(User user) {
        checkUserRegistered(user);
        if (isValid())
            return Optional.ofNullable(userRepo.save(user));

        return Optional.empty();
    }

    @Override
    public Optional<User> update(User user) {
        checkUserRegistered(user);
        if (isValid())
            return Optional.ofNullable(userRepo.save(user));

        return Optional.empty();
    }


    @Override
    public void delete(Long id) {
        findById(id);
        if (isValid())
            userRepo.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        Optional<User> user = userRepo.findById(id);
        if (user ==  null || user.isEmpty()){
            addNotification("User", "Usuário não encontrado.");
            return Optional.empty();
        }

        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepo.findAll(pageable);
    }


    protected void checkUserRegistered(User user) {
        User userBD = findUserByEmail(user.getEmail());

        if (userBD != null && user.getId() != userBD.getId()) {
            addNotification("User", "Usuário já cadastrado para o email informado.");
            return;
        }

        if(userBD != null && user.getPassword() == null)
            user = new User(user.getId(),user.getName(),user.getEmail(),userBD.getPassword(),user.getRoles());
         else
            user.EncodePassword(user.getPassword());

        for (Role role : user.getRoles()) {
            Role roleDB = roleRepo.findByName(role.getName());
            if (roleDB != null)
                role = roleDB;
        }
    }


}
