package TestArtifacts.Test5;

public class TestClass5_01 {

    public int x = 10;

    public void F1(){
        TestClass5_02 t1_02 = new TestClass5_02();
        x = t1_02.getSomeValue();
        System.out.println("Member instance `x` was tainted by an external call");
    }
}


