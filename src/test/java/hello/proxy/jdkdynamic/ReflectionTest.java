package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** 동적 프록시, 일반적으로 리플렉션 사용 안함*/
@Slf4j
public class ReflectionTest {

    //공통 로직1과 공통 로직2는 호출하는 메서드만 다르고 전체 코드 흐름이 완전히 같다.
    //먼저 start 로그를 출력한다.
    //어떤 메서드를 호출한다.
    //메서드의 호출 결과를 로그로 출력한다.
    //여기서 공통 로직1과 공통 로직 2를 하나의 메서드로 뽑아서 합칠 수 있을까
    //쉬워 보이지만 메서드로 뽑아서 공통화하는 것이 생각보다 어렵다. 왜냐하면 중간에 호출하는 메서드가 다르기 때문이다.
    //호출하는 메서드인 target.callA() , target.callB() 이 부분만 동적으로 처리할 수 있다면 문제를 해결할 수 있을 듯 하다
    //이럴 때 사용하는 기술이 바로 리플렉션이다. 리플렉션은 클래스나 메서드의 메타정보를 사용해서 동적으로 호출하는 메서드를 변경할 수 있다
    @Test
    void reflection0() {
        Hello target = new Hello();

        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA(); //호출하는 메서드가 다음
        log.info("result={}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB(); //호출하는 메서드가 다음
        log.info("result={}", result2);
        //공통 로직2 종료
    }

    @Test
    void reflection1() throws Exception {
        //클래스 정보, 내부에 있을 때는 $표시
        //Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello") :
        //클래스 메타정보를 획득한다. 참고로 내부 클래스는 구분을 위해 $ 를 사용한다.
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();


        //callA 메서드 정보, 문자로 얻음
        //classHello.getMethod("call") : 해당 클래스의 call 메서드 메타정보를 획득한다.
        Method methodCallA = classHello.getMethod("callA");

        //methodCallA.invoke(target) : 획득한 메서드 메타정보로 실제 인스턴스의 메서드를 호출한다. 여기서
        //methodCallA 는 Hello 클래스의 callA() 이라는 메서드 메타정보이다.
        //methodCallA.invoke(인스턴스) 를 호출하면서 인스턴스를 넘겨주면 해당 인스턴스의 callA() 메서드를 찾아서 실행한다.
        //여기서는 target 의 callA() 메서드를 호출한다.
        //기존의 callA() , callB() 메서드를 직접 호출하는 부분이 Method 로 대체되었다.
        //덕분에 이제 공통로직을 만들 수 있게 되었다
        Object result1 = methodCallA.invoke(target);
        log.info("result1={}", result1);

        //callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target);
        log.info("result2={}", result2);
    }

    //추상화해서 메서드로 넘긴다
    @Test
    void reflection2() throws Exception {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        //메서드가 있는 클래스 객체 생성
        Hello target = new Hello();
        //클래스 정보의 메서드로 메서드 이름 파라미티로 줘 메서드 객체 만듬
        Method methodCallA = classHello.getMethod("callA");
        //메서드 정보와 클래스의 객체 파라미터로
        dynamicCall(methodCallA, target);

        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }
    //정적인 target.callA() , target.callB() 코드를 리플렉션을 사용해서
    //Method 라는 메타정보로 추상화

    //공통 로직1, 공통 로직2를 한번에 처리할 수 있는 통합된 공통 처리 로직
    //Object target : 실제 실행할 인스턴스 정보
    private void dynamicCall(Method method, Object target) throws Exception {
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);
    }

    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }
        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}
//리플렉션을 사용하면 클래스와 메서드의 메타정보를 사용해서 애플리케이션을 동적으로 유연하게 만들 수 있다.
//하지만 리플렉션 기술은 런타임에 동작하기 때문에, 컴파일 시점에 오류를 잡을 수 없다