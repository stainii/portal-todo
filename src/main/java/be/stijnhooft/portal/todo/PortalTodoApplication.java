package be.stijnhooft.portal.todo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableMongoRepositories
public class PortalTodoApplication {

	@Value("${timeZone}")
	private String timeZone;

	public static final String APPLICATION_NAME = "Todo";

	public static void main(String[] args) {
		SpringApplication.run(PortalTodoApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}
}
