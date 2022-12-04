package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeDecorator implements Component {

    private Component component;

    //MessageDecorator겍체가 들어옴
    //MessageDecorator도 Component의 구현체이므로
    public TimeDecorator(Component component) {
        this.component = component;
    }

    @Override
    public String operation() {
        log.info("TimeDecorator 실행");

        long startTime = System.currentTimeMillis();

        //operation()로 주입받은 구현체 MessageDecorator의 operation() 실행
        //MessageDecorator의 operation() 실행하면 RealComponent 의 operation() 실행
        //위에 과정의 시간을 측정
        String result = component.operation();

        long endTime = System.currentTimeMillis();

        long resultTime = endTime - startTime;

        log.info("TimeDecorator 종료 resultTime={}ms", resultTime);

        return result;
    }
}
