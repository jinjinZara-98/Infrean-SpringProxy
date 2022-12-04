package hello.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;

//클라이언트가 호출하는 서버와 같은 인터페이스를 사용
@Slf4j
public class CacheProxy implements Subject {

    //실제 객체에 접근, 프록시에서 호출해야할 대상을 target이라 함
    private Subject target;

    //처음 operation() 호출하면 data란 값 들어감
    private String cacheValue;

    //Subject구현체 주입받음
    public CacheProxy(Subject target) {
        this.target = target;
    }

    @Override
    public String operation() {

        log.info("프록시 호출");

        //처음 호출했을때는 cacheValue 없음,
        //처음 호출이 아니면 Subject구현체 operation() 실행 할 필요 없이바로 리턴
        //주입받은 Subject구현체 operation() 결과값 캐시값에 담음
        if (cacheValue == null) {
            cacheValue = target.operation();
        }

        //첫 호출이 아니라면 저장되어 있던 구현체의 값 계속 반환
        return cacheValue;
    }
}