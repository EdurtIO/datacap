package io.edurt.datacap.server.controller.user;

import io.edurt.datacap.server.body.AuthBody;
import io.edurt.datacap.server.common.JwtResponse;
import io.edurt.datacap.server.common.Response;
import io.edurt.datacap.server.entity.UserEntity;
import io.edurt.datacap.server.service.UserService;
import io.edurt.datacap.server.validation.ValidationGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final UserService userService;

    public AuthController(UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public Response<JwtResponse> authenticateUser(@RequestBody @Validated(ValidationGroup.Crud.Auth.class) AuthBody configure)
    {
        UserEntity user = new UserEntity();
        user.setUsername(configure.getUsername());
        user.setPassword(configure.getPassword());
        return this.userService.authenticate(user);
    }

    @PostMapping("/signup")
    public Response<?> registerUser(@RequestBody @Validated(ValidationGroup.Crud.Create.class) AuthBody configure)
    {
        UserEntity user = new UserEntity();
        user.setUsername(configure.getUsername());
        user.setPassword(configure.getPassword());
        return this.userService.saveOrUpdate(user);
    }
}
