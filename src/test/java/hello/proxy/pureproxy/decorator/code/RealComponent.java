package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

//그냥 구현체, 데코레이터 아님
//구현체, 단순히 로그를 남기고 data문자를 반환하는
@Slf4j
public class RealComponent implements Component {

    @Override
    public String operation() {
        log.info("RealComponent 실행");
        return "data";
    }
}
