package TestArtifacts.Test11;

public class TestClass11_02 {

    public int member_variable=(int)Math.random();

    public int getSomeValue() {
        return getSomeValue2();
    }

    public int getSomeValue2(){
        return this.member_variable;
    }

    public void doSomething(int w) {
        int y = w + (int)Math.random();
        System.out.println(y);
    }
}
