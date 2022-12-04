package hello.proxy.app.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 코드를 보면 advenced의 v0과 똑같다
 * 여기 컨트롤러 코드를 수정하지 않고 로그 추적기를 도입한 것!!!
 *
 * V2는 인터페이스 없는 구체 클래스 - 스프링 빈으로 수동 등록
 * 인터페이스 없이 상속으로 프록시 만드는
 *
 * 스프링MVC는 타입에 @Controller 또는 @RequestMapping 애노테이션이 있어야 스프링 컨트롤러로 인식
 * 수동등록하기 위해 @Controller안쓰고, @RequestMapping만 씀 컴포넌트 스캔 대상이 안됨
 */
@Slf4j
@RequestMapping
@ResponseBody
public class OrderControllerV2 {

    private final OrderServiceV2 orderService;

    public OrderControllerV2(OrderServiceV2 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v2/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v2/no-log")
    public String noLog() {
        return "ok";
    }

}
