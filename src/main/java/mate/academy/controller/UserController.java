package mate.academy.controller;

import io.swagger.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.model.RoleName;
import mate.academy.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update user role")
    @PutMapping("/{id}/role")
    public UserResponseDto updateRole(@PathVariable("id") Long userId,
                                      @RequestParam RoleName role) {
        return userService.updateRole(userId, role);
    }

    @Operation(summary = "Get user profile information")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/me")
    public UserResponseDto getProfileInfo() {
        return userService.getProfileInfo();
    }

    @Operation(summary = "Update user profile information")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/me")
    public UserResponseDto updateProfileInfo(@RequestBody UserRequestDto userRequestDto) {
        return userService.updateProfileInfo(userRequestDto);
    }
}
