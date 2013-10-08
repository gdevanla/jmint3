package MutantInjectionArtifacts.JTD;


public class JTD1 {

    int x = 10;
    private int y = 100;

    public void F1(){
        int x = (int)Math.random();
        JTD2 t1_02 = new JTD2();
        t1_02.useLocalVariable(this.x);
    }
}


