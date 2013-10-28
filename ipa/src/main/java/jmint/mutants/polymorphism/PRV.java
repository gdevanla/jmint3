package jmint.mutants.polymorphism;
import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.List;
import java.util.Set;

public class PRV extends BaseMutantInjector {
    public PRV(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        if (!(stmt.getRightOp() instanceof JimpleLocal)) return null; //nothing simple to do here.

        Type t1 = stmt.getRightOp().getType();
        if (! SUtil.isTypeIncludedInAnalysis(t1)) return null;

        //Set<Type> localTypes = SUtil.getAllTypesInLocal(((SootMethod) parent.getO2()).getActiveBody().getLocals(), true);
        Set<Type> localTypes = SUtil.getAllTypesInInstance( SUtil.getResolvedClass(method), true);

        for (Type t2:localTypes){
            if ( t2.equals(t1) ) { //SUtil.doTypesShareParentClass(t1, t2)){ muJava does not do class hierarchy comaparison
                String replacableTypes = String.format("%s to %s since their parent is %s",
                        t1 , SUtil.getResolvedClass(t2.toString()),
                        SUtil.getResolvedClass(t1.toString()).getSuperclass());
                MutantHeader header = new MutantHeader(udChain, parent, parent, MutantsCode.PRV, replacableTypes);
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }
        }

        return null;

    }

}