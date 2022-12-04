package hello.proxy.pureproxy.decorator.code;

//컴포넌트를 구현한건 두 가지 리얼컴포넌트 데코레이터
//데코레이터 하위에 실제 데코레이터를 생성, 추상클래스를 만들고 이 속성을 가지게 공통속성을 만듬
public interface Component {
    String operation();
}
