package com.example.demo;

import com.example.demo.console.MyConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		var app = SpringApplication.run(DemoApplication.class, args);

		MyConsole console = app.getBean(MyConsole.class);
	}



}
