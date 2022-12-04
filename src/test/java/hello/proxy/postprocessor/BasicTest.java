package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 빈 후처리기 사용 전 기본 빈 등록*/
public class BasicTest {

    @Test
    void basicConfig() {
        //스프링 컨테이너 생성
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BasicConfig.class);

        //이름 이 beanA인 빈 객체 갖고옴
        A a = applicationContext.getBean("beanA", A.class);
        a.helloA();

        //B는 빈으로 등록되지 않는다.
        //() -> applicationContext.getBean(B.class)이 코드를 실행했을 때 (NoSuchBeanDefinitionException.class이 예외가 터지면 성공
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(B.class));
    }

    //설정파일
    @Slf4j
    @Configuration
    static class BasicConfig {
        //A클래스 객체 반환
        @Bean(name = "beanA")
        public A a() {
            return new A();
        }
    }

    @Slf4j
    static class A {
        public void helloA() {
            log.info("hello A");
        }
    }

    @Slf4j
    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }
}
