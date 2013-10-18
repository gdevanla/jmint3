package jmint;

import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.lang.reflect.Field;


/*class MutantCandidate<T,V> {
    final Pair<T,V> value;

    public MutantCandidate(Pair<T,V> v){
        this.value = v;
    }

} */

/*class StmtMutantCandidate {
    Pair<Stmt, SootMethod> value;
}

class FieldMutantCandidate {
    Pair<Field, SootClass> value;
}

class MethodMutantCandidate {
    Pair<SootMethod, SootClass> value;
}*/




/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 10/11/13
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class MutantInfo {

    public final Pair<DefinitionStmt, Host> mutatedStmt;
    public final SootClass mutatedClass;
    public final MutantsCode mutantCode;

    public MutantInfo(Pair<DefinitionStmt, Host> mutatedStmt,
                          SootClass mutatedClass,
                          MutantsCode mutantCode){

        this.mutatedStmt = mutatedStmt;
        this.mutatedClass = mutatedClass;
        this.mutantCode = mutantCode;
    }


}
