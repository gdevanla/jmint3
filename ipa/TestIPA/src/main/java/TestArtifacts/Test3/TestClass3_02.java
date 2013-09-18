package TestArtifacts.Test3;

public class TestClass3_02 {

    TestClass3_01 t = new TestClass3_01();

    public void useMemberInstance() {
        int y = 100;
        int z = t.x + y;
        System.out.println("value of z =" + z);
    }
}
