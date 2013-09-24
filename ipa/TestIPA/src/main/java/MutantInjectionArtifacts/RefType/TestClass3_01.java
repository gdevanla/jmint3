package MutantInjectionArtifacts.RefType;

public class TestClass3_01 {

    public int x;

    public void F1(){

        TestClass3_02 t1_02 = new TestClass3_02();
        t1_02.passRefType(this);
        //int z = t1_02.testParameterRef(1010);
        //System.out.print(z);
    }


}


