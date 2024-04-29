package com.oleksiity.usersapi.service;

import com.oleksiity.usersapi.controller.payload.CreateUpdateUserPayload;
import com.oleksiity.usersapi.dto.UserDto;
import com.oleksiity.usersapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserService {

    User registerUser(CreateUpdateUserPayload userPayload);

    Page<User> getAllUsers(Pageable pageable);

    User findUserById(long id);

    Page<User> getAllUsersByDateRange(Pageable pageable, LocalDate from, LocalDate to);

    User updateUser(UserDto userDto, long id);

    User replaceUser(CreateUpdateUserPayload userPayload, long id);

    void deleteUserById(long id);


}
