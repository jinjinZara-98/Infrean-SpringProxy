package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**http://localhost:8080/v1/no-log
//요구사항에 의해 이것을 호출 했을 때는 로그가 남으면 안된다.
//이런 문제를 해결하기 위해 메서드 이름을 기준으로 특정 조건을 만족할 때만 로그를 남기는 기능을 개발

//포인트컷 개념 처음 도입!!!

//LogTraceFilterHandler 는 기존 기능에 다음 기능이 추가되었다.
//특정 메서드 이름이 매칭 되는 경우에만 LogTrace 로직을 실행한다.
//이름이 매칭되지 않으면 실제 로직을 바로 호출한다.
//스프링이 제공하는 PatternMatchUtils.simpleMatch(..) 를 사용하면
//단순한 매칭 로직을 쉽게 적용할 수 있다.

//xxx : xxx가 정확히 매칭되면 참
//xxx* : xxx로 시작하면 참
//*xxx : xxx로 끝나면 참
//*xxx* : xxx가 있으면 참
//String[] patterns : 적용할 패턴은 생성자를 통해서 외부에서 받는다

//특정 패턴은 설정 클래스에 배열로 만들어놓음 */
public class LogTraceFilterHandler implements InvocationHandler {

    //프록시가 호출할 대상
    private final Object target;
    private final LogTrace logTrace;
    //메서드명이 이거일때만 로그를 남기는
    private final String[] patterns;

    //클라이언트가 원래 호출하려던 대상인 구현체 target 주입받음
    //생성자로 패턴을 주입받음
    public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    //Object proxy 프록시 자신
    //Method method 호출한 메서드, 프록시가 호출하는 메서드의 정보
    //Object[] args 메서드를 호출할 때 전달한 변수

    //method.invoke(target, args) :
    //리플렉션을 사용해서 target 인스턴스의 메서드를 실행한다.
    //args는 메서드 호출시 넘겨줄 인수이다
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //프록시가 호출하는 메서드 이름을 가져옴
        String methodName = method.getName();

        //가져온 메서드이름과 패턴 배열을 넣고 매칭되는지 확인,
        //매칭이 안되면 밑에 코드처럼 로그를 출력하는 코드를 실행안하는
        //method.invoke(target, args); 이 코드에 너무 연연하지 말자?
        if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            return method.invoke(target, args);
        }

        TraceStatus status = null;
        try {
            String message = method.getDeclaringClass().getSimpleName() + "." +
                    method.getName() + "()";
            status = logTrace.begin(message);

            //로직 호출
            Object result = method.invoke(target, args);
            logTrace.end(status);

            return result;
        } catch (Exception e) {

            logTrace.exception(status, e);
            throw e;
        }
    }
}
