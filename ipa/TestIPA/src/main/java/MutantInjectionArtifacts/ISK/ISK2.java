package MutantInjectionArtifacts.ISK;

class Base {
   public int basex = (int)Math.random();
}

public class ISK2 extends Base {

    public int basex = (int)Math.random();

    public int getSomeInstanceVar(){
        int zz = super.basex;
        return zz;
    }

}
