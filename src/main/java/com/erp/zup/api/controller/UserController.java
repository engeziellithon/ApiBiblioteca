package com.erp.zup.api.controller;

import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.api.dto.user.request.UserRequestDTO;
import com.erp.zup.api.dto.user.request.UserUpdateRequestDTO;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.IUserService;
import com.erp.zup.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
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
    public ResponseEntity create(@RequestBody @Valid UserRequestDTO obj) {
        Optional<User> user = service.create(mapper.map(obj, User.class));

        if (user.isEmpty())
            return ResponseEntity.badRequest().body(service.getNotifications());

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(service.create(mapper.map(obj, User.class)).get().getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = ID)
    public ResponseEntity<UserRequestDTO> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequestDTO obj) {
        User userEntity = new User(id,obj.getName(),obj.getEmail(),obj.getPassword(),mapper.mapAll(obj.getRoles(),Role.class));

        return ResponseEntity.ok().body(mapper.map(service.update(userEntity), UserRequestDTO.class));
    }

    @DeleteMapping(value = ID)
    public ResponseEntity<UserRequestDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/sentry")
//    public void sentry(@RequestBody UserRequestDTO obj) throws Exception {
//        logger.info("get data db");
//
//        try {
//           var exception = 1/0;
//        } catch (Exception e) {
//            logger.error(e.getMessage(),e);
//        }
//
//    }
//
//    static String extractPostRequestBody(UserRequestDTO obj) throws IOException {
//        Gson gson = new Gson();
//        var json = gson.toJson(obj);
//        return json;
//    }
}
