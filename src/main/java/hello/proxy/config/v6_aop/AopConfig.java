package hello.proxy.config.v6_aop;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v6_aop.aspect.LogTraceAspect;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
/**
 * V1, V2 애플리케이션은 수동으로 스프링 빈으로 등록해야 동작
 *
 * 이 설정 클래스에 수동으로 빈 등록
 *
 * 그럼 메인 클래스가 여기에 있는 빈들만 등록하는데
 * 등록하려는 빈들을 빈 후처리기가 프록시 빈들로 바꿔 등록
 *
 * 어드바이저만 따로 만들어 등록
 * 어드바이저 안의 포인트컷 조건을 보고 프록시 객체를 만들지 판단하고
 * 만들어야 하면 등록하려던 빈이 아닌 프록시 객체를 만들어 빈으로 등록
 *
 * 스프링에 빈으로 자동으로 등록되는 AnnotationAwareAspectJAutoProxyCreator )인 빈 후처리기는 Advisor 를
 * 자동으로 찾아와서 필요한 곳에 프록시를 생성하고 적용해준다고 했다. 자동 프록시 생성기는 여기에 추가로 하나의 역할을 더 하는데,
 * 바로 @Aspect 를 찾아서 이것을 Advisor 로 만들어준다. 쉽게 이야기해서 지금까지 학습한 기능에 @Aspect 를
 * Advisor 로 변환해서 저장하는 기능도 한다. 그래서 이름 앞에 AnnotationAware (애노테이션을
 * 인식하는)가 붙어 있는 것
 *
 * @Aspect를 어드바이저로 변환해서 저장하는 과정
 * 1. 실행: 스프링 애플리케이션 로딩 시점에 자동 프록시 생성기를 호출한다.
 * 2. 모든 @Aspect 빈 조회: 자동 프록시 생성기는 스프링 컨테이너에서 @Aspect 애노테이션이 붙은 스프링 빈을 모두 조회한다.
 * 3. 어드바이저 생성: @Aspect 어드바이저 빌더를 통해 @Aspect 애노테이션 정보를 기반으로 어드바이저를 생성한다.
 * 4. @Aspect 기반 어드바이저 저장: 생성한 어드바이저를 @Aspect 어드바이저 빌더 내부에 저장한다.
 * */
@Import({AppV1Config.class, AppV2Config.class})
public class AopConfig {

    /**
     * @Aspect를 적용한 어드바이저를 빈으로 등록
     *
     * LogTraceAspect 에 @Component 애노테이션을 붙여서
     * 컴포넌트 스캔을 사용해서 스프링 빈으로 등록해도 된다
     *
     * 빈 후처리기는 따로 등록하지 않아도 됨 */
    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }
}
