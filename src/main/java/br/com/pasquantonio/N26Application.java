package br.com.pasquantonio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan()
public class N26Application {

	public static void main(String[] args) {
		SpringApplication.run(N26Application.class, args);
	}
}
