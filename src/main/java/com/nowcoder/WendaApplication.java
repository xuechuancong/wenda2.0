package com.nowcoder;

import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nowcoder.dao")
public class WendaApplication {

	UserDAO userDAO;

	public static void main(String[] args) {
		SpringApplication.run(WendaApplication.class, args);
	}
}
