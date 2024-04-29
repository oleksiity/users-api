package com.oleksiity.usersapi.controller.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oleksiity.usersapi.UserDataHelper;
import com.oleksiity.usersapi.entity.User;
import com.oleksiity.usersapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ReturnCreatedUserAndRefForEndpoint() throws Exception {
        var userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(UserDataHelper.getArsenWenger());


        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPayload));
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                 {
                                    "data": [
                                        {
                                            "email": "awenger@gunners.com",
                                            "firstName": "Arsen",
                                            "lastName": "Wenger",
                                            "birthDate": "1949-10-22",
                                            "address": "Hornsey Rd, London N7 7AJ"
                                        }
                                    ],
                                    "links": {
                                        "ref": "/api/v1/users/1"
                                    }
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void getUsers_ReturnsUsersList() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/users");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                 {
                                    "pagination": {
                                        "page": 0,
                                        "pageSize": 10,
                                        "totalPages": 1,
                                        "totalElements": 3
                                    },
                                    "data": [
                                        {
                                            "email": "cantona@machester.com",
                                            "firstName": "Eric",
                                            "lastName": "Cantona",
                                            "birthDate": "1966-05-24",
                                            "address": "Sir Matt Busby Way, Old Trafford, Stretford, Manchester M16 0RA",
                                            "phoneNumber": "0994562398"
                                        },
                                        {
                                            "email": "awenger@gunners.com",
                                            "firstName": "Arsen",
                                            "lastName": "Wenger",
                                            "birthDate": "1949-10-22",
                                            "address": "Hornsey Rd, London N7 7AJ"
                                        },
                                        {
                                            "email": "sergio@madrid.com",
                                            "firstName": "Sergio",
                                            "lastName": "Ramos",
                                            "birthDate": "1986-03-30",
                                            "address": "Av. de Concha Espina, 1, Chamartín, 28036 Madrid",
                                            "phoneNumber": "0445523499"
                                        }
                                    ]
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void getUserById_ReturnsUser() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/users/{id}", 1);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                 {
                                    "data": [
                                        {
                                            "email": "sergio@madrid.com",
                                            "firstName": "Sergio",
                                            "lastName": "Ramos",
                                            "birthDate": "1986-03-30",
                                            "address": "Av. de Concha Espina, 1, Chamartín, 28036 Madrid",
                                            "phoneNumber": "0445523499"
                                        }
                                    ]
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void getUsersWithCustomPageSize2AndAscSorting_ReturnsPageWithListOfTwoUsersAndNextPageRef() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/users?pageSize=2&sortDirection=ASC");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "pagination": {
                                        "page": 0,
                                        "pageSize": 2,
                                        "totalPages": 2,
                                        "totalElements": 3
                                    },
                                    "data": [
                                       {
                                           "email": "sergio@madrid.com",
                                           "firstName": "Sergio",
                                           "lastName": "Ramos",
                                           "birthDate": "1986-03-30",
                                           "address": "Av. de Concha Espina, 1, Chamartín, 28036 Madrid",
                                           "phoneNumber": "0445523499"
                                       },
                                       {
                                           "email": "awenger@gunners.com",
                                           "firstName": "Arsen",
                                           "lastName": "Wenger",
                                           "birthDate": "1949-10-22",
                                           "address": "Hornsey Rd, London N7 7AJ"
                                       }
                                    ],
                                    "links": {
                                        "next": "/api/v1/users?page=1&pageSize=2&sortField=ID&sortDirection=ASC"
                                    }
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void findUsersByDateRange_ReturnsPageWithListOfUsersAcceptableByDateRange() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/users?startDate=1945-01-01&endDate=1966-07-01");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "pagination": {
                                        "page": 0,
                                        "pageSize": 10,
                                        "totalPages": 1,
                                        "totalElements": 2
                                    },
                                    "data": [
                                       {
                                           "email": "cantona@machester.com",
                                           "firstName": "Eric",
                                           "lastName": "Cantona",
                                           "birthDate": "1966-05-24",
                                           "address": "Sir Matt Busby Way, Old Trafford, Stretford, Manchester M16 0RA",
                                           "phoneNumber": "0994562398"
                                       },
                                       {
                                           "email": "awenger@gunners.com",
                                           "firstName": "Arsen",
                                           "lastName": "Wenger",
                                           "birthDate": "1949-10-22",
                                           "address": "Hornsey Rd, London N7 7AJ"
                                       }
                                    ]
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void putUpdateUser_ReturnUpdatedUser() throws Exception {
        var user = User.builder()
                .firstName("Gerd")
                .lastName("Muller")
                .email("gerdmuller@example.com")
                .birthDate(LocalDate.of(1945, 3, 11))
                .build();

        var updatePayload = UserDataHelper.convertUserToCreateUpdateUserPayload(user);


        var requestBuilder = MockMvcRequestBuilders.put("/api/v1/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload));
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                 {
                                    "data": [
                                        {
                                            "email": "gerdmuller@example.com",
                                            "firstName": "Gerd",
                                            "lastName": "Muller",
                                            "birthDate": "1945-03-11"
                                        }
                                    ],
                                    "links": {
                                        "ref": "/api/v1/users/1"
                                    }
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void patchUpdateUser_ReturnUpdatedUser() throws Exception {
        var user = User.builder()
                .email("newemail@example.com")
                .phoneNumber("0543217766")
                .build();

        var userDto = UserDataHelper.convertUserToUserDto(user);


        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/users/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto));
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                 {
                                    "data": [
                                        {
                                            "email": "newemail@example.com",
                                            "firstName": "Arsen",
                                            "lastName": "Wenger",
                                            "birthDate": "1949-10-22",
                                            "address": "Hornsey Rd, London N7 7AJ",
                                            "phoneNumber": "0543217766"
                                        }
                                    ],
                                    "links": {
                                        "ref": "/api/v1/users/2"
                                    }
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void deleteUser_ReturnEmptyResponse() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/users/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void createUserUnder18_ReturnErrorDetails() throws Exception {
        User user = UserDataHelper.getArsenWenger();
        user.setBirthDate(LocalDate.of(2020,1,1));
        var userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(user);


        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPayload));
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "type": "about:blank",
                                    "title": "Bad Request",
                                    "status": 400,
                                    "detail": "Request contains errors",
                                    "instance": "/api/v1/users",
                                    "errors": [
                                        "The user does not meet the age restrictions"
                                    ]
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void findUsersByInvalidDateRange_ReturnsErrorDetails() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/users?startDate=1999-01-01&endDate=1966-07-01");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                          "type": "about:blank",
                          "title": "Bad Request",
                          "status": 400,
                          "detail": "Request contains errors",
                          "instance": "/api/v1/users",
                          "errors": [
                              "The specified date range is not valid. 'startDate' must be less than 'endDate'"
                          ]
                      }
                      """)
                );
    }

    @Test
    void createUserWithInvalidEmail_ReturnErrorDetails() throws Exception {
        User user = UserDataHelper.getArsenWenger();
        user.setEmail("invalidemailformat.com");
        var userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(user);


        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPayload));
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "type": "about:blank",
                                    "title": "Bad Request",
                                    "status": 400,
                                    "detail": "Request contains errors",
                                    "instance": "/api/v1/users",
                                    "errors": [
                                        "The email of a user has an invalid format"
                                    ]
                                }
                                """)
                );
    }

    @Test
    void createUserWithoutFirstAndLastName_ReturnErrorDetails() throws Exception {
        User user = UserDataHelper.getArsenWenger();
        user.setFirstName(null);
        user.setLastName(null);
        var userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(user);


        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPayload));
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "type": "about:blank",
                                    "title": "Bad Request",
                                    "status": 400,
                                    "detail": "Request contains errors",
                                    "instance": "/api/v1/users",
                                    "errors": [
                                        "The first name of a user can not be empty",
                                        "The last name of a user can not be empty"
                                    ]
                                }
                                """)
                );
    }

    @Test
    void createUserWithInvalidPhoneNumber_ReturnErrorDetails() throws Exception {
        User user = UserDataHelper.getArsenWenger();
        user.setPhoneNumber("54736764537848653");
        var userPayload = UserDataHelper.convertUserToCreateUpdateUserPayload(user);


        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPayload));
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "type": "about:blank",
                                    "title": "Bad Request",
                                    "status": 400,
                                    "detail": "Request contains errors",
                                    "instance": "/api/v1/users",
                                    "errors": [
                                        "The phone number is invalid. It should consist of 10 digits"
                                    ]
                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/test-users.sql")
    void deleteUserByInvalidId_ReturnErrorDetails() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/users/{id}", 322)
                .contentType(MediaType.APPLICATION_JSON);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "type": "about:blank",
                                    "title": "Not Found",
                                    "status": 404,
                                    "detail": "Error 404: Nothing has been found",
                                    "instance": "/api/v1/users/322",
                                    "errors": [
                                        "The user with specified id not found"
                                    ]
                                }
                                """)
                );
    }
}
