package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

//스프링은 @Controller 또는 @RequestMapping 이 있어야 스프링 컨트롤러로 인식
//그리고 스프링 컨트롤러로 인식해야, HTTP URL이 매핑되고 동작한다.
//이 애노테이션은 인터페이스에 사용해도 된다.
//수동 등록하는
@RequestMapping
@ResponseBody
public interface OrderControllerV1 {

    //logTrace 적용할 대상
    //@RequestParam 넣어놔야함, 이게 없으면 실행 시점에 itemId 인식이 안됨
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);

    //logTrace 적용하지 않을 대상
    @GetMapping("/v1/no-log")
    String noLog();
}
