package hello.proxy.app.v1;

/**코드를 보면 advenced의 v0과 똑같다. Impl이여서 살짝 다르지만
 * 인터페이스가 아닌 클래스를 상속하는 프록시는 똑같음
 * 여기 컨트롤러 코드를 수정하지 않고 로그 추적기를 도입한 것!!!*/
public class OrderControllerV1Impl implements OrderControllerV1 {

    private final OrderServiceV1 orderService;

    public OrderControllerV1Impl(OrderServiceV1 orderService) {
        this.orderService = orderService;
    }

    @Override
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @Override
    public String noLog() {
        return "ok";
    }
}
