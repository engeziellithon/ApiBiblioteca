package com.erp.zup.service.user;

import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.repository.IRoleRepository;
import com.erp.zup.repository.IUserRepository;
import com.erp.zup.api.config.notifiable.NotifiableValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService extends NotifiableValidate {

    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private IRoleRepository roleRepo;

    
    public Optional<User> create(User user) {
        checkUserRegistered(user);
        if (isValid())
            return Optional.ofNullable(userRepo.save(user));

        return Optional.empty();
    }

    
    public Optional<User> update(User user) {
        checkUserRegistered(user);
        if (isValid())
            return Optional.ofNullable(userRepo.save(user));

        return Optional.empty();
    }


    
    public void delete(Long id) {
        findById(id);
        if (isValid())
            userRepo.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        Optional<User> user = userRepo.findById(id);
        if (user ==  null || user.isEmpty())
            addNotification("User", "Usuário não encontrado");

        return Optional.ofNullable(user).orElse(Optional.empty());
    }

    
    public User findUserByEmail(String email) {
        User user = userRepo.findByEmailIgnoreCase(email);

        return user;
    }

    
    public Page<User> findAll(Pageable pageable) {
        return userRepo.findAll(pageable);
    }


    protected void checkUserRegistered(User user) {
        User userBD = findUserByEmail(user.getEmail());

        if (userBD != null && user.getId() != userBD.getId()) {
            addNotification("User", "Usuário já cadastrado para o email informado");
            return;
        }

        if(userBD != null && user.getPassword() == null)
            user = new User(user.getId(),user.getName(),user.getEmail(),userBD.getPassword(),user.getRoles());
         else
            user.EncodePassword();


        for (int i = 0; i < user.getRoles().size(); i++) {
            Role roleDB = roleRepo.findByName(user.getRoles().get(i).getName());
            if (roleDB != null)
                user.getRoles().set(i,roleDB);
        }
    }


}
