package MutantInjectionArtifacts.JSC;

public class JSC2 {

    public int instancevar;
    public static int staticVar;

    public int getSomeInstanceVar(){

        int zz = this.instancevar;

        return zz;

    }

    public int getStaticVar() {
        return JSC2.staticVar;
    }
}
