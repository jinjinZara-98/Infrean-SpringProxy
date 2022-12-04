package hello.proxy.config;

import hello.proxy.app.v1.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//수동등록
@Configuration
public class AppV1Config {

    //의존주입, 각 구현체에서 정의한 생성자로
    //의존하는 구현체를 파라미터로 주입받아 반환되는
    //컨트롤러는 서비스 의존, 서비스는 리포지토리 의존
    @Bean
    public OrderControllerV1 orderControllerV1() {
        return new OrderControllerV1Impl(orderServiceV1());
    }

    @Bean
    public OrderServiceV1 orderServiceV1() {
        return new OrderServiceV1Impl(orderRepositoryV1());
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1() {
        return new OrderRepositoryV1Impl();
    }

}
