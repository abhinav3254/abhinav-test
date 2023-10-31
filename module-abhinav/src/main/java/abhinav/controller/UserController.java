package abhinav.controller;


import abhinav.dto.LogInDTO;
import abhinav.modal.User;
import abhinav.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) User user) {
        return userService.signUp(user);
    }

    @PutMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) LogInDTO logInDTO) {
        return userService.login(logInDTO);
    }

}
