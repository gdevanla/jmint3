package MutantInjectionArtifacts.JID;

public class JID2 {

    public int instancevar;
    public static int staticVar;

    public int getSomeInstanceVar(){
        int zz = this.instancevar;
        return zz;

    }

    public int getStaticVar() {
        return JID2.staticVar;
    }
}
