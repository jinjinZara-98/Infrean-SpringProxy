package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**JDK 동적 프록시 적용, 설정 클래스 따로 있음
 *
 * 공통 로직 들어있는 템플릿?
 *
 */
public class LogTraceBasicHandler implements InvocationHandler {

    //프록시가 호출할 대상
    private final Object target;
    private final LogTrace logTrace;

    //클라이언트가 원래 호출하려던 대상인 구현체 target 주입받음
    public LogTraceBasicHandler(Object target, LogTrace logTrace) {
        this.target = target;
        this.logTrace = logTrace;
    }

    //Object proxy 프록시 자신
    //Method method 호출한 메서드, 프록시가 호출하는 메서드
    //Object[] args 메서드를 호출할 때 전달한 변수

    //method.invoke(target, args) :
    //리플렉션을 사용해서 target 인스턴스의 메서드를 실행한다.
    //args는 메서드 호출시 넘겨줄 인수이다
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        TraceStatus status = null;
        try {
            //LogTrace 에 사용할 메시지이다. 프록시를 직접 개발할 때는 "OrderController.request()" 와
            //같이 프록시마다 호출되는 클래스와 메서드 이름을 직접 남겼다. 이제는 Method 를 통해서 호출되는
            //메서드 정보와 클래스 정보를 동적으로 확인할 수 있기 때문에 이 정보를 사용
            String message = method.getDeclaringClass().getSimpleName() + "." +
                    method.getName() + "()";

            status = logTrace.begin(message);

            //로직 호출
            //구현체인 target의 메서드 여기서 호출
            Object result = method.invoke(target, args);

            logTrace.end(status);

            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
