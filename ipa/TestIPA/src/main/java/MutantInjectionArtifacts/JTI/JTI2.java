package MutantInjectionArtifacts.JTI;

public class JTI2 {

    public void useLocalVariable(int x) {
        int y = 10;
        int z = x + y;
        System.out.println("value of z =" + z);
    }
}
