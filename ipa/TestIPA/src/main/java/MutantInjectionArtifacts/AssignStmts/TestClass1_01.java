package MutantInjectionArtifacts.AssignStmts;

public class TestClass1_01 {

    int m = 230;
    static int yy = 1210;
    int z;
    int samevariable = 10;

    public TestClass1_01(){
          z = 100;
    }

    public TestClass1_01(int k){
        this.z = k;
    }

    public void F1(){

        int samevariable = 1001;
        this.samevariable = 100;

        yy = 100;
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


