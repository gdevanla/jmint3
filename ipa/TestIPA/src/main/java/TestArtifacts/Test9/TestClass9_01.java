package TestArtifacts.Test9;

public class TestClass9_01 {

    public int x = 10;

    public void F1(){
        TestClass9_02 t1_02 = new TestClass9_02();
        x = t1_02.member_variable;
        System.out.println("Member instance `x` was tainted by an external member variable");
    }
}


