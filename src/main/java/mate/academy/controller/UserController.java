package mate.academy.controller;

import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.UserDto;
import mate.academy.model.RoleName;
import mate.academy.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update user role")
    @PutMapping("/{id}/role")
    public UserDto updateRole(@PathVariable("id") Long userId, @RequestParam RoleName role) {
        return userService.updateRole(userId, role);
    }

    @Operation(summary = "Get user profile info")
    @GetMapping("/me")
    public UserDto getProfileInfo() {
        return userService.getProfileInfo();
    }

    @Operation(summary = "Update user profile info")
    @PutMapping("/me")
    public UserDto updateProfileInfo(@RequestBody UserDto userDto) {
        return userService.updateProfileInfo(userDto);
    }
}
