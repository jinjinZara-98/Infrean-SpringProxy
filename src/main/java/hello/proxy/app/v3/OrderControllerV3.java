package hello.proxy.app.v3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 자동 빈 등록, 컴포넌트 스캔 쓸거기 때문에
 */
@Slf4j
@RestController
public class OrderControllerV3 {

    private final OrderServiceV3 orderService;

    //의존 주입 코드를 써줄 설정 클래스가 없으니
    //빈으로 등록될 클래스 안에서 자동 의존 주입
    //생성자 하나므로 @Autowired 생략됨
    public OrderControllerV3(OrderServiceV3 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v3/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v3/no-log")
    public String noLog() {
        return "ok";
    }

}
