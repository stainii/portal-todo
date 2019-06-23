package be.stijnhooft.portal.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.Clock;

@SpringBootApplication
@EnableMongoRepositories
public class PortalTodoApplication {

	public static final String APPLICATION_NAME = "Todo";

	public static void main(String[] args) {
		SpringApplication.run(PortalTodoApplication.class, args);
	}

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}

}
