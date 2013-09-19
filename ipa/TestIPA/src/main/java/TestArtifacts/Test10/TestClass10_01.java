package TestArtifacts.Test10;

public class TestClass10_01 {

    public void F1(){
        TestClass10_02 t1_02 = new TestClass10_02();
        int y = (int)Math.random();
        if ( y < 100) {
            int z = (int)Math.random();
            t1_02.doSomething(z);
        }
        else
        {
            int w = (int)Math.random();
            t1_02.doSomething(w);
        }
    }
}


