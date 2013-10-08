package MutantInjectionArtifacts.JSC;

/*
   Test case when member variable in one class
   is used in external class
 */
public class JSCTest1 {

    public static void main(String[] args){
        JSC1 t = new JSC1();
        t.F1();
        t.F2();
    }
}
