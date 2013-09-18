package TestArtifacts.Test10;

public class TestClass10_01 {

    public void F1(){
        TestClass10_02 t1_02 = new TestClass10_02();
        int x = t1_02.member_variable;
        System.out.println("Local instance `x` was tainted by an external member variable" + x);
    }
}


