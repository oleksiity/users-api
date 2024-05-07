package com.oleksiity.usersapi.service.impl;

import com.oleksiity.usersapi.UserDataHelper;
import com.oleksiity.usersapi.controller.payload.CreateUpdateUserPayload;
import com.oleksiity.usersapi.controller.payload.UserSortField;
import com.oleksiity.usersapi.exception.InvalidDateRangeException;
import com.oleksiity.usersapi.mapper.UserMapper;
import com.oleksiity.usersapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Successful case of user registration")
    public void shouldSuccessfullyRegisterUser() {
        var userToSave = UserDataHelper.getArsenWenger();
        userToSave.setId(null);
        CreateUpdateUserPayload userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(userToSave);

        when(userMapper.fromPayload(eq(userPayload)))
                .thenReturn(UserDataHelper.getArsenWenger());

        var savedUser = userService.registerUser(userPayload);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(UserDataHelper.getArsenWenger());
        verify(userRepository,times(1)).save(eq(UserDataHelper.getArsenWenger()));
    }

    @Test
    @DisplayName("Successful case of get all users")
    public void shouldSuccessfullyGetAllUsers() {
        var pageable = PageRequest.of(1, 10, Sort.Direction.DESC, UserSortField.ID.getDatabaseFieldName());
        var userPage = new PageImpl<>(List.of(UserDataHelper.getSergioRamos(),UserDataHelper.getArsenWenger(),UserDataHelper.getEricCantona()),
                PageRequest.of(0, 10), 1);

        when(userRepository.findAll(eq(pageable)))
                .thenReturn(userPage);

        var usersResult = userService.getAllUsers(pageable);

        assertThat(usersResult).isNotNull();
        assertThat(usersResult.getTotalElements()).isEqualTo(3);
        assertThat(usersResult.getTotalPages()).isEqualTo(1);
        assertThat(usersResult.getContent().size()).isEqualTo(3);
        assertThat(usersResult.getContent().get(0)).isEqualTo(UserDataHelper.getSergioRamos());
        assertThat(usersResult.getContent().get(1)).isEqualTo(UserDataHelper.getArsenWenger());
        assertThat(usersResult.getContent().get(2)).isEqualTo(UserDataHelper.getEricCantona());
        verify(userRepository, times(1)).findAll(eq(pageable));

    }

    @Test
    @DisplayName("Successful case of find user by id")
    public void shouldSuccessfullyFindUserById() {
        when(userRepository.findById(eq(1L)))
                .thenReturn(Optional.of(UserDataHelper.getSergioRamos()));

        var usersResult = userService.findUserById(1L);

        assertThat(usersResult).isNotNull();
        assertThat(usersResult).isEqualTo(UserDataHelper.getSergioRamos());
        verify(userRepository, times(1)).findById(eq(1L));
    }

    @Test
    @DisplayName("Unsuccessful case of find user by id")
    public void shouldThrowAnExceptionWhenTryToFindUserByInvalidId() {
        when(userRepository.findById(eq(999L)))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class, () -> userService.findUserById(999L)
        );
    }

    @Test
    @DisplayName("Successful case of find users by date range")
    public void shouldSuccessfullyFindUsersByDateRange() {
        var startDate = LocalDate.of(1945, 1, 1);
        var endDate = LocalDate.of(1955, 1, 1);
        var pageable = PageRequest.of(1, 10, Sort.Direction.DESC, UserSortField.ID.getDatabaseFieldName());
        var userPage = new PageImpl<>(List.of(UserDataHelper.getArsenWenger()), PageRequest.of(0, 10), 1);

        when(userRepository.findAllByBirthDateBetween(eq(startDate), eq(endDate), eq(pageable)))
                .thenReturn(userPage);

        var usersResult = userService.getAllUsersByDateRange(pageable, startDate, endDate);

        assertThat(usersResult).isNotNull();
        assertThat(usersResult.getContent()).isNotNull();
        assertThat(usersResult.getContent().size()).isEqualTo(1);
        assertThat(usersResult.getContent().get(0)).isEqualTo(UserDataHelper.getArsenWenger());
        verify(userRepository, times(1)).findAllByBirthDateBetween(eq(startDate), eq(endDate), eq(pageable));
    }

    @Test
    @DisplayName("Unsuccessful case of find users by date range")
    public void shouldThrowAnExceptionWhenTryToFindUsersByInvalidDateRange() {
        var startDate = LocalDate.of(1945, 2, 1);
        var endDate = LocalDate.of(1945, 1, 1);
        var pageable = PageRequest.of(1, 10, Sort.Direction.DESC, UserSortField.ID.getDatabaseFieldName());

        assertThrows(
                InvalidDateRangeException.class, () -> userService.getAllUsersByDateRange(pageable, startDate, endDate)
        );
    }

    @Test
    @DisplayName("Successful case of user update")
    public void shouldSuccessfullyUpdateUser() {
        var userToUpdate = UserDataHelper.getArsenWenger();
        userToUpdate.setEmail("updated@gunners.com");
        userToUpdate.setPhoneNumber("9765432112");
        var userDto = UserDataHelper.convertUserToUserDto(userToUpdate);

        when(userRepository.findById(eq(2L)))
                .thenReturn(Optional.of(UserDataHelper.getArsenWenger()));

        var updateUser = userService.updateUser(userDto, 2L);

        assertThat(updateUser).isNotNull();
        assertThat(updateUser).isEqualTo(userToUpdate);
    }

    @Test
    @DisplayName("Successful case of user replacement")
    public void shouldSuccessfullyReplaceUser() {
        var userToReplace = UserDataHelper.getEricCantona();
        var userDto = UserDataHelper.convertUserToCreateUpdateUserPayload(userToReplace);

        when(userRepository.findById(eq(2L)))
                .thenReturn(Optional.of(UserDataHelper.getArsenWenger()));

        var updateUser = userService.replaceUser(userDto, 2L);

        assertThat(updateUser).isNotNull();
        assertThat(updateUser.getId()).isEqualTo(2L);
        assertThat(updateUser.getEmail()).isEqualTo(userToReplace.getEmail());
        assertThat(updateUser.getFirstName()).isEqualTo(userToReplace.getFirstName());
        assertThat(updateUser.getLastName()).isEqualTo(userToReplace.getLastName());
        assertThat(updateUser.getBirthDate()).isEqualTo(userToReplace.getBirthDate());
        assertThat(updateUser.getAddress()).isEqualTo(userToReplace.getAddress());
        assertThat(updateUser.getPhoneNumber()).isEqualTo(userToReplace.getPhoneNumber());
    }

    @Test
    @DisplayName("Successful case of user delete")
    public void shouldSuccessfullyDeleteUser() {
        when(userRepository.findById(eq(2L)))
                .thenReturn(Optional.of(UserDataHelper.getArsenWenger()));

        userService.deleteUserById(2L);

        verify(userRepository, times(1)).delete(eq(UserDataHelper.getArsenWenger()));
    }
}