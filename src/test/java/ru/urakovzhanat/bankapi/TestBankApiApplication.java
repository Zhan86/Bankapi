package ru.urakovzhanat.bankapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestBankApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(BankApiApplication::main).with(TestBankApiApplication.class).run(args);
	}

}
