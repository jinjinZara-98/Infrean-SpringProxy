package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

/**ConcreteService 는 인터페이스가 없는 구체 클래스이다. 여기에 CGLIB를 사용해서 프록시를 생성해보자.
 *
//현재 인터페이스 없음

//CGLIB 제약
//클래스 기반 프록시는 상속을 사용하기 때문에 몇가지 제약이 있다.
//부모 클래스의 생성자를 체크해야 한다. CGLIB는 자식 클래스를 동적으로 생성하기 때문에 기본 생성자가 필요하다.
//클래스에 final 키워드가 붙으면 상속이 불가능하다. CGLIB에서는 예외가 발생한다.
//메서드에 final 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없다. CGLIB에서는 프록시 로직이 동작하지 않는다.

//CGLIB가 동적으로 생성하는 클래스 이름은 다음과 같은 규칙으로 생성된다.
//대상클래스$$EnhancerByCGLIB$$임의코드

//클라이언트가 call() 하면 프록시의 TimeMethodInterceptor intercept() 호출
  그 안에서 실제 객체 호출*/
@Slf4j
public class CglibTest {

    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        //Enhancer라는 코드로 시작, 부여?
        //Enhancer : CGLIB는 Enhancer 를 사용해서 프록시를 생성한다.
        Enhancer enhancer = new Enhancer();

        //동적프록시 생성, 구체 클래스 기반으로 ConcreteService를 상속받은 클래스 만들어야함
        //enhancer.setSuperclass(ConcreteService.class) : CGLIB는 구체 클래스를 상속 받아서 프록시를 생성할 수 있다.
        //어떤 구체 클래스를 상속 받을지 지정한다.
        enhancer.setSuperclass(ConcreteService.class);

        //target은 실제 객체
        //프록시객체를 생성하며 파라미터에 적용할 실행 로직을 할당한다.
        enhancer.setCallback(new TimeMethodInterceptor(target));

        //enhancer.create()로 프록시 생성
        //앞서 설정한 enhancer.setSuperclass(ConcreteService.class) 에서 지정한 클래스를 상속 받아서 프록시가 만들어진다.
        //JDK 동적 프록시는 인터페이스를 구현(implement)해서 프록시를 만든다.
        //CGLIB는 구체 클래스를 상속 (extends)해서 프록시를 만든다
        ConcreteService proxy = (ConcreteService) enhancer.create();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

    }
}
