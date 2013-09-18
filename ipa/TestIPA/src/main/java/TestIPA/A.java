package TestIPA;

public class A {

    public int result;

    public int neverCalled(int y){
        int z = 10;
        return z + 20;
    }

    public int doSomething(int x, int y){
        return x*y;
    }

    public int callSomething(){
        int param1 = 10;
        int param2 = 20;
        result = doSomething(param1, param2);
        int z = 10 + result;
        return result;
    }


    public static void main(String[] s){
        A a = new A();
        int x  = a.callSomething();
        System.out.println("Value of x is " + x);

    }

}
