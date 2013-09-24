package MutantInjectionArtifacts.RefType;

public class TestClass3_02 {

    int member = 0;

    public int testParameterRef(int x) {
        int y = (int)Math.random();
        return x;

    }

    public void passRefType(TestClass3_01 testClass2_01) {
        int z =(int)Math.random();
        //int x = x;
        //member
    }

}
