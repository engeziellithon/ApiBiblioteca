package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.user.request.UserRequestDTO;
import com.erp.zup.api.dto.user.request.UserUpdateRequestDTO;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.IUserService;
import com.google.gson.Gson;
import io.sentry.Breadcrumb;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "Authenticate user and update token")
public class UserController {

    @Autowired
    private IUserService service;

    @Autowired
    private MapperUtil mapper;

    protected static final Logger logger = LogManager.getLogger(UserController.class);



    private static final String ID = "/{id}";



    @GetMapping(value = ID)
    public ResponseEntity<UserRequestDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(mapper.map(service.findById(id), UserRequestDTO.class));
    }


    @Operation(summary = "Get all user by filter", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserRequestDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication Failure", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<UserRequestDTO>> findAll(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<User> listUsers = service.findAll(pageable);

        if (listUsers == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(listUsers
                .stream().map(x -> mapper.map(x, UserRequestDTO.class)).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<UserRequestDTO> create(@RequestBody @Valid UserRequestDTO obj) {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(service.create(mapper.map(obj, User.class)).getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = ID)
    public ResponseEntity<UserRequestDTO> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequestDTO obj) {
        var userEntity = mapper.map(obj, User.class);
        userEntity.setId(id);
        return ResponseEntity.ok().body(mapper.map(service.update(userEntity), UserRequestDTO.class));
    }

    @DeleteMapping(value = ID)
    public ResponseEntity<UserRequestDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sentry")
    public void sentry(@RequestBody UserRequestDTO obj) throws Exception {
        logger.info("get data db");



    }

    static String extractPostRequestBody(UserRequestDTO obj) throws IOException {
        Gson gson = new Gson();
        var json = gson.toJson(obj);
        return json;
    }
}
