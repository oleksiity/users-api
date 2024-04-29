package com.oleksiity.usersapi.service.impl;

import com.oleksiity.usersapi.controller.payload.CreateUpdateUserPayload;
import com.oleksiity.usersapi.dto.UserDto;
import com.oleksiity.usersapi.entity.User;
import com.oleksiity.usersapi.exception.InvalidDateRangeException;
import com.oleksiity.usersapi.mapper.UserMapper;
import com.oleksiity.usersapi.repository.UserRepository;
import com.oleksiity.usersapi.service.UserService;
import com.oleksiity.usersapi.validation.NullUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public User registerUser(CreateUpdateUserPayload userPayload) {
        var user = userMapper.fromPayload(userPayload);
        userRepository.save(user);
        log.info("User with email {} has been saved", user.getEmail());

        return user;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findUserById(long id) {
        var optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            log.info("User with id {} can not be found", id);
            throw new NoSuchElementException("users-api.users.errors.user_not_found");
        }
        return optionalUser.get();
    }

    @Override
    public Page<User> getAllUsersByDateRange(Pageable pageable, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException();
        }
        return userRepository.findAllByBirthDateBetween(from, to, pageable);
    }

    @Override
    @Transactional
    public User updateUser(UserDto userDto, long id) {
        var user = findUserById(id);

        NullUtils.updateIfChanged(user::setFirstName, userDto.getFirstName(), user::getFirstName);
        NullUtils.updateIfChanged(user::setLastName, userDto.getLastName(), user::getLastName);
        NullUtils.updateIfChanged(user::setEmail, userDto.getEmail(), user::getEmail);
        NullUtils.updateIfChanged(user::setAddress, userDto.getAddress(), user::getAddress);
        NullUtils.updateIfChanged(user::setBirthDate, userDto.getBirthDate(), user::getBirthDate);
        NullUtils.updateIfChanged(user::setPhoneNumber, userDto.getPhoneNumber(), user::getPhoneNumber);
        log.info("User with id {} has been partly updated", user.getId());

        return user;
    }

    @Override
    @Transactional
    public User replaceUser(CreateUpdateUserPayload userPayload, long id) {
        var user = findUserById(id);
        user.setAddress(userPayload.address());
        user.setEmail(userPayload.email());
        user.setBirthDate(userPayload.birthDate());
        user.setPhoneNumber(userPayload.phoneNumber());
        user.setFirstName(userPayload.firstName());
        user.setLastName(userPayload.lastName());
        log.info("User with id {} has been fully updated", user.getId());

        return user;
    }

    @Override
    public void deleteUserById(long id) {
        var user = findUserById(id);
        userRepository.delete(user);
        log.info("User with id {} has been deleted", user.getId());
    }


}
