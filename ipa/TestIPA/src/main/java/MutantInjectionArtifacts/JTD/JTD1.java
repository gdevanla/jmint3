package MutantInjectionArtifacts.JTD;


public class JTD1 {

    int x = (int)Math.random();
    private int y = 100;

    public void F1(int x){
        x = (int)Math.random();
        JTD2 t1_02 = new JTD2();
        t1_02.useLocalVariable(this.x);
    }
}


