package TestArtifacts.Test7;

public class TestClass7_01 {

    TestClass7_03 t7_03 = new TestClass7_03();

    public void F1(){
        TestClass7_02 t1_02 = new TestClass7_02();
        t1_02.updateObject(t7_03);
        System.out.println("Member object instance t7_03 was tainted by an external call = " + t7_03.x);
    }
}


