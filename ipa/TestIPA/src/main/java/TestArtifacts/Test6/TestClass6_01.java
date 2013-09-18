package TestArtifacts.Test6;

public class TestClass6_01 {

    public void F1(){
        TestClass6_02 t1_02 = new TestClass6_02();
        int x = t1_02.getSomeValue();
        int y = x + 1010;
        System.out.println("Local instance `x` was tainted by an external call" + y);
    }
}


