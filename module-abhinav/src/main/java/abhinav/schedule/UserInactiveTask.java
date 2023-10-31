package abhinav.schedule;
import abhinav.modal.User;
import abhinav.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserInactiveTask {

    @Autowired
    private UserRepository userRepository;

//    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
//    public void checkInactiveUsers() {
//
//        LocalDate sevenDaysAgo = LocalDate.now().minus(Period.ofDays(7));
//
//        List<User> userList = userRepository.findUsersNotLoggedInFor7Days(sevenDaysAgo);
//
//        for (User user : userList) {
//            user.setStatus(false);
//            userRepository.save(user);
//        }
//    }


    @Transactional
    @Scheduled(cron = "0 * * * * ?") // Run every minute
    public void checkInactiveUsers() {
        System.out.println("Hello abhinav this is @Scheduled method which is being called ---------------------------*************");
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        List<User> userList = userRepository.findUsersNotLoggedInSince(oneMinuteAgo);

        for (User user : userList) {
            user.setStatus(false);
            userRepository.save(user);
        }
    }

}
