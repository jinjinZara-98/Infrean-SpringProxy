package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

//데코레이터 아님, 클라이언트임
@Slf4j
public class DecoratorPatternClient {

    private Component component;

    //Component 구현체 주입받음
    //Component를 구현한 데코레이터도 주입받을 수 있는
    public DecoratorPatternClient(Component component) {
        this.component = component;
    }

    public void execute() {
        //주입받은 구현체의 메서드 결과를 출력
        String result = component.operation();

        //데코레이터 안의 코드 다 실행 후 마지막으로
        log.info("result = {}", result);
    }
}
