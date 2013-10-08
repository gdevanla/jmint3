package MutantInjectionArtifacts.JID;

/*
   Test case when member variable in one class
   is used in external class
 */
public class JIDTest1 {

    public static void main(String[] args){
        JID1 t = new JID1();
        t.F1();
        //t.F2();
    }
}
