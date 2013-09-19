package MutantInjectionArtifacts.IfStmt;

public class TestClass2_02 {

    public void useLocalVariable(int x) {

        int y = 10000;
        int z = x + y;
        System.out.println("value of z =" + z);
    }
}
