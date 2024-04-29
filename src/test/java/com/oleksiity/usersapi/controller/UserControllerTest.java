package com.oleksiity.usersapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oleksiity.usersapi.UserDataHelper;
import com.oleksiity.usersapi.controller.payload.CreateUpdateUserPayload;
import com.oleksiity.usersapi.dto.UserDto;
import com.oleksiity.usersapi.entity.User;
import com.oleksiity.usersapi.exception.InvalidDateRangeException;
import com.oleksiity.usersapi.mapper.UserMapper;
import com.oleksiity.usersapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully create new user")
    public void testRegisterUser() throws Exception {

        // Create a corresponding User and UserDto
        var user = UserDataHelper.getSergioRamos();
        var userDto = UserDataHelper.convertUserToUserDto(user);

        // Create a sample payload for user registration
        var userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(user);

        // Mock the service and mapper behavior
        when(userService.registerUser(eq(userPayload))).thenReturn(user);
        when(userMapper.toDto(eq(user))).thenReturn(userDto);

        // Test user registration
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPayload)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Sergio"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Ramos"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("sergio@madrid.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Av. de Concha Espina, 1, Chamartín, 28036 Madrid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].birthDate").value("1986-03-30"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phoneNumber").value("0445523499"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.links.ref").value("/api/v1/users/1"));

        // Verify that the service was called once
        verify(userService, times(1)).registerUser(eq(userPayload));
    }

    @Test
    @DisplayName("Should throw MethodArgumentNotValidException when try to create new user, cause by user age is under 18")
    public void testThrowsExceptionWhenRegisterUder18() throws Exception {

        var userPayload = new CreateUpdateUserPayload(
                "sergio@example.com",
                "Lamine",
                "Yamal",
                LocalDate.of(2007, 7, 13),
                "C/ d'Aristides Maillol, 12, Les Corts, 08028 Barcelona",
                "0441234567"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPayload)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Request contains errors"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("The user does not meet the age restrictions"));

        verify(userService, times(0)).registerUser(any(CreateUpdateUserPayload.class));

    }

    @Test
    @DisplayName("Should successfully get user with id 2")
    public void testGetUserById() throws Exception {

        var user = UserDataHelper.getArsenWenger();
        var userDto = UserDataHelper.convertUserToUserDto(user);

        when(userService.findUserById(eq(2L))).thenReturn(user);
        when(userMapper.toDto(eq(user))).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Arsen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Wenger"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("awenger@gunners.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Hornsey Rd, London N7 7AJ"));

        verify(userService, times(1)).findUserById(eq(2L));
    }

    @Test
    @DisplayName("Should successfully get all user list")
    public void testGetAllUsers() throws Exception {
        List<User> users = List.of(UserDataHelper.getSergioRamos(), UserDataHelper.getArsenWenger(), UserDataHelper.getEricCantona());
        var userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);

        when(userService.getAllUsers(any())).thenReturn(userPage);
        when(userMapper.toDto(anyList())).thenReturn(users.stream()
                .map(UserDataHelper::convertUserToUserDto)
                .toList());

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Sergio"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Ramos"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("sergio@madrid.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Av. de Concha Espina, 1, Chamartín, 28036 Madrid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].birthDate").value("1986-03-30"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phoneNumber").value("0445523499"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].firstName").value("Arsen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].lastName").value("Wenger"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].email").value("awenger@gunners.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].address").value("Hornsey Rd, London N7 7AJ"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].birthDate").value("1949-10-22"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].firstName").value("Eric"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].lastName").value("Cantona"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].email").value("cantona@machester.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].address").value("Sir Matt Busby Way, Old Trafford, Stretford, Manchester M16 0RA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].birthDate").value("1966-05-24"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.pageSize").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalElements").value(3));

        verify(userService, times(1)).getAllUsers(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should successfully get users with the custom pagination settings in request and return link on the next page in response")
    public void testGetAllUsersWithCustomPaginationAndNextPageRef() throws Exception {
        var userPage = new PageImpl<>(List.of(UserDataHelper.getSergioRamos()), PageRequest.of(0, 1), 3);

        when(userService.getAllUsers(any())).thenReturn(userPage);
        when(userMapper.toDto(anyList())).thenReturn(List.of(UserDataHelper.convertUserToUserDto(UserDataHelper.getSergioRamos())));

        mockMvc.perform(get("/api/v1/users?pageSize=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Sergio"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Ramos"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("sergio@madrid.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Av. de Concha Espina, 1, Chamartín, 28036 Madrid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].birthDate").value("1986-03-30"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.pageSize").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalPages").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.links.next").value("/api/v1/users?page=1&pageSize=1&sortField=ID&sortDirection=DESC"));

        verify(userService, times(1)).getAllUsers(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should successfully get users with the custom pagination settings in request and return link on the previous page in response")
    public void testGetAllUsersWithCustomPaginationAndPrevPageRef() throws Exception {

        var userPage = new PageImpl<>(List.of(UserDataHelper.getArsenWenger()), PageRequest.of(1, 1), 3);

        when(userService.getAllUsers(any())).thenReturn(userPage);
        when(userMapper.toDto(anyList())).thenReturn(List.of(UserDataHelper.convertUserToUserDto(UserDataHelper.getArsenWenger())));

        mockMvc.perform(get("/api/v1/users?pagepageSize=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Arsen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Wenger"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("awenger@gunners.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Hornsey Rd, London N7 7AJ"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.pageSize").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalPages").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.links.prev").value("/api/v1/users?page=0&pageSize=1&sortField=ID&sortDirection=DESC"));

        verify(userService, times(1)).getAllUsers(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should successfully get users by birth date range functionality")
    public void getUsersByDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(1945, 1, 1);
        LocalDate endDate = LocalDate.of(1965, 1, 1);

        var userPage = new PageImpl<>(List.of(UserDataHelper.getArsenWenger()), PageRequest.of(0, 10), 1);

        when(userService.getAllUsersByDateRange(any(Pageable.class), eq(startDate), eq(endDate)))
                .thenReturn(userPage);
        when(userMapper.toDto(anyList())).thenReturn(List.of(UserDataHelper.convertUserToUserDto(UserDataHelper.getArsenWenger())));

        mockMvc.perform(get("/api/v1/users?startDate=1945-01-01&endDate=1965-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Arsen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Wenger"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("awenger@gunners.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Hornsey Rd, London N7 7AJ"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.pageSize").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pagination.totalElements").value(1));

        verify(userService, times(1)).getAllUsersByDateRange(any(PageRequest.class), eq(startDate), eq(endDate));
    }

    @Test
    @DisplayName("Should throw InvalidDateRangeException when get users by invalid birth date range functionality")
    public void ThrowsInvalidDateRangeExceptionWhenGetUsersByInvalidDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(1945, 1, 1);
        LocalDate endDate = LocalDate.of(1944, 1, 1);

        doThrow(new InvalidDateRangeException())
                .when(userService).getAllUsersByDateRange(any(Pageable.class), eq(startDate), eq(endDate));

        mockMvc.perform(get("/api/v1/users?startDate=1945-01-01&endDate=1944-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Request contains errors"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("The specified date range is not valid. 'startDate' must be less than 'endDate'"));

        verify(userService, times(1)).getAllUsersByDateRange(any(PageRequest.class), eq(startDate), eq(endDate));
    }

    @Test
    @DisplayName("Should successfully replace user by new one")
    public void shouldFullyUpdateUserByPutMethod() throws Exception {

        var user = UserDataHelper.getEricCantona();
        var userDto = UserDataHelper.convertUserToUserDto(user);
        var userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(user);

        when(userService.replaceUser(eq(userPayload), eq(7L))).thenReturn(user);
        when(userMapper.toDto(eq(user))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/{id}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPayload)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Eric"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Cantona"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("cantona@machester.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Sir Matt Busby Way, Old Trafford, Stretford, Manchester M16 0RA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].birthDate").value("1966-05-24"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phoneNumber").value("0994562398"));

        verify(userService, times(1)).replaceUser(eq(userPayload), eq(7L));
    }

    @Test
    @DisplayName("Should successfully update user by calling patch method endpoint")
    public void shouldPartlyUpdateUserByPatchMethod() throws Exception {

        var user = UserDataHelper.getEricCantona();
        user.setLastName("Onana");
        user.setEmail("onana@machester.com");
        user.setPhoneNumber("0965439988");

        var requestDto = new UserDto();
        requestDto.setLastName("Onana");
        requestDto.setEmail("onana@machester.com");
        requestDto.setPhoneNumber("0965439988");
        requestDto.setBirthDate(LocalDate.of(1966, 5, 24));

        var updatedUserDto = UserDataHelper.convertUserToUserDto(user);

        updatedUserDto.setPhoneNumber("0965439988");
        updatedUserDto.setBirthDate(LocalDate.of(1966, 5, 24));

        when(userService.updateUser(eq(requestDto), eq(7L))).thenReturn(user);
        when(userMapper.toDto(eq(user))).thenReturn(updatedUserDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/{id}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Eric"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Onana"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("onana@machester.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("Sir Matt Busby Way, Old Trafford, Stretford, Manchester M16 0RA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].birthDate").value("1966-05-24"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phoneNumber").value("0965439988"));

        verify(userService, times(1)).updateUser(eq(requestDto), eq(7L));
    }

    @Test
    @DisplayName("Should successfully delete user by id")
    public void testDeleteUser() throws Exception {

        mockMvc.perform(delete("/api/v1/users/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(userService, times(1)).deleteUserById(2L);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when try to delete user by an incorrect id")
    public void givenIncorrectId_whenDelete_thenErrorResponse() throws Exception {

        doThrow(new NoSuchElementException("The user with specified id not found"))
                .when(userService).deleteUserById(eq(999L));

        mockMvc.perform(delete("/api/v1/users/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Error 404: Nothing has been found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("The user with specified id not found"));

        verify(userService, times(1)).deleteUserById(999L);

    }
}