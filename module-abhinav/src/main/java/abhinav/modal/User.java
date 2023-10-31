package abhinav.modal;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String email;
    private String phone;
    private String password;

    private boolean status;
    private String role;

    @Temporal(TemporalType.TIMESTAMP)  // Use TIMESTAMP to store both date and time
    private Date blockTime;

    private Integer wrongAttempt;


    private LocalDateTime lastLogin;

}
