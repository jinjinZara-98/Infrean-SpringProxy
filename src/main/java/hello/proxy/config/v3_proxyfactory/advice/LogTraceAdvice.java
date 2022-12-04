package hello.proxy.config.v3_proxyfactory.advice;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/** 실제 어플리케이션에 적용할 어드바이스 만들기, MethodInterceptor는 어드바이스의 자식 클래스
 *
 * MethodInterceptor를 구현해야함, CGLIB와 이름이 똑같아서 헷갈릴수잇음
 */
public class LogTraceAdvice implements MethodInterceptor {

    //프록시에 적용할 로직, 공통 로직, 변하지 않는 로직

    /**실제 객체인 target 받을 필요 없음
    //프록시 팩토리가 실제 객체 받음*/

    private final LogTrace logTrace;

    //타겟을 파라미터로 받을 필요 없음, 스프링이 제공하는 MethodInterceptor로 편리하게 사용
    public LogTraceAdvice(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    /**
     * invocation에서 필요한 것들을 다 꺼낼 수 있음.
     * invocation.proceed()가 타겟을 찾아서 그 타겟에 있는 다음 실체를 실행*/
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        TraceStatus status = null;

        try {
            //invocation 메서드 정보 가져오고
            //메서드 이름 토대로 메시지 만듬
            Method method = invocation.getMethod();
            String message = method.getDeclaringClass().getSimpleName() + "." +
                    method.getName() + "()";

            status = logTrace.begin(message);

            /**로직 호출, 실제 객체 호출, invocation.proceed()로 편리하게 */
            Object result = invocation.proceed();

            logTrace.end(status);

            return result;

        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
