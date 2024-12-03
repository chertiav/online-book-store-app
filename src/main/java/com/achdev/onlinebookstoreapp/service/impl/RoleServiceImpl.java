package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.model.Role;
import com.achdev.onlinebookstoreapp.repository.role.RoleRepository;
import com.achdev.onlinebookstoreapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find role by id: " + id));
    }
}
