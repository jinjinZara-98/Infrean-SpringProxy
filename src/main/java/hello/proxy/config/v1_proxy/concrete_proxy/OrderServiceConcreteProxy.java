package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

public class OrderServiceConcreteProxy extends OrderServiceV2 {

    private final OrderServiceV2 target;
    private final LogTrace logTrace;

    //상속받아 생성자 생성
    //클래스 기반 프록시의 단점
    //super(null) : OrderServiceV2 : 자바 기본 문법에 의해 자식 클래스를 생성할 때는 항상 super() 로
    //부모 클래스의 생성자를 호출. 이 부분을 생략하면 기본 생성자가 호출된다.
    //그런데 부모 클래스인 OrderServiceV2 는 기본 생성자가 없고,
    //생성자에서 파라미터 1개를 필수로 받는다. 따라서 파라미터를 넣어서 super(..) 를 호출해야 한다.
    //프록시는 부모 객체의 기능을 사용하지 않기 때문에 super(null) 을 입력해도 된다.
    //인터페이스 기반 프록시는 이런 고민을 하지 않아도 된다
    public OrderServiceConcreteProxy(OrderServiceV2 target, LogTrace logTrace) {
        super(null);
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public void orderItem(String itemId) {

        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderService.orderItem()");
            //target 호출
            target.orderItem(itemId);
            logTrace.end(status);
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
