package MutantInjectionArtifacts.IPC;

/*
   Test case when member variable in one class
   is used in external class
 */
public class IPCTest1 {

    public static void main(String[] args){
        IPC1 t = new IPC1();
        t.F1();
        //t.F2();
    }
}
