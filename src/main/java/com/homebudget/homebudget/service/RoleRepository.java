package com.homebudget.homebudget.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homebudget.homebudget.model.Role;


@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
