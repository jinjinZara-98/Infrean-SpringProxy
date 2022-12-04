package hello.proxy.config.v4_postprocessor.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
/**빈 후처리기 만들기, 스프링 컨테어니에 빈을 등록하기 전에 빈 객체 조작
 *
 * BeanPostProcessor를 구현해야함
 *
 * 원본 객체를 프록시 객체로 변환
 * */
//특정 패키지 밑으로 작업, 특정 패키지에 있는 빈들만 프록시로 만드는
@Slf4j
public class PackageLogTracePostProcessor implements BeanPostProcessor {

    private final String basePackage;
    private final Advisor advisor;

    /** 어드바이스와 포인트컷을 갖고있는 어드바이저를 외부에서 주입
     * 모든 스프링 빈들에 프록시를 적용할 필요는 없다.
     * 여기서는 특정 패키지와 그 하위에 위치한 스프링 빈들만 프록시를 적용
     * 여기서는 hello.proxy.app 과 관련된 부분에만 적용하면 된다.
     * 다른 패키지의 객체들은 원본 객체를 그대로 반환*/
    public PackageLogTracePostProcessor(String basePackage, Advisor advisor) {
        this.basePackage = basePackage;
        this.advisor = advisor;
    }

    /**빈의 초기화 끝나고 나서, 빈 객체가 만들어지고 나서 프록시 적용
     *
     * 이 메서드에서 반환하는 객체가 스프링 컨테이너에 등록됨됨*/
   @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        //빈 이름과, 빈의 클래스 타입을 로그로 출력
        log.info("param beanName={} bean={}", beanName, bean.getClass());

        //프록시 적용 대상 여부 체크
        //프록시 적용 대상이 아니면 원본을 그대로 진행
        //app패키지 하위에 있는 것들만 프록시를 적용

        //빈의 패키지 이름 가져옴
        String packageName = bean.getClass().getPackageName();

        /**빈 패키지 이름이 생성자로 들어온 basePackage으로 시작하지 않으면
        //등록하려던 빈 그냥 컨테이너에 등록*/
        if (!packageName.startsWith(basePackage)) {
            return bean;
        }

        //프록시 대상이면 프록시 팩토리 파라미터에
        //원래 등록하려던 빈을 주입
        ProxyFactory proxyFactory = new ProxyFactory(bean);

        //프록시에 어드바이저 추가, 외부에서 받은
        proxyFactory.addAdvisor(advisor);

        //캐스팅 안하고 오브젝트로 꺼냄
        Object proxy = proxyFactory.getProxy();

        //원래 등록하려던 빈의 클래스 타입, 프록시 객체의 클래스 타입 로그로 출력
        log.info("create proxy: target={} proxy={}", bean.getClass(), proxy.getClass());

        //프록시 객체 스프링 컨테이너에 등록
        return proxy;
    }
}
