package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecorator implements Component {

    //리얼 객체인 리얼컴포넌트 들어옴
    private Component component;

    //구현체 주입
    //프록시가 호출해야 하는 대상을 component 에 저장
    public MessageDecorator(Component component) {
        this.component = component;
    }

    //그니까 데코레이터 operation()를 호출하면 구현체 operation()를 호출해 부가 기능 더해 반환
    @Override
    public String operation() {
        log.info("MessageDecorator 실행");

        //리얼 객체 호출
        //data -> *****data*****
        //프록시와 연결된 대상을 호출
        //구현체 메서드 결과 받아 거기에 부가 기능 추가
        String result = component.operation();
        String decoResult = "*****" + result + "*****";
        log.info("MessageDecorator 꾸미기 적용 전 = {}, 적용 후 = {}", result, decoResult);

        return decoResult;
    }
}
