package hello.proxy.config.v6_aop.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

/**
 * @Aspect : 애노테이션 기반 프록시를 적용할 때 필요하다.
 *
 * @Aspect 을 붙인다고 어드바이스가 빈으로 등록되지 않는다
 * AopConfig 에서 수동 빈 등록함함
 **/
@Slf4j
@Aspect
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    /**
     * @Around 의 값에 포인트컷 표현식을 넣는다. 표현식은 AspectJ 표현식을 사용한다.
     * @Around으로 포인트컷, 어디에 적용할건지
     * */
    @Around("execution(* hello.proxy.app..*(..))")

    /**어드바이스 로직
     * @Around 의 메서드는 어드바이스( Advice )가 된다
     * 어드바이저를 편리하게 생성하는*/
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus status = null;

        try {
            /**
             * ProceedingJoinPoint joinPoint : 어드바이스에서 살펴본 MethodInvocation invocation 과 유사한 기능이다.
             * 내부에 실제 호출 대상, 전달 인자, 그리고 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함되어 있다.
             * joinPoint.proceed() : 실제 호출 대상( target )을 호출한다
             */

             /**
             * invocation에서 필요한 것들을 다 꺼낼 수 있음.
             * invocation.proceed()가 타겟을 찾아서 그 타겟에 있는 다음 실체를 실행
             * */
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            //로직 호출
            Object result = joinPoint.proceed();

            logTrace.end(status);

            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
