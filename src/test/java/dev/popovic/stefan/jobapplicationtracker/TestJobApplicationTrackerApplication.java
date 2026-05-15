package dev.popovic.stefan.jobapplicationtracker;

import org.springframework.boot.SpringApplication;

public class TestJobApplicationTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.from(JobApplicationTrackerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
