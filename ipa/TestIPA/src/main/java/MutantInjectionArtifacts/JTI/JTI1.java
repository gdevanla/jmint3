package MutantInjectionArtifacts.JTI;


public class JTI1 {

    int x = 10;
    private int y = 100;

    public void F1(){
        int x = (int)Math.random() ;
        int z = x;

        JTI2 t1_02 = new JTI2();
        t1_02.useLocalVariable(z);
    }
}


