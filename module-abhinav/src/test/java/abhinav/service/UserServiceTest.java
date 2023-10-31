package abhinav.service;

import abhinav.dto.LogInDTO;
import abhinav.jwt.JwtUtils;
import abhinav.modal.User;
import abhinav.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
    }

    @Test
    @DisplayName("Test signUp method when user is successfully registered")
    public void testSignUpWhenUserIsRegisteredThenReturnCreated() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<String> responseEntity = userService.signUp(user);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("user registered", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test signUp method when user with same email already exists")
    public void testSignUpWhenUserWithSameEmailExistsThenReturnBadRequest() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        ResponseEntity<String> responseEntity = userService.signUp(user);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("email already exists", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test signUp method when an exception occurs")
    public void testSignUpWhenExceptionOccursThenReturnInternalServerError() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        doThrow(new RuntimeException()).when(userRepository).save(any(User.class));

        ResponseEntity<String> responseEntity = userService.signUp(user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}