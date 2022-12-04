package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.Test;

/**
 * 구체 클래스 기반 프록, 인터페이스 사용 하지 않는
 */
public class ConcreteProxyTest {

    //자바의 다형성은 인터페이스를 구현하든, 아니면 클래스를 상속하든 상위 타입만 맞으면 다형성이 적용된다.
    //쉽게 이야기해서 인터페이스가 없어도  프록시를 만들수 있다는 뜻
    @Test
    void noProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteClient client = new ConcreteClient(concreteLogic);
        client.execute();
    }

    //클라이언트가 TimeProxy를 TimeProxy가 ConcreteLogi을 참조하도록
    //실행 결과를 보면 인터페이스가 없어도 클래스 기반의 프록시가 잘 적용된 것을 확인할 수 있다.
    @Test
    void addProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();

        //중간에 껴서
        TimeProxy timeProxy = new TimeProxy(concreteLogic);

        /**여기서 핵심은 ConcreteClient 의 생성자에 concreteLogic 이 아니라 timeProxy 를 주입하는 부분
        //TimeProxy가 ConcreteLogic의 자식이니 주입 가능**/
        ConcreteClient client = new ConcreteClient(timeProxy);
        client.execute();
    }
}
