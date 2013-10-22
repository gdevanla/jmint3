package jmint.mutants.overloading;

import jmint.*;
import jmint.mutants.MutantsCode;
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

public class OMD extends BaseMutantInjector {
    public OMD(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        List<MutantInfo> mutants = new ArrayList<MutantInfo>();
        if ( method.getName().matches("get.*")){
            Set<Unit> units = SUtil.findUnitInvokingOverloadedMethods(udChain, method);

            //generating mutants
            //TODO: Generate the actual mutants
            for(Unit u:units){
                MutantHeader header = new MutantHeader(udChain, parent,
                        new Pair<Stmt, Host>((Stmt)u, udChain.getUseMethod()), MutantsCode.OMD);
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }

        }
        return null;
    }

}