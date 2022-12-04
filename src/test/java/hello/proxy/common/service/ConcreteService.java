package hello.proxy.common.service;

import lombok.extern.slf4j.Slf4j;

/**
 * CGLIB: Code Generator Library
 * CGLIB는 바이트코드를 조작해서 동적으로 클래스를 생성하는 기술을 제공하는 라이브러리이다.
 * CGLIB를 사용하면 인터페이스가 없어도 구체 클래스만 가지고 동적 프록시를 만들어낼 수 있다.
 * CGLIB는 원래는 외부 라이브러리인데, 스프링 프레임워크가 스프링 내부 소스 코드에 포함했다. 따라서
 * 스프링을 사용한다면 별도의 외부 라이브러리를 추가하지 않아도 사용할 수 있다.
 * 참고로 우리가 CGLIB를 직접 사용하는 경우는 거의 없다.
 * 이후에 설명할 스프링의 ProxyFactory 라는 것이 이 기술을 편리하게 사용하게 도와주기 때문에,
 * 너무 깊이있게 파기 보다는 CGLIB가 무엇인지 대략 개념만 잡으면 된다.
 * 예제 코드로 CGLIB를 간단히 이해해보자.
 * 공통 예제 코드
 * 앞으로 다양한 상황을 설명하기 위해서 먼저 공통으로 사용할 예제 코드를 만들어보자.
 * 인터페이스와 구현이 있는 서비스 클래스 - ServiceInterface , ServiceImpl
 * 구체 클래스만 있는 서비스 클래스 - ConcreteService
 *
 * 인터페이스 있을 떄랑 인터페이스 없이 구체 클래스만 있을 때
 */
@Slf4j
public class ConcreteService {
    public void call() {
        log.info("ConcreteService 호출");
    }
}
