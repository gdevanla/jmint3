package TestArtifacts.Test8;

public class TestClass8_01 {

    public void F1(){
        TestClass8_03 t7_03 = new TestClass8_03();
        TestClass8_02 t1_02 = new TestClass8_02();
        t1_02.updateObject(t7_03);
        System.out.println("Local object instance t7_03 was tainted by an external call = " + t7_03.x);
    }
}


