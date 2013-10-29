package jmint.mutants.overloading;
import jmint.*;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OMR extends BaseMutantInjector {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public OMR(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        //we only want OMD on def stmt classes
        if ( ! method.getDeclaringClass().equals(udChain.defMethod.getDeclaringClass())){ return null;}

        List<MutantInfo> mutants = new ArrayList<MutantInfo>();
        if (SUtil.areMethodsAvailableFor(method, false)){

                //this mutant will be replacing the body with one call to alternate method. We represent
                // this with getUnits().getFirst()
                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        new Pair<Stmt, Host>((Stmt)method.getActiveBody().getUnits().getFirst(), method),
                        MutantsCode.OMR

                );
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }
        return null;
    }
}
