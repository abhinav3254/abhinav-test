package abhinav.service;

import abhinav.dto.LogInDTO;
import abhinav.jwt.JwtUtils;
import abhinav.modal.User;
import abhinav.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<String> signUp(User user) {
        try {
            User user1 = userRepository.findByEmail(user.getEmail());

            if (Objects.isNull(user1)) {

                user.setRole("user");
                user.setStatus(true);
                user.setWrongAttempt(0);
                user.setBlockTime(null);

                userRepository.save(user);
                return new ResponseEntity<>("user registered",HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>("email already exists",HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> login(LogInDTO logInDTO) {
        try {

            User user = userRepository.findByEmail(logInDTO.getEmail());

            if (Objects.isNull(user)) {
                return new ResponseEntity<>("user not found",HttpStatus.NOT_FOUND);
            } else {

                if (logInDTO.getPassword().equals(user.getPassword()) ) {
                    if (!user.isStatus()) {
                        return new ResponseEntity<>("you account have been blocked",HttpStatus.FORBIDDEN);
                    }
                    if (isThreeMinutesOver(user)) {
                        // generate token
                        user.setBlockTime(null);
                        user.setStatus(true);
                        user.setWrongAttempt(0);
                        // Get the current LocalDateTime
                        LocalDateTime currentLocalDateTime = LocalDateTime.now();

                        user.setLastLogin(currentLocalDateTime);
                        userRepository.save(user);
                        String token = jwtUtils.generateToken(logInDTO.getEmail(), user.getRole());
                        return new ResponseEntity<>(token,HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("wait for sometime",HttpStatus.BAD_REQUEST);
                    }
                } else {
                    user.setWrongAttempt(user.getWrongAttempt()+1);
                    if (user.getWrongAttempt()>3) {
                        user.setBlockTime(new Date());
                    }
                    userRepository.save(user);
                    return new ResponseEntity<>("wrong password,attempt number :- "+user.getWrongAttempt(),HttpStatus.NOT_ACCEPTABLE);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private boolean isThreeMinutesOver(User user) {
        if (user.getBlockTime()==null) {
            return true;
        } else {
            Date blockTime = user.getBlockTime();
            Date now = new Date();
            long timeDifference = now.getTime() - blockTime.getTime();
            // right now it will unlock the account in 30 seconds
            return timeDifference >= 0.5 * 60_000;
        }
    }

}
