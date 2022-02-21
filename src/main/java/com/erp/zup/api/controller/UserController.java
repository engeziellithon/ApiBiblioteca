package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.PaginationDTO;
import com.erp.zup.api.dto.user.request.UserRequestDTO;
import com.erp.zup.api.dto.user.request.UserUpdateRequestDTO;
import com.erp.zup.api.dto.user.response.UserPaginationResponseDTO;
import com.erp.zup.api.dto.user.response.UserResponseDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jflunt.notifications.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "Authenticate user and update token")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private MapperUtil mapper;

    private PaginationDTO<UserRequestDTO> paginationDTO;

    private static final String ID = "/{id}";

    @Operation(summary = "Get user by id", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",content = @Content(schema = @Schema(implementation = UserPaginationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content)
    })
    @GetMapping(value = ID)
    public ResponseEntity findById(@Valid @PathVariable Long id) {
        Optional<User> user = service.findById(id);
        if(service.isInvalid())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(service.getNotifications());

        return ResponseEntity.ok().body(mapper.map(user.get(), UserResponseDTO.class));
    }


    @Operation(summary = "Get all user by filter", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200",content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserPaginationResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication Failure", content = @Content)
    })
    @GetMapping
    public ResponseEntity findAll(@Valid @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                  @Valid @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page > 0 ? page : 0,size > 0 ? size : 0,Sort.by("name"));

        Page<User> listUsers = service.findAll(pageRequest);

        if (listUsers.getSize() == 0)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(mapper.mapToGenericPagination(listUsers,UserPaginationResponseDTO.class));
    }


    @Operation(summary = "Save new user", responses = {
            @ApiResponse(description = "Successful create and header location", responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity create(@RequestBody @Valid UserRequestDTO obj) {
        Optional<User> user = service.create(mapper.map(obj, User.class));

        if (service.isInvalid())
            return ResponseEntity.badRequest().body(service.getNotifications());

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(user.get().getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update user", responses = {
            @ApiResponse(description = "Successful", responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = ID)
    public ResponseEntity update(@Valid @PathVariable Long id,@RequestBody @Valid UserUpdateRequestDTO obj) {

        User user = new User(id,obj.getName(),obj.getEmail(), obj.getPassword(),mapper.mapAll(obj.getRoles(),Role.class));

        Optional<User> userUpdated = service.update(user);
        if (service.isInvalid())
            return ResponseEntity.badRequest().body(service.getNotifications());

        return ResponseEntity.ok().body(mapper.map(userUpdated.get(), UserResponseDTO.class));
    }

    @Operation(summary = "Delete user", responses = {
            @ApiResponse(description = "Successful", responseCode = "204"),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = ID)
    public ResponseEntity delete(@Valid @PathVariable Long id) {
        service.delete(id);
        if(service.isInvalid())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(service.getNotifications());

        return ResponseEntity.noContent().build();
    }
}
