package TestIPA;

import java.awt.*;

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

    public void testTryCatch(){
        int x = (int)Math.random();
        try {
            int y = (int)Math.random();
            System.out.print(y+x);
        }
        catch(Exception ex){
            System.out.println("Exception");
        }
        finally {
            System.out.println("Finally");
        }
    }

    public void testTryCatch1(){
            int x = (int)Math.random();
            System.out.println("This works.");
    }


    public static void main(String[] s){
        A a = new A();
        //System.out.println("Test");
        //int x  = a.callSomething();
        //System.out.println("Value of x is " + x);
        System.out.println("Calling catch1");
        a.testTryCatch1();
        System.out.println("Calling catch2");
        a.testTryCatch();

    }

}
