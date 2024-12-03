package com.achdev.onlinebookstoreapp.mapper.role;

import com.achdev.onlinebookstoreapp.model.Role;
import com.achdev.onlinebookstoreapp.service.RoleService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleSetMapper {
    private final RoleService roleService;

    @RoleMapping
    public Set<Role> map(List<Long> roles) {
        return roles.stream()
                .map(roleService::findById)
                .collect(Collectors.toSet());
    }

}
