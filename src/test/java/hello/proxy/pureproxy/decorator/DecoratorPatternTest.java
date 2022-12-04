package hello.proxy.pureproxy.decorator;

import hello.proxy.pureproxy.decorator.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

//부가 기능 추가
//앞서 설명한 것 처럼 프록시를 통해서 할 수 있는 기능은 크게 접근 제어와 부가 기능 추가라는 2가지로 구분한다.
//앞서 프록시 패턴에서 캐시를 통한 접근 제어를 알아보았다. 이번에는 프록시를 활용해서 부가 기능을 추가해보자.
//이렇게 프록시로 부가 기능을 추가하는 것을 데코레이터 패턴이라 한다.
/**데코레이터 패턴: 원래 서버가 제공하는 기능에 더해서 부가 기능을 수행한다.
//예) 요청 값이나, 응답 값을 중간에 변형한다.
//예) 실행 시간을 측정해서 추가 로그를 남긴다.

여기서 데코레이터는 Message와 Time
 반환값에 값을 추가하고, 시간을 측정하는*/
@Slf4j
public class DecoratorPatternTest {

    @Test
    void noDecorator() {
        Component realComponent = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
        client.execute();
    }

    //응답 값을 꾸며주는 데코레이터
    @Test
    void decorator1() {
        Component realComponent = new RealComponent();

        //프록시처럼 중간에 껴듬, 구현체 대신 받아 부가 기능 추가
        Component messageDecorator = new MessageDecorator(realComponent);

        DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
        //호출
        //데코레이터의 execute()를 호출하면 데코레이터 execute() 안에서
        //실제 객체 execute()를 호출하고 거기에 부가 기능 추가해 반환하는
        client.execute();
    }

    //실행 시간을 측정하는 데코레이터, 프록시가 프록시를 호출하는, 위로 올라가며 호출하는
    @Test
    void decorator2() {
        Component realComponent = new RealComponent();

        //이번엔 두 개를 중간에 껴, 만든 데코레이터 둘 다 Component 구현체이므로 주입 가능

        //구현체를 받아 로그 메시지를 더해 출력하고
        Component messageDecorator = new MessageDecorator(realComponent);

        //시간을 측정하는 코드 추가
        Component timeDecorator = new TimeDecorator(messageDecorator);

        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        client.execute();
    }
}
/**
 * 여기서 생각해보면 Decorator 기능에 일부 중복이 있다. 꾸며주는 역할을 하는 Decorator 들은 스스로
 * 존재할 수 없다. 항상 꾸며줄 대상이 있어야 한다. 따라서 내부에 호출 대상인 component 를 가지고 있어야
 * 한다. 그리고 component 를 항상 호출해야 한다. 이 부분이 중복이다. 이런 중복을 제거하기 위해
 * component 를 속성으로 가지고 있는 Decorator 라는 추상 클래스를 만드는 방법도 고민할 수 있다.
 * 이렇게 하면 추가로 클래스 다이어그램에서 어떤 것이 실제 컴포넌트 인지, 데코레이터인지 명확하게
 * 구분할 수 있다.
 *
 * 프록시 패턴 vs 데코레이터 패턴
 * 여기까지 진행하면 몇가지 의문이 들 것이다.
 * Decorator 라는 추상 클래스를 만들어야 데코레이터 패턴일까?
 * 프록시 패턴과 데코레이터 패턴은 그 모양이 거의 비슷한 것 같은데?
 *
 * 의도(intent)
 * 사실 프록시 패턴과 데코레이터 패턴은 그 모양이 거의 같고, 상황에 따라 정말 똑같을 때도 있다.
 * 그러면 둘을 어떻게 구분하는 것일까?
 * 디자인 패턴에서 중요한 것은 해당 패턴의 겉모양이 아니라 그 패턴을 만든 의도가 더 중요하다.
 * 따라서 의도에 따라 패턴을 구분한다.
 * 프록시 패턴의 의도: 다른 개체에 대한 접근을 제어하기 위해 대리자를 제공
 * 데코레이터 패턴의 의도: 객체에 추가 책임(기능)을 동적으로 추가하고, 기능 확장을 위한 유연한 대안 제공
 *
 * 정리
 * 프록시를 사용하고 해당 프록시가 접근 제어가 목적이라면 프록시 패턴이고, 새로운 기능을 추가하는 것이
 * 목적이라면 데코레이터 패턴이 된다.
 *
 */