package com.oleksiity.usersapi;

import com.oleksiity.usersapi.controller.payload.CreateUpdateUserPayload;
import com.oleksiity.usersapi.dto.UserDto;
import com.oleksiity.usersapi.entity.User;

import java.time.LocalDate;

public class UserDataHelper {

    public static User getSergioRamos() {
        var sergio = new User();
        sergio.setId(1L);
        sergio.setFirstName("Sergio");
        sergio.setLastName("Ramos");
        sergio.setEmail("sergio@madrid.com");
        sergio.setAddress("Av. de Concha Espina, 1, Chamartín, 28036 Madrid");
        sergio.setPhoneNumber("0445523499");
        sergio.setBirthDate(LocalDate.of(1986, 3, 30));

        return sergio;
    }

    public static User getArsenWenger() {
        var arsen = new User();
        arsen.setId(2L);
        arsen.setFirstName("Arsen");
        arsen.setLastName("Wenger");
        arsen.setEmail("awenger@gunners.com");
        arsen.setAddress("Hornsey Rd, London N7 7AJ");
        arsen.setBirthDate(LocalDate.of(1949, 10, 22));

        return arsen;
    }

    public static User getEricCantona() {
        var eric = new User();
        eric.setId(7L);
        eric.setFirstName("Eric");
        eric.setLastName("Cantona");
        eric.setEmail("cantona@machester.com");
        eric.setAddress("Sir Matt Busby Way, Old Trafford, Stretford, Manchester M16 0RA");
        eric.setPhoneNumber("0994562398");
        eric.setBirthDate(LocalDate.of(1966, 5, 24));

        return eric;
    }

    public static UserDto convertUserToUserDto(User user) {
        return new UserDto(user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getBirthDate(),
                user.getAddress(), user.getPhoneNumber());
    }

    public static CreateUpdateUserPayload convertUserToCreateUpdateUserPayload(User user) {
        return new CreateUpdateUserPayload(user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getBirthDate(),
                user.getAddress(), user.getPhoneNumber());
    }
}
