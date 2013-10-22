package MutantInjectionArtifacts.PPD;


import soot.jimple.GroupIntPair;

class Base {
    public int x;

    public int getFromChild(Child child) {
            return (int)Math.random();
    }
}

public class PPD3 extends Base {


    public PPD3(){

    }

    public PPD3(int y){
        x = y;
    }


}

