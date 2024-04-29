package com.oleksiity.usersapi.controller;

import com.oleksiity.usersapi.controller.payload.CreateUpdateUserPayload;
import com.oleksiity.usersapi.controller.payload.UserSortField;
import com.oleksiity.usersapi.dto.ApiResponseDto;
import com.oleksiity.usersapi.dto.Pagination;
import com.oleksiity.usersapi.dto.UserDto;
import com.oleksiity.usersapi.entity.User;
import com.oleksiity.usersapi.mapper.UserMapper;
import com.oleksiity.usersapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final String CREATED_USER = "/api/v1/users/{id}";
    private final String BASE_URL = "/api/v1/users";

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping()
    public ResponseEntity<ApiResponseDto<?>> registerUser(@Valid @RequestBody CreateUpdateUserPayload userPayload) {
        var user = userService.registerUser(userPayload);
        var userURI = buildResourceFefURI(user);

        return ResponseEntity.created(userURI)
                .body(ApiResponseDto.<UserDto>builder()
                        .data(Collections.singleton(userMapper.toDto(user)))
                        .links(Map.of("ref", userURI))
                        .build());

    }

    @GetMapping()
    public ResponseEntity<ApiResponseDto<?>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                         @RequestParam(defaultValue = "ID") UserSortField sortField,
                                                         @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        var pageable = PageRequest.of(page, pageSize, sortDirection, sortField.getDatabaseFieldName());
        Page<User> userPage;
        if (startDate != null && endDate != null) {
            userPage = userService.getAllUsersByDateRange(pageable, startDate, endDate);
        } else {
            userPage = userService.getAllUsers(pageable);
        }

        return ResponseEntity.ok(
                ApiResponseDto.<UserDto>builder()
                        .data(userMapper.toDto(userPage.getContent()))
                        .pagination(Pagination.builder()
                                .page(userPage.getNumber())
                                .pageSize(userPage.getSize())
                                .totalPages(userPage.getTotalPages())
                                .totalElements(userPage.getTotalElements())
                                .build())
                        .links(buildPageLinks(userPage, sortField, sortDirection))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getUser(@PathVariable long id) {
        var user = userService.findUserById(id);

        return ResponseEntity.ok(ApiResponseDto.<UserDto>builder()
                .data(Collections.singleton(userMapper.toDto(user)))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateUser(@Valid @RequestBody UserDto userDto,
                                                        @PathVariable long id) {
        var user = userService.updateUser(userDto, id);

        return ResponseEntity.ok(ApiResponseDto.<UserDto>builder()
                .data(Collections.singleton(userMapper.toDto(user)))
                .links(Map.of("ref", buildResourceFefURI(user)))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> replaceUser(@Valid @RequestBody CreateUpdateUserPayload userPayload,
                                                         @PathVariable long id) {
        var user = userService.replaceUser(userPayload, id);

        return ResponseEntity.ok(ApiResponseDto.<UserDto>builder()
                .data(Collections.singleton(userMapper.toDto(user)))
                .links(Map.of("ref", buildResourceFefURI(user)))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

    private Map<String, URI> buildPageLinks(Page<?> page, UserSortField sortField, Sort.Direction sortDirection) {
        Map<String, URI> map = new HashMap<>();
        if (page.hasNext()) {
            var next = page.nextPageable();
            var nextPageURI = UriComponentsBuilder
                    .fromUriString(BASE_URL)
                    .queryParam("page", next.getPageNumber())
                    .queryParam("pageSize", next.getPageSize())
                    .queryParam("sortField", sortField)
                    .queryParam("sortDirection", sortDirection)
                    .build().toUri();
            map.put("next", nextPageURI);
        }
        if (page.hasPrevious()) {
            var prev = page.previousPageable();
            var prevPageURI = UriComponentsBuilder
                    .fromUriString(BASE_URL)
                    .queryParam("page", prev.getPageNumber())
                    .queryParam("pageSize", prev.getPageSize())
                    .queryParam("sortField", sortField)
                    .queryParam("sortDirection", sortDirection)
                    .build().toUri();
            map.put("prev", prevPageURI);
        }

        return map.isEmpty() ? null : map;
    }

    private URI buildResourceFefURI(User user) {
        return UriComponentsBuilder
                .fromUriString(CREATED_USER)
                .build(Map.of("id", user.getId()));
    }
}
