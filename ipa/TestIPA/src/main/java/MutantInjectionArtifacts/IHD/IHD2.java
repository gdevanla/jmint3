package MutantInjectionArtifacts.IHD;

public class IHD2 {

    public void useLocalVariable(int x) {
        int y = 10;
        int z = x + y;
        System.out.println("value of z =" + z);
    }
}
