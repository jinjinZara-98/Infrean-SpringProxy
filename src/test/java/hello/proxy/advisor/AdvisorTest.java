package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.awt.*;
import java.lang.reflect.Method;

/**프록시 팩토리 사용, 어드바이스 사용
 *
 * */
@Slf4j
public class AdvisorTest {

    @Test
    void advisorTest1() {
        //실제 객체 생성
        ServiceInterface target = new ServiceImpl();

        //프록시 팩토리 객체 생성, 실제 객체 주입
        //인터페이스 사용했으니까 JDK 동적 프록시 생성
        ProxyFactory proxyFactory = new ProxyFactory(target);

        //어드바이저 생성
        //항상 참인 Pointcut, 어떤 메서드를 호출해도 부가 기능인 어드바이스 호출,
        //만들었던 TimeAdvice 사용
        //하나의 어드바이저에 하나의 포인트컷 하나의 어드바이스
        //프록시 팩토리에 적용할 어드바이저를 지정한다.
        //어드바이저는 내부에 포인트컷과 어드바이스를 모두 가지고 있다.
        //따라서 어디에 어떤 부가 기능을 적용해야 할지 어드바이스 하나로 알 수 있다.
        //프록시 팩토리를 사용할 때 어드바이저는 필수
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());

        //프록시 팩토리에 어드바이스가 아닌 어드바이저 추가
        proxyFactory.addAdvisor(advisor);

        //프록시 객체 가져옴, 실제 객체 타입의 클래스로 캐스팅
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    /** save() 메서드에는 어드바이스 로직을 적용하지만,
     * find() 메서드에는 어드바이스 로직을 적용하지 않도록*/
    @Test
    @DisplayName("직접 만든 포인트컷")
    void advisorTest2() {
        //실제 객체 생성
        ServiceInterface target = new ServiceImpl();

        //프록시 팩토리 객체 생성, 실제 객체 주입
        //인터페이스 사용하니 JDK 동적 프록시 생성
        ProxyFactory proxyFactory = new ProxyFactory(target);

        //어드바이저 생성
        //어드바이스 적용할 지점인 새로 만든 필터인 포인트컷 파라미터로
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());

        //프록시 팩토리에 어드바이저 추가
        proxyFactory.addAdvisor(advisor);

        //프록시 객체 꺼내면서 실제 객체 타입으로 캐스팅
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        //프록시 객체로 실제 객체가 호출해야할 메서드 호출
        //save() 만 어드바이스 적용
        proxy.save();
        proxy.find();
    }

    /**포인트컷 만들기, 포인트컷 구현
     * 포인트컷은 크게 ClassFilter 와 MethodMatcher 둘로 이루어진다.
     * 이름 그대로 하나는 클래스가 맞는지, 하나는 메서드가 맞는지 확인할 때 사용한다.
     * 둘다 true 로 반환해야 어드바이스를 적용할 수 있다*/
    static class MyPointcut implements Pointcut {

        //클래스는 항상 참으로
        @Override
        public ClassFilter getClassFilter() {
            //true 반환하니 어드바이스 적용
            return ClassFilter.TRUE;
        }

        //메서드는 따로 정의한 포인트컷으로
        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    //사용할 메서드 필터 만들기, 메서드 필터를 구현
    static class MyMethodMatcher implements MethodMatcher {

        private String matchName = "save";

        /**
         * matches() : 이 메서드에 method , targetClass 정보가 넘어온다.
         * 이 정보로 어드바이스를 적용할지 적용하지 않을지 판단할 수 있다.
         * 여기서는 메서드 이름이 "save" 인 경우에 true 를 반환하도록 판단 로직을 적용했다.
         * */
        @Override
        public boolean matches(Method method, Class<?> targetClass) {

            //메서드 이름을 갖고와 비교 이전에 저장해둔 save와
            boolean result = method.getName().equals(matchName);

            log.info("포인트컷 호출 method={} targetClass={}", method.getName(), targetClass);
            log.info("포인트컷 결과 result={}", result);

            return result;
        }

        /**
         * isRuntime() , matches(... args) : isRuntime() 이 값이 참이면 matches(... args) 메서드가 대신 호출된다.
         *
         * 동적으로 넘어오는 매개변수를 판단 로직으로 사용할 수 있다.
         * isRuntime() 이 false 인 경우 클래스의 정적 정보만 사용하기 때문에 스프링이 내부에서 캐싱을 통해 성능 향상이 가능하지만,
         * isRuntime() 이 true 인 경우 매개변수가 동적으로 변경된다고 가정하기 때문에 캐싱을 하지 않는다.
         *크게 중요한 부분은 아니니 참고만
         * */
        @Override
        public boolean isRuntime() {
            return false;
        }

        //참이면 이게 불러지는
        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }

    /**
     * 스프링이 제공하는 포인트컷
     * 스프링은 무수히 많은 포인트컷을 제공한다.
     * 대표적인 몇가지만 알아보자.
     * NameMatchMethodPointcut : 메서드 이름을 기반으로 매칭한다. 내부에서는 PatternMatchUtils 를 사용한다.
     * 예) *xxx* 허용
     * JdkRegexpMethodPointcut : JDK 정규 표현식을 기반으로 포인트컷을 매칭한다.
     * TruePointcut : 항상 참을 반환한다.
     * AnnotationMatchingPointcut : 애노테이션으로 매칭한다.
     * AspectJExpressionPointcut : aspectJ 표현식으로 매칭한다.
     * 가장 중요한 것은 aspectJ 표현식
     * 여기에서 사실 다른 것은 중요하지 않다. 실무에서는 사용하기도 편리하고 기능도 가장 많은 aspectJ
     * 표현식을 기반으로 사용하는 AspectJExpressionPointcut 을 사용
     * */
    @Test
    @DisplayName("스프링이 제공하는 포인트컷")
    void advisorTest3() {
        //실제 객체 생성
        ServiceInterface target = new ServiceImpl();

        //프록시 팩토리 객체 생성, 실제 객체 주입
        ProxyFactory proxyFactory = new ProxyFactory(target);

        /**NameMatchMethodPointcut 사용
         *
         * NameMatchMethodPointcut :
         * 메서드 이름을 기반으로 매칭한다. 내부에서는 PatternMatchUtils 를 사용한다.
         * 예) *xxx* 허용
         * JdkRegexpMethodPointcut : JDK 정규 표현식을 기반으로 포인트컷을 매칭한다.
         * TruePointcut : 항상 참을 반환한다.
         * AnnotationMatchingPointcut : 애노테이션으로 매칭한다.
         * AspectJExpressionPointcut : aspectJ 표현식으로 매칭한다.
         *
         * 가장 중요한 것은 aspectJ 표현식
         * 여기에서 사실 다른 것은 중요하지 않다.
         * 실무에서는 사용하기도 편리하고 기능도 가장 많은 aspectJ
         * 표현식을 기반으로 사용하는 AspectJExpressionPointcut 을 사용
         *
         * 포인트컷 객체 생성
         * */
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();

        /**어디에 어드바이스 적용할지, 메서드이름이 save인 경우에만*/
        pointcut.setMappedNames("save");

        //어드바이저 객체 생성해 포인트컷과 어드바이스 파라미터로 줌
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());

        //프록시 팩토리에 어드바이저 추가
        proxyFactory.addAdvisor(advisor);

        //프록시 객체 가져옴, 실제 객체의 타입으로 캐스팅
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        //똑같이 save()만 어드바이스 적용됨
        proxy.save();
        proxy.find();
    }
}
