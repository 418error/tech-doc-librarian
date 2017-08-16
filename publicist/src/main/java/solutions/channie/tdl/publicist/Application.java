package solutions.channie.tdl.publicist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import solutions.channie.tdl.publicist.services.GatherService;

@SpringBootApplication
public class Application {

    @Autowired
    public GatherService gatherService;

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner kickOff(){
        return args -> {
            gatherService.getRepos();
        };
    }

}
