package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/authentication")
public class LoginEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public LoginEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) throws ValidationException, ConflictException {
        LOGGER.trace("login({})", userLoginDto);
        return userService.login(userLoginDto);
    }

    @GetMapping
    public UserDetailDto getUser(@RequestHeader("Authorization") String authToken) {
        LOGGER.trace("getUser()");
        return userMapper.entityToUserDetailDto(
            userService.getUser(authToken)
        );
    }

    @PutMapping("/{id}")
    public UserDetailDto update(@PathVariable long id, @RequestBody UserDetailDto userDetailDto) throws ValidationException, ConflictException {
        LOGGER.trace("update({},{})", id, userDetailDto);
        return userService.update(userDetailDto);
    }

    @DeleteMapping("/{id}")
    public UserDetailDto delete(@PathVariable long id) {
        LOGGER.trace("delete({})", id);
        return userService.delete(id);
    }


    @PutMapping("/signOut/{userId}")
    public UserDetailDto signOut(@RequestBody String flatName, @PathVariable long userId) throws AuthorizationException {
        LOGGER.trace("signOut({},{})", flatName, userId);
        return userService.signOut(flatName, userId);
    }

    @GetMapping("/users/{userId}")
    public List<UserDetailDto> getUsers(@PathVariable long userId) {
        LOGGER.trace("getUsers({})", userId);
        return userService.getAllOtherUsers(userId);
    }

    @PutMapping("/admin")
    @Secured("ROLE_ADMIN")
    public UserDetailDto setAdmin(@RequestBody Long selectedUserId) {
        LOGGER.trace("setAdmin({})", selectedUserId);
        return userService.setAdminToTheFlat(selectedUserId);
    }

}
