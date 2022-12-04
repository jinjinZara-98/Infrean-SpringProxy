package hello.proxy.pureproxy.concreteproxy.code;

//인터페이스 없고 구현 클래스만 있을 때 프록시 적용
public class ConcreteClient {

    private ConcreteLogic concreteLogic;

    public ConcreteClient(ConcreteLogic concreteLogic) {
        this.concreteLogic = concreteLogic;
    }

    public void execute() {
        concreteLogic.operation();
    }
}
