package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	/**
	 * jackson 라이브러리는 기본적으로 이 프록시 객체를 json으로 어떻게 생성해야 하는지 모름. 예외발생
	 * Hibernate5Module 을 스프링 빈으로 등록하면 해결(스프링 부트 사용중)
	 * => 권장하지 않는 방식임. 엔티티를 API 응답으로 외부로 노출하는 것은 좋지 않음. DTO로 변환해서 반환할 것
	 */
	@Bean
	Hibernate5Module hibernate5Module() { // 권장하지 않음
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		// 강제 지연 로딩 설정
		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING,true);
		return hibernate5Module;
	}

}
