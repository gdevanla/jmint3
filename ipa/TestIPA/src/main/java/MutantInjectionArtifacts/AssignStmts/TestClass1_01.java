package MutantInjectionArtifacts.AssignStmts;

public class TestClass1_01 {

    int m = 0;

    public void F1(){
        int w = (int)Math.random();
        int x = 1000000 + w;
        TestClass1_02 t1_02 = new TestClass1_02();
        t1_02.useLocalVariable(x);
    }

    public void F2(){
        m = (int)Math.random();
        int x = this.m;
        TestClass1_02 t1_02 = new TestClass1_02();
        t1_02.useLocalVariable(x);
    }


}


