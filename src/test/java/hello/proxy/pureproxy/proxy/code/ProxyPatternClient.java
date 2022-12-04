package hello.proxy.pureproxy.proxy.code;

public class ProxyPatternClient {

    //테스트에서 캐시프록시가 들어옴
    private Subject subject;

    //생성자, 캐시프록시도 Subject 구현하므로 캐시프록시도 주입이 가능
    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }

    //주입받은 Subject 객체로 operation()메서드
    //주입받은 프록시 객체의 operation() 사용
    public void execute() {
        subject.operation();
    }
}
