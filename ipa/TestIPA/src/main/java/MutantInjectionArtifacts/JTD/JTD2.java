package MutantInjectionArtifacts.JTD;

public class JTD2 {

    public void useLocalVariable(int x) {
        int y = 10;
        int z = x + y;
        System.out.println("value of z =" + z);
    }
}
