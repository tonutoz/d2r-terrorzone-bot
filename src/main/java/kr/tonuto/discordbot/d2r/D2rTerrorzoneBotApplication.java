package kr.tonuto.discordbot.d2r;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class D2rTerrorzoneBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(D2rTerrorzoneBotApplication.class, args);
    }

}
