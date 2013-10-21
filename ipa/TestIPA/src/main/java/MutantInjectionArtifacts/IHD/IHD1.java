package MutantInjectionArtifacts.IHD;

class IHDParent1{
    public int x;
}

class IHDUnRelatedClass{
    public int y;
}

class A{
   protected int x; //IHD
}

class B extends A{
    int x;
}

public class IHD1 extends IHDParent1 {

    public int x = 10;
    //private int y = 100;

    public void F1(){
        IHDUnRelatedClass c = new IHDUnRelatedClass();
        int y = c.y;
        int z = new B().x;
        IHD2 t1_02 = new IHD2();
        t1_02.useLocalVariable(this.x+z);
    }



}


