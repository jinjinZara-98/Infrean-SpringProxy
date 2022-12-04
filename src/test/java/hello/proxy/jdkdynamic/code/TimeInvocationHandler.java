package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * TimeInvocationHandler 은 InvocationHandler 인터페이스를 구현한다.
 * 이렇게해서 JDK 동적 프록시에 적용할 공통 로직을 개발할 수 있다.
 * */
@Slf4j
public class TimeInvocationHandler implements InvocationHandler {

    //동적 프록시가 호출할 대상
    private final Object target;

    public TimeInvocationHandler(Object target) {
        this.target = target;
    }

    //Object proxy 프록시 자신
    //Method method 호출한 메서드, 프록시가 호출하는 메서드
    //Object[] args 메서드를 호출할 때 전달한 변수

    //method.invoke(target, args) :
    //리플렉션을 사용해서 target 인스턴스의 메서드를 실행한다.
    //args는 메서드 호출시 넘겨줄 인수이다
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        //call이 넘어옴
        //이전에는 target.target의 메서드 이랬는데
        //생성자로 받은 실제 객체, 메서드를 호출할 때 전달하는 변수를 넘겨줌
        //이렇게 실제 객체인 target을 넘겨주면 target의 메서드 호출
        Object result = method.invoke(target, args);

        long endTime = System.currentTimeMillis();

        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 resultTime={}", resultTime);

        return result;
    }
}
