package com.qeat.service;

import com.qeat.domain.Role;
import com.qeat.domain.User;
import com.qeat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void bootstrapAdmin_isDisabledByDefault() {
        UserService userService = new UserService(userRepository, false);

        assertThatThrownBy(() -> userService.bootstrapAdmin(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("ADMIN 부트스트랩이 비활성화되어 있습니다.");
        verify(userRepository, never()).existsByRole(Role.ADMIN);
    }

    @Test
    void bootstrapAdmin_promotesFirstAdminWhenEnabled() {
        UserService userService = new UserService(userRepository, true);
        User user = User.create("21012345", "홍길동", "컴퓨터공학과", 3, Role.OPERATOR);
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User promoted = userService.bootstrapAdmin(1L);

        assertThat(promoted.getRole()).isEqualTo(Role.ADMIN);
    }
}
