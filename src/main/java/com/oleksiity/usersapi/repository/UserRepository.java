package com.oleksiity.usersapi.repository;

import com.oleksiity.usersapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    Page<User> findAllByBirthDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
