package MutantInjectionArtifacts.IfStmt;

public class TestClass2_01 {
    public void F1(){
        int w = (int)Math.random();
        int x = 0;
        if (w > 100){
            x = 1000000 + w;
        }
        else
        {
            x = 20000 * w;
        }

        TestClass2_02 t1_02 = new TestClass2_02();
        t1_02.useLocalVariable(x);
    }


}


