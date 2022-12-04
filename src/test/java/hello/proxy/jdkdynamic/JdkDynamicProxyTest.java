package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

/**
 * 실행 순서
 * 1. 클라이언트는 JDK 동적 프록시의 call() 을 실행한다.
 * 2. JDK 동적 프록시는 InvocationHandler.invoke() 를 호출한다. TimeInvocationHandler 가
 * 구현체로 있으로 TimeInvocationHandler.invoke() 가 호출된다.
 * 3. TimeInvocationHandler 가 내부 로직을 수행하고, method.invoke(target, args) 를 호출해서
 * target 인 실제 객체( AImpl )를 호출한다.
 * 4. AImpl 인스턴스의 call() 이 실행된다.
 * 5. AImpl 인스턴스의 call() 의 실행이 끝나면 TimeInvocationHandler 로 응답이 돌아온다. 시간
 * 로그를 출력하고 결과를 반환한다
 *
 * dynamicA() 와 dynamicB() 둘을 동시에 함께 실행하면 JDK 동적 프록시가 각각 다른 동적 프록시
 * 클래스를 만들어주는 것을 확인할 수 있다
 *
 * 예제를 보면 AImpl , BImpl 각각 프록시를 만들지 않았다. 프록시는 JDK 동적 프록시를 사용해서
 * 동적으로 만들고 TimeInvocationHandler 는 공통으로 사용했다.
 * JDK 동적 프록시 기술 덕분에 적용 대상 만큼 프록시 객체를 만들지 않아도 된다. 그리고 같은 부가 기능
 * 로직을 한번만 개발해서 공통으로 적용할 수 있다. 만약 적용 대상이 100개여도 동적 프록시를 통해서
 * 생성하고, 각각 필요한 InvocationHandler 만 만들어서 넣어주면 된다.
 * 결과적으로 프록시 클래스를 수 없이 만들어야 하는 문제도 해결하고, 부가 기능 로직도 하나의 클래스에
 * 모아서 단일 책임 원칙(SRP)도 지킬 수 있게 되었다
 *
 * JDK Proxy가 생성한 클래스 이름이다.
 * proxyClass=class com.sun.proxy.$Proxy1
 * */
@Slf4j
public class JdkDynamicProxyTest {

    @Test
    void dynamicA() {
        //구현체 주입, 변하는 로직 핵심 로직
        AInterface target = new AImpl();

        //동적 프록시에 적용할 핸들러, 뱐하지 않는 로직, 템플릿?
        //핵심 로직을 생성자 파라미터로?
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        //동적 프록시는 java.lang.reflect.Proxy 를 통해서 생성

        //클래스 로더 정보, 인터페이스, 그리고 핸들러 로직
        //그러면 해당 인터페이스를 기반으로 동적 프록시를 생성하고 그 결과를 반환

        //new Class[]{AInterface.class}는 인터페이스 여러 개일수 있으니
        //newProxy하면 동적으로 생성되는, AInterface.class.getClassLoader()는 어떤 클래스를 얻어야할지 지정
        //handler는 프록시가 사용할 로직이 뭔지
        AInterface proxy = (AInterface) Proxy.newProxyInstance( AInterface.class.getClassLoader(),
                                                        new Class[]{AInterface.class}, handler);

        proxy.call();
        //target이나 proxy나 껍데기인 AInterface는 같지만
        //클래스 정보를 출력해보면 target은 구현체인 AImpl이 나오고
        //proxy는 JDK 동적 프록시가 이름 그대로 동적으로 만들어준 프록시 나옴
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }

    @Test
    void dynamicB() {
        BInterface target = new BImpl();

        //TimeInvocationHandler 이거 한 개로 프록시 기능 처리
        //개수가 늘어난다고 프록시 클래스도 그만큼 더 만드는게 아니다
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        BInterface proxy = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(),
                                                        new Class[]{BInterface.class}, handler);

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }
}
