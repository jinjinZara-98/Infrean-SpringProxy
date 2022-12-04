package hello.proxy.config.v3_proxyfactory;

import hello.proxy.app.v1.*;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 프록시 팩토리 사용,
 *
 *JDK 동적 프록시 적용
 * */
@Slf4j
@Configuration
public class ProxyFactoryConfigV1 {

    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        //서비스를 파라미터로 받는 구현 객체 생성, 실제 객체
        OrderControllerV1 orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));

        //프록시 팩토리 생성, 생성자 파라미터에 실제 객체 주입
        /** 인터페이스 사용하므로 JDK 동적 프록시 적용*/
        ProxyFactory factory = new ProxyFactory(orderController);

        //프록시 팩토리에 어드바이저 추가
        //하나의 어드바이스 하나의 포인트컷 하나의 어드바이저
        //getAdvisor() 에서 포인트컷 어드바이스 만들어 어드아비저 객체에 넣어 어드바이저 반환
        factory.addAdvisor(getAdvisor(logTrace));

        //프록시 객체 가져옴, 실제 객체 타입으로 캐스팅
        OrderControllerV1 proxy = (OrderControllerV1) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderController.getClass());

        //프록시 객체 반환
        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));

        ProxyFactory factory = new ProxyFactory(orderService);
        factory.addAdvisor(getAdvisor(logTrace));
        OrderServiceV1 proxy = (OrderServiceV1) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderService.getClass());

        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        //타겟, OrderRepositoryV1구현 객체
        OrderRepositoryV1Impl orderRepository = new OrderRepositoryV1Impl();
        //프록시 생성
        ProxyFactory factory = new ProxyFactory(orderRepository);
        //프록시에 어드바이저 추가
        factory.addAdvisor(getAdvisor(logTrace));
        OrderRepositoryV1 proxy = (OrderRepositoryV1) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderRepository.getClass());

        return proxy;
    }

    private Advisor getAdvisor(LogTrace logTrace) {
        /**pointcut 생성
         *
         */
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();

        //request* , order* , save* : request 로 시작하는 메서드에 포인트컷은 true 를 반환한다.
        //이렇게 설정한 이유는 noLog() 메서드에는 어드바이스를 적용하지 않기 위해서
        pointcut.setMappedNames("request*", "order*", "save*");

        /**advice 셍성
         *
         * 실제 어드바이스 생성하고 거기에 logTrace 주입
         *
         */
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        //포인트컷과 어드바이스 넣은 어드바이저 반환
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
