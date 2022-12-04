package hello.proxy.config.v1_proxy.interface_proxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

/**
 * 템플릿, 템플릿 콜백, 전략 모두 로그 추적기에 적용하려면 원본 수정해야함
 * 프록시는 원본 코드 손대지 않고 수정 가능
 *
 * OrderControllerImpl과 같은 인터페이스 구현
 * 이전에는 OrderControllerImpl에 이런 코드를 모두 추가한
 * 프록시를 사용해 이 부분을 프록시가 대신 처리해주는
 * */
@RequiredArgsConstructor
public class OrderControllerInterfaceProxy implements OrderControllerV1 {

    //@Autowired 생략됨
    //OrderControllerV1 구현체를 주입받음
    //수동 등록된 빈이 주입되는
    private final OrderControllerV1 target;
    private final LogTrace logTrace;

    @Override
    public String request(String itemId) {

        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderController.request()");
            //target 호출
            /**
             * 클라이언트가 호출할 실제 객체 OrderControllerImplV1의 request를
             * 프록시가 대신 호출하는
             * */
            String result = target.request(itemId);
            logTrace.end(status);

            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    //보안상의 이유로 로그를 찍지 않는다
    @Override
    public String noLog() {
        return target.noLog();
    }
}
