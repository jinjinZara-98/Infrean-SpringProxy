package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션 실행 시점에 프록시를 사용하도록 의존 관계를 설정해
 * 기존에는 스프링 빈이 orderControlerV1Impl , orderServiceV1Impl 같은 실제 객체를 반환했다
 *
 * 하지만 이제는 프록시를 사용해야한다. 따라서 프록시를 생성하고 프록시를 실제 스프링 빈 대신 등록한다.
 * 실제 객체는 스프링 빈으로 등록하지 않는다.
 * 프록시는 내부에 실제 객체를 참조하는
 * 스프링 빈으로 실제 객체 대신에 프록시 객체를 등록했기 때문에
 * 앞으로 스프링 빈을 주입 받으면 실제 객체 대신에 프록시 객체가 주입
 * 쉽게 이야기해서 프록시 객체 안에 실제 객체가 있는 것
 *
 * 스프링 컨테이너에 프록시 객체가 등록된다.
 * 스프링 컨테이너는 이제 실제 객체가 아니라 프록시 객체를 스프링 빈으로 관리한다
 * 실제 객체는 스프링 컨테이너와는 상관이 없다. 실제 객체는 프록시 객체를 통해서 참조될 뿐이다.
 * 프록시 객체가 실제 객체 호출하니
 * 프록시 객체는 스프링 컨테이너가 관리하고 자바 힙 메모리에도 올라간다.
 * 반면에 실제 객체는 자바 힙 메모리에는 올라가지만 스프링 컨테이너가 관리하지는 않는다.
 * 왜냐 스프링 컨테이너에 등록된 빈은 프록시니까
 *
 * 클라이언트가 컨트롤러 프록시 호출 컨트롤러 프록시가 진짜 컨트롤러 호출
 * 진짜 컨트롤러가 서비스프록시 호출 서비스프록시가 진짜 서비스 호출
 * */
@Configuration
public class InterfaceProxyConfig {

    //컨트롤러가 프록시 생성하며 스프링 빈에 프록시가 등록, 컨트롤러 구현체가 서비스를 참조
    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace) {

        //프록시가 중간에 껴있으니 orderService를 주입받은 OrderControllerV1Impl를
        //바로 반환하는게 아닌 프록시 파라미터에 주입해서 프록시를 반환
        OrderControllerV1Impl controllerImpl = new OrderControllerV1Impl(orderService(logTrace));
        //구현체가 아닌 프록시 반환, 실제 객체는 프록시 파라미터에 의존주입
        return new OrderControllerInterfaceProxy(controllerImpl, logTrace);
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        //OrderServiceV1Impl를 그냥 반환하는게 아닌 프록시 파라미터에 반환
        OrderServiceV1Impl serviceImpl = new OrderServiceV1Impl(orderRepository(logTrace));
        return new OrderServiceInterfaceProxy(serviceImpl, logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1Impl repositoryImpl = new OrderRepositoryV1Impl();
        return new OrderRepositoryInterfaceProxy(repositoryImpl, logTrace);
    }

}
