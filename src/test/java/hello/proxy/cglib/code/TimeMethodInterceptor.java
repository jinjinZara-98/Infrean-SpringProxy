package hello.proxy.cglib.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB에서 공통로직
 *
 * JDK 동적 프록시에서 실행 로직을 위해 InvocationHandler 를 제공했듯이,
 * CGLIB는 MethodInterceptor 를 제공
 * TimeMethodInterceptor 는 MethodInterceptor 인터페이스를 구현해서 CGLIB 프록시의 실행 로직을 정의한다.
 * */
@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {

    //항상 프록시는 내가 호출할 대상 클래스 필요
    private final Object target;

    //생상지러 주입받음, 실제 객체
    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }

    //JDK 동적 프록시를 설명할 때 예제와 거의 같은 코드이다.
    //Object target : 프록시가 호출할 실제 대상
    //proxy.invoke(target, args) : 실제 대상을 동적으로 호출한다.
    //참고로 method 를 사용해도 되지만, CGLIB는 성능상 MethodProxy proxy 를 사용하는 것을 권장*/

    //obj : CGLIB가 적용된 객체, 프록시 자신
    //method : 호출된 메서드, 프록시하 호출하는 메서드이 정보
    //args : 메서드를 호출하면서 전달된 인수
    //methodProxy : 메서드 호출에 사용
    //JDK 동적 프록시보다 파라미터가 하나 더 많다
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = methodProxy.invoke(target, args);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);

        return result;
    }
}
