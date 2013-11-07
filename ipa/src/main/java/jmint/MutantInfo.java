package jmint;

import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.lang.reflect.Field;

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
