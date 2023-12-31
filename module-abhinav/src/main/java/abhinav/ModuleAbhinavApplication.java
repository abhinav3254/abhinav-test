package abhinav;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("abhinav")
public class ModuleAbhinavApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleAbhinavApplication.class, args);
	}

}
