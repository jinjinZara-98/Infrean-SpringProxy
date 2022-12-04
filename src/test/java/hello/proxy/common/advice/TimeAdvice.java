package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 스프링이 제공하는 프록시 팩토리 사용
 *
 * JDK 동적 프록시가 제공하는 InvocationHandler 와
 * CGLIB가 제공하는 MethodInterceptor 의 개념과 유사
 *
 * 프록시 팩토리 사용하면 둘 대신에 Advice 사용
 *
 * Advice는 MethodInterceptor를 구현해
 * MethodInterceptor 는 Interceptor 를 상속하고 Interceptor 는 Advice 인터페이스를 상속
 *
 * CGLIB가 제공하는 MethodInterceptor과 구현해야하는 이름이 똑같아 헷갈릴 수 있음
 *
 * import org.springframework.cglib.proxy.MethodInterceptor;
 * import org.aopalliance.intercept.MethodInterceptor;
 * */
@Slf4j
public class TimeAdvice implements MethodInterceptor {

    /**
     * 타겟을 안넣어줘도 됨, 타겟 클래스 정보가 MethodInvocation 객체 안에 있음
     * 프록시 팩토리로 프록시를 생성하는 단계에서 이미 target 정보를 파라미터로 전달받기 때문
     *
     * MethodInvocation invocation
     * 내부에는 다음 메서드를 호출하는 방법, 현재 프록시 객체 인스턴스, args , 메서드 정보 등이
     * 포함되어 있다. 기존에 파라미터로 제공되는 부분들이 이 안으로 모두 들어갔다고 생각하면 된다
     *
     * InvocationHandler, MethodInterceptor처럼
     * obj : CGLIB가 적용된 객체, 프록시 자신
     * method : 호출된 메서드, 프록시하 호출하는 메서드이 정보
     * args : 메서드를 호출하면서 전달된 인수
     * methodProxy : 메서드 호출에 사용
     * 와 같이 여러 개 파라미터가 있지 않고 하나의 파라미터만
     * */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");

        //실제 객체 호출전 측정 시작
        long startTime = System.currentTimeMillis();

        /**
         * invocation에서 필요한 것들을 다 꺼낼 수 있음.
         * invocation.proceed()가 타겟을 찾아서 그 타겟에 있는 다음 실체를 실행*/
        Object result = invocation.proceed();
//        invocation.getMethod().getName()

        //실제 객체 끝나고 측정 끝
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);

        return result;
    }
}
