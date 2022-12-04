package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.*;

/**
 * new ProxyFactory(target) :
 *프록시 팩토리를 생성할 때, 생성자에 프록시의 호출 대상을 함께 넘겨준다.\
 *
 * 프록시 팩토리는 이 인스턴스 정보를 기반으로 프록시를 만들어낸다.
 * 만약 이 인스턴스에 인터페이스가 있다면 JDK 동적 프록시를 기본으로 사용하고
 * 인터페이스가 없고 구체 클래스만 있다면 CGLIB를 통해서 동적 프록시를 생성한다.
 * 여기서는 target 이 new ServiceImpl() 의 인스턴스이기 때문에 ServiceInterface 인터페이스가 있다.
 * 따라서 이 인터페이스를 기반으로 JDK 동적 프록시를 생성한다.
 * proxyFactory.addAdvice(new TimeAdvice()) :
 * 프록시 팩토리를 통해서 만든 프록시가 사용할 부가 기능 로직을 설정한다.
 * JDK 동적 프록시가 제공하는 InvocationHandler 와 CGLIB가 제공하는 MethodInterceptor 의 개념과 유사하다.
 * 이렇게 프록시가 제공하는 부가 기능 로직을 어드바이스( Advice )라 한다. 번역하면 조언을 해준다고 생각하면 된다.
 * proxyFactory.getProxy() : 프록시 객체를 생성하고 그 결과를 받는다
 * */
@Slf4j
public class ProxyFactoryTest {

    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        //실제 객체 생성
        ServiceInterface target = new ServiceImpl();

        //프록시 팩토리 객체 생성 후 타겟인 구현체를 주입
        //여기서 주입하므로 Advide 파라미터에 타겟 주입할 필요 없음

        //여기서 인터페이스가 아닌 구현 클래스였다면 CGLIB의 동적 프록시 생성
        ProxyFactory proxyFactory = new ProxyFactory(target);

        //프록시 팩토리에 부가 기능 로직인 Advice를 주입하고
        proxyFactory.addAdvice(new TimeAdvice());

        //프록시 팩토리로 프록시 객체 가져와
        //원래 실제 객체의 타입으로 캐스팅
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        //실제 객체와 프록시 객체가 출력됨
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        //프록시가 실제 객체 호출, 실제 객체의 메서드 사용
        //원래는 target.save() 이렇게 직접 호출해야 하지만
        //advice안의 invoke 메서드안에 invocation.proceed()로 실제 객체 호출
        proxy.save();

        //AopUtils.isAopProxy는 ProxyFactory로 만들었을 때만
        //JDK 동적 프록시 CGLIB 동적 프록시 인지 확인하는 로직
        //현재는 인터페이스가 있고 JDK로 동적 프록시 생성했으니
        //CGLIB 는 false임

        //proxy.getClass() 처럼 인스턴스의 클래스 정보를 직접 출력해서 확인할 수 있다.
        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    void concreteProxy() {
        //인터페이스 없고 구체 클래스로
        ConcreteService target = new ConcreteService();

        //구체 클래스를 파라미터로 주입받음으로서 CGLIB의 동적 프록시 생성
        ProxyFactory proxyFactory = new ProxyFactory(target);

        //프록시 팩토리에 부가 기능 로직인 Advice를 주입하고
        proxyFactory.addAdvice(new TimeAdvice());

        //프록시 팩토리에서 프록시 객체 갖고오고 실제 객체 타입으로 캐스팅
        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        //프록시가 실제 객체 호출, 실제 객체의 메서드 사용
        //원래는 target.call() 이렇게 직접 호출해야 하지만
        //advice안의 invoke 메서드안에 invocation.proceed()로 실제 객체 호출
        proxy.call();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        //현재 구체 클래스 사용해 CGLIB의 동적 프록시 생성했으니 JDK는 false
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

    /**
     * 인터페이스가 있지만, CGLIB를 사용해서
     * 인터페이스가 아닌 클래스 기반으로 동적 프록시를 만드는 방법
     * */
    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        //인터페이스 사용
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        //하지만 여기서 setProxyTargetClass(true)로 클래스 기반으로 동적 프록시를 만들어버림
        //CGLIB 동적 프록시 생성, 강제로 CGLIB를 사용
        proxyFactory.setProxyTargetClass(true);

        //프록시 팩토리에 부가 기능 로직인 Advice를 주입하고
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        //setProxyTargetClass(true)로 클래스 기반으로 CGLIB 동적 프록시 생성
        //인터페이스를 사용해도 JDK false
        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

}
