package MutantInjectionArtifacts.IHI;

class IHIParent1 {
    public int x;
}

class A{
   protected int x; //IHD

}

class B extends A {

}

class IHDUnRelatedClass{
    public int y;
}

public class IHI1 extends IHIParent1 {


    //private int y = 100;

    public void F1(){
        IHDUnRelatedClass c = new IHDUnRelatedClass();
        int y = c.y;
        int z = new B().x;
        IHI2 t1_02 = new IHI2();
        t1_02.useLocalVariable(this.x+z);
    }

}


