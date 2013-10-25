package jmint.mutants.progmistakes;
import jmint.*;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.SimpleLocalDefs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EMM extends BaseMutantInjector {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public EMM(UseDefChain udChain) {
        super(udChain);
    }

    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        List<MutantInfo> mutants = new ArrayList<MutantInfo>();
        if ( method.getName().matches("set.*")){
            Set<Unit> units = findStatementsInvokingSetter(method);

            //generating mutants

            //TODO: Generate the actual mutants
            for(Unit u:units){
                MutantHeader header = new MutantHeader(udChain, parent,
                        new Pair<Stmt, Host>((Stmt)u, udChain.getUseMethod()),
                        MutantsCode.EMM);
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }

        }
        return null;
    }

    public Set<Unit> findStatementsInvokingSetter(SootMethod setterMethod){
        Set<Unit> mutableUnits = new HashSet<Unit>();
        //assert(udChain.useValue instanceof Local);
        if (!(udChain.useValue instanceof Local)){
            logger.debug("useValue other than Local found=" + udChain.useUnit + ":" + udChain.useValue.getClass());
            return mutableUnits;
        }

        Local l = (Local) udChain.useValue;
        PatchingChain<Unit> units = SUtil.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        PatchingChain<Unit> units2 = SUtil.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        Unit predUnit = units.getPredOf(udChain.useUnit);
        while (predUnit != null){
            if (!(predUnit instanceof Stmt)){
                predUnit = units.getPredOf(predUnit);
                continue;
            }

            if ( ((Stmt) predUnit).containsInvokeExpr()
                    && ((Stmt)predUnit).getInvokeExpr().getMethod().getSubSignature().equals(setterMethod.getSubSignature())
                    &&  SUtil.areOtherSetterMethodsAvailable(((Stmt)predUnit).getInvokeExpr().getMethod().getDeclaringClass(),
                    setterMethod))
                mutableUnits.add(predUnit);

            try{
                predUnit = units.getPredOf(predUnit);
            }
            catch(Exception ex){
                break; //we did not find any preds
            }
        }

    return mutableUnits;

    }

}