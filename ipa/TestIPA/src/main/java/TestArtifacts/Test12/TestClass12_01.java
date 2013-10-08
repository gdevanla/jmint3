package TestArtifacts.Test12;

public class TestClass12_01 {
    int z = 10;
    public void x(){

    }

    public void F1(){
        TestClass12_02 t1_02 = new TestClass12_02();
        Base b = t1_02.getChild();
        F2((Child1)b);
        System.out.println(b.baseMember);
    }

    public Base F2(Base c){
        TestClass12_02 t1_02 = new TestClass12_02();
        Child1 b = (Child1) c;
        t1_02.consumeChild(b);
        System.out.println(b.baseMember);
        return b;
    }
}


