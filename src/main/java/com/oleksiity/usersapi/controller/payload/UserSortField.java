package com.oleksiity.usersapi.controller.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserSortField {

    ID("id"),
    EMAIL("email"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    BIRTH_DATE("birthDate"),
    PHONE("phoneNumber"),
    ADDRESS("address");

    private final String databaseFieldName;
}
