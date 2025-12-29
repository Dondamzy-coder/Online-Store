package com.codewithdondamzy.onlinestore.Repository;

import com.codewithdondamzy.onlinestore.Models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,String> {

    Optional<Admin> findByEmailAddress(String emailAddress);

    Optional<Admin> findByUserName(String userName);
}
