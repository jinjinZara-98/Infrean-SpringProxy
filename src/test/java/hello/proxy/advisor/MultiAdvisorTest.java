package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

/**스프링이 제공하는 프록시 팩토리 사용
 *
 * 만약 여러 어드바이저를 하나의 target 에 적용하려면 어떻게 해야할까?
//쉽게 이야기해서 하나의 target 에 여러 어드바이스를 적용하려면 어떻게 해야할까?
//지금 떠오르는 방법은 프록시를 여러게 만들면 될 것 같다.
//여러 프록시*/
public class MultiAdvisorTest {

    //여러 프록시의 문제
    //이 방법이 잘못된 것은 아니지만, 프록시를 2번 생성해야 한다는 문제가 있다. 만약 적용해야 하는
    //어드바이저가 10개라면 10개의 프록시를 생성해
    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1() {
        //client -> proxy2(advisor2) -> proxy1(advisor1) -> target

        //실게 객체 생성
        ServiceInterface target = new ServiceImpl();

        //프록시 팩토리 생성, 실제 객체 주입
        ProxyFactory proxyFactory1 = new ProxyFactory(target);

        //어드바이저
        //하나의 어드바이저에 하나의 포인트컷 하나의 어드바이스
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());

        //프록시 팩토리에에 어드바이저 추가
        proxyFactory1.addAdvisor(advisor1);

        //프록시 객체 가져옴, 실제 객체의 타입으로 캐스팅
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();


        /**프록시 팩토리 2 생성,
        //target이 아닌 proxy1 주입, 타겟을 넣지 않고 프록시가 프록시를 걸기 떄문에*/
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);

        //어드바이저
        //하나의 어드바이저에 하나의 포인트컷 하나의 어드바이스
        //다른 어드바이스 적용
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());

        //프록시 팩토리에 어드바이저 추가
        proxyFactory2.addAdvisor(advisor2);

        //프록시 객체 가져옴, 실제 객체의 타입으로 캐스팅
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        //프록시 객체로 메서드 호출, 원래 ServiceImpl()의 save()를 호출하는
        proxy2.save();

    }

    /** 여러 프록시의 문제
     이 방법이 잘못된 것은 아니지만, 프록시를 2번 생성해야 한다는 문제가 있다. 만약 적용해야 하는
     어드바이저가 10개라면 10개의 프록시를 생성 */

    /**
     * 이전에는 어드바이저 하나당 프록시를 하나씩 생성했음
     * 하나의 프록시, 여러 어드바이저
     * 스프링은 이 문제를 해결하기 위해 하나의 프록시에 여러 어드바이저를 적용할 수 있게 만들어두었다.
     * */
    @Test
    @DisplayName("하나의 프록시, 여러 어드바이저")
    void multiAdvisorTest2() {
        /**client -> proxy -> advisor2 -> advisor1 -> target
        //하나의 어드바이저에 하나의 포인트컷 하나의 어드바이스
        //어드바이스는 현재 만든 것을 이용*/
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());

        //실제 객체 생성
        ServiceInterface target = new ServiceImpl();

        //프록시 팩토리 생성
        ProxyFactory proxyFactory1 = new ProxyFactory(target);

        /**
         * 하나의 프록시 팩토리에 어드바이저 2개 모두 추가,
         * 2번 1번 순으로 호출되게 만들꺼기 때문에 순서대로 추가
         * 프록시 팩토리에 원하는 만큼 addAdvisor() 를 통해서 어드바이저를 등록하면 된다.
         * 등록하는 순서대로 advisor 가 호출된다. 여기서는 advisor2 , advisor1 순서로 등록했다
         * */
        proxyFactory1.addAdvisor(advisor2);
        proxyFactory1.addAdvisor(advisor1);

        //프록시 객체 가져옴, 실제 객체의 타입므로 캐스팅
        ServiceInterface proxy = (ServiceInterface) proxyFactory1.getProxy();

        //실행
        proxy.save();

        /** 결과적으로 여러 프록시를 사용할 때와 비교해서 결과는 같고, 성능은 더 좋다  */
    }

    /**어드바이스 만듬, MethodInterceptor 구현, MethodInterceptor는 Advice의 자식*/
    @Slf4j
    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1 호출");

            /**
             * 실제 객체의 결과를 반환
             *
             * 즉 위에 로그를 출력하고 프록시 팩토리로 들어온 실제 객체의 메서드 호출하는
             * */
            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2 호출");

            return invocation.proceed();
        }
    }

}
