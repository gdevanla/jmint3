package jmint.mutants.polymorphism;

import jmint.*;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PPD extends BaseMutantInjector {
    public PPD(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        Set<Unit> units = SUtil.findAssignStmtsWithInvoke(udChain, (SootMethod) parent.getO2());

        //TODO: Generate the actual mutants
        for(Unit u:units){
            Set<Pair<Type, Type>> params = SUtil.getGeneralizableParameters(((AssignStmt) u).getInvokeExpr());
            StringBuffer info = new StringBuffer();
            for (Pair<Type, Type> param:params){
              info = info.append(param);
            }

            MutantHeader header = new MutantHeader(udChain, parent,
                    new Pair<Stmt, Host>((Stmt)u, udChain.getUseMethod()),
                    MutantsCode.PPD, info.toString());
            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }
        }

        return null;
    }



}