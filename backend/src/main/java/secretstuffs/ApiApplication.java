package secretstuffs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Spring Boot application.
 * This class initializes and runs the application context.
 */
@SpringBootApplication
public class ApiApplication
{
	private static final Logger logger = LoggerFactory.getLogger(ApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
		logger.info("ApiApplication has started successfully.");
	}
}