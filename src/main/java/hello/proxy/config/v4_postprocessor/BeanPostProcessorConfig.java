package hello.proxy.config.v4_postprocessor;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.config.v4_postprocessor.postprocessor.PackageLogTracePostProcessor;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** 빈 후처리기 등록

//@Import({AppV1Config.class, AppV2Config.class}) :
//V3는 컴포넌트 스캔으로 자동으로 스프링 빈으로 등록되지만, V1, V2 애플리케이션은 수동으로 스프링 빈으로 등록해야 동작한다.
//ProxyApplication 에서 등록해도 되지만 편의상 여기에 등록하자. */
@Slf4j
@Configuration
//V1 V2 수동으로 등록, V3는 컴포넌트 스캔 대상이 되므로 IMPORT할 필요 없음,
@Import({AppV1Config.class, AppV2Config.class})
public class BeanPostProcessorConfig {

    /**@Bean logTraceProxyPostProcessor() :
    * * 특정 패키지를 기준으로 프록시를 생성하는 빈 후처리기를 스프링 빈으로 등록한다.
    //빈 후처리기는 스프링 빈으로만 등록하면 자동으로 동작한다.
    //여기에 프록시를 패키지 정보( hello.proxy.app )와 어드바이저( getAdvisor(logTrace) )를 넘겨준다.
    //이제 프록시를 생성하는 코드가 설정 파일에는 필요 없다. 순수한 빈 등록만 고민하면 된다.
    //프록시를 생성하고 프록시를 스프링 빈으로 등록하는 것은 빈 후처리기가 모두 처리해준다*
    //프록시를 등록하는 코드가 다 없어짐, 중복되는 코드가 다 사라짐, PackageLogTracePostProcessor에 다 들어감

    //여기서는 생략했지만, 실행해보면 스프링 부트가 기본으로 등록하는 수 많은 빈들이 빈 후처리기를 통과하는 것을 확인할 수 있다.
    //여기에 모두 프록시를 적용하는 것은 올바르지 않다. 꼭 필요한 곳에만 프록시를 적용해야 한다.
    //여기서는 basePackage 를 사용해서 v1~v3 애플리케이션 관련 빈들만 프록시 적용 대상이 되도록 했다.
    //v1: 인터페이스가 있으므로 JDK 동적 프록시가 적용된다.
    //v2: 구체 클래스만 있으므로 CGLIB 프록시가 적용된다.
    //v3: 구체 클래스만 있으므로 CGLIB 프록시가 적용된다.

    //컴포넌트 스캔에도 적용
    //여기서 중요한 포인트는 v1, v2와 같이 수동으로 등록한 빈 뿐만 아니라 컴포넌트 스캔을 통해 등록한 v3 빈들도 프록시를 적용할 수 있다는 점이다.
    //v3 컨트롤러 서비스 리포지토리는 @RestController를 붙여 자동으로 빈 등록됨
    //이것은 모두 빈 후처리기 덕분이다

    //프록시 적용 대상 여부 체크
    //애플리케이션을 실행해서 로그를 확인해보면 알겠지만, 우리가 직접 등록한 스프링 빈들 뿐만 아니라
    //스프링 부트가 기본으로 등록하는 수 많은 빈들이 빈 후처리기에 넘어온다.
    //그래서 어떤 빈을 프록시로 만들 것인지 기준이 필요하다.
    //여기서는 간단히 basePackage 를 사용해서 특정 패키지를 기준으로 해당 패키지와 그 하위 패키지의 빈들을 프록시로 만든다.
    //스프링 부트가 기본으로 제공하는 빈 중에는 프록시 객체를 만들 수 없는 빈들도 있다.
    //따라서 모든 객체를 프록시로 만들 경우 오류가 발생한다
    //v1 v2 v2 모두 동일한 결과 나옴
     */
    @Bean
    public PackageLogTracePostProcessor logTracePostProcessor(LogTrace logTrace) {

        /** 어드바이저 만드는 메서드 따로 만들어 어드바이스와 포인크컷 설정함*/
        return new PackageLogTracePostProcessor("hello.proxy.app", getAdvisor(logTrace));
    }

    private Advisor getAdvisor(LogTrace logTrace) {

        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");

        //advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        //포인트컷과 어드바이스를 파라미터로 받은 어드바이저를 반환
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
