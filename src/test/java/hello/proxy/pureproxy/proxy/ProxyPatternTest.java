package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.Test;


public class ProxyPatternTest {


    /**
     * 테스트 코드에서는 client.execute() 를 3번 호출한다.
     * 데이터를 조회하는데 1초가 소모되므로 총 3초의 시간이 걸린다.
     * 실행 결과
     * RealSubject - 실제 객체 호출
     * RealSubject - 실제 객체 호출
     * RealSubject - 실제 객체 호출
     * client.execute()을 3번 호출하면 다음과 같이 처리된다.
     * 1. client -> realSubject 를 호출해서 값을 조회한다. (1초)
     * 2. client -> realSubject 를 호출해서 값을 조회한다. (1초)
     * 3. client -> realSubject 를 호출해서 값을 조회한다. (1초)
     *
     *그런데 이 데이터가 한번 조회하면 변하지 않는 데이터라면 어딘가에 보관해두고 이미 조회한 데이터를
     * 사용하는 것이 성능상 좋다. 이런 것을 캐시라고 한다.
     * */
    //프록시 패턴의 주요 기능은 접근 제어이다. 캐시도 접근 자체를 제어하는 기능 중 하나
    @Test
    void noProxyTest() {
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);

        client.execute();
        client.execute();
        client.execute();
    }


    /**
     * client.execute()을 3번 호출하면 다음과 같이 처리된다.
     * 1. client의 cacheProxy 호출 cacheProxy에 캐시 값이 없다. realSubject를 호출, 결과를 캐시에 저장 (1초)
     * 2. client의 cacheProxy 호출 cacheProxy에 캐시 값이 있다. cacheProxy에서 즉시 반환 (0초)
     * 3. client의 cacheProxy 호출 cacheProxy에 캐시 값이 있다. cacheProxy에서 즉시 반환 (0초)
     * 결과적으로 캐시 프록시를 도입하기 전에는 3초가 걸렸지만,
     * 캐시 프록시 도입 이후에는 최초에 한번만 1초가 걸리고, 이후에는 거의 즉시 반환한다.
     * 정리
     *
     * 프록시 패턴의 핵심은 RealSubject 코드와 클라이언트 코드를 전혀 변경하지 않고,
     *
     * 프록시를 도입해서 접근 제어를 했다는 점이다.
     * 그리고 클라이언트 코드의 변경 없이 자유롭게 프록시를 넣고 뺄 수 있다. 실제 클라이언트 입장에서는
     * 프록시 객체가 주입되었는지, 실제 객체가 주입되었는지 알지 못한다.
     * */
    @Test
    void cacheProxyTest() {
        //RealSubject기 사바?
        RealSubject realSubject = new RealSubject();

        //중간에 CacheProxy를 생성해 CacheProxy 생성자에 RealSubject 객체를 주입
        //그리고 CacheProxy객체를 ProxyPatternClient에 의존주입
        //CacheProxy도 Subject 구현하므로 주입 가능
        CacheProxy cacheProxy = new CacheProxy(realSubject);

        //캐시프록시를 주입받고
        ProxyPatternClient client = new ProxyPatternClient(cacheProxy);

        //캐시프록시도 subject를 구현하기 때문에
        //execute() 안에 subject.operation()를 사용가능
        client.execute();
        client.execute();
        client.execute();

        //execute()이 호출하는거
    }
}
