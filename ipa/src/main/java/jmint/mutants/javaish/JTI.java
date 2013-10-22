package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import org.junit.Assume;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

public class JTI extends BaseMutantInjector {

    public JTI(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent){

        SootMethod method = (SootMethod)parent.getO2();

        if (!(stmt.getRightOp() instanceof JimpleLocal )) return null;

        Unit u = injectableUnit((JimpleLocal)stmt.getRightOp(), stmt, method);
        if (u != null)
        {
            MutantHeader header = new MutantHeader(udChain,
                    parent,
                    parent,
                    MutantsCode.JTD,
                    String.format("this.%s will replace this %s", ((JimpleLocal) stmt.getRightOp()).getName(),
                            ((JimpleLocal) stmt.getRightOp()).getName()));

            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }
        }

        u = injectableUnit((JimpleLocal)stmt.getLeftOp(), stmt, method);
        if (u != null){
            if (u != null)
            {
                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        parent,
                        MutantsCode.JTD,
                        String.format("this.%s will replace this %s", ((JimpleLocal) stmt.getRightOp()).getName(),
                                ((JimpleLocal) stmt.getRightOp()).getName()));

                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }
        }


        return null;
    }



    public Unit injectableUnit(JimpleLocal l, DefinitionStmt defStmt, SootMethod method){
        assert(defStmt instanceof AssignStmt);

        if (SUtil.doesClassHasMember(
                method.getDeclaringClass(),
                l.getName(),
                l.getType())) return defStmt;


        return null;

    }


}
