package MutantInjectionArtifacts.JDC;

public class JDC2 {

    public int getSomeValue() {
        return 100;
    }

    public void updateObject(JDC3 t7_03) {
        int y = (int)Math.random();
        t7_03.x = y;
    }
}
