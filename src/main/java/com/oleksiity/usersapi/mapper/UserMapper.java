package com.oleksiity.usersapi.mapper;

import com.oleksiity.usersapi.controller.payload.CreateUpdateUserPayload;
import com.oleksiity.usersapi.dto.UserDto;
import com.oleksiity.usersapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User fromDto(UserDto userDto);

    List<UserDto> toDto(List<User> employees);

    List<User> fromDto(List<UserDto> employees);

    User fromPayload(CreateUpdateUserPayload source);


}
