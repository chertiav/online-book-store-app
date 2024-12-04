package com.achdev.onlinebookstoreapp.repository.role;

import com.achdev.onlinebookstoreapp.model.Role;
import com.achdev.onlinebookstoreapp.model.Role.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
