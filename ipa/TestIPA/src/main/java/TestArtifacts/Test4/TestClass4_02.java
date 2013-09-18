package TestArtifacts.Test4;

public class TestClass4_02 {

    TestClass4_01 t = new TestClass4_01();

    public void useMemberInstance() {
        int y = 100;
        int z = t.x + y;
        System.out.println("value of z =" + z);
    }
}
