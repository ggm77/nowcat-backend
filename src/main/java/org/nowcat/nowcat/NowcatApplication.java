package org.nowcat.nowcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // createdAt컬럼을 위해서 추가
public class NowcatApplication {
	// 2025.12.26 개발 시작
	public static void main(String[] args) {
		SpringApplication.run(NowcatApplication.class, args);
	}

}
