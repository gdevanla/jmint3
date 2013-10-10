package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.SootUtilities;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.grimp.NewInvokeExpr;
import soot.jimple.*;
import soot.jimple.internal.InvokeExprBox;
import soot.jimple.internal.JInstanceFieldRef;
import soot.toolkits.scalar.Pair;

import java.util.List;

public class JDC extends BaseMutantInjector {
    public JDC(UseDefChain udChain) {
        super(udChain);
    }

    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);

        for(Pair<DefinitionStmt, SootMethod> o :udChain.getAllDefStmts()){

            DefinitionStmt defStmt = o.getO1();
            if (!(defStmt instanceof AssignStmt))
                continue; //for jdc we are not interested in any other statements

            //we are interested in statements like these
            // $r = new someclass
            if (!(defStmt.getRightOp() instanceof NewExpr)) continue;

            if ( !SootUtilities.isTypeIncludedInAnalysis(
                    defStmt.getDefBoxes().get(0).getValue())) continue;

            //now go find the constructor being invoked and see if it is default constructor.
            Unit u = SootUtilities.getUnitInvokingDefaultConstructor(
                    defStmt.getDefBoxes().get(0).getValue(),
                    o.getO2());
            if (u!=null) System.out.println(u);
            if (u != null) return true;
        }
        //did not find any special invoke.
        return false;
    }

    public String getLineNumber(){
        assert(canInject());

        return "-1";
    }

    public String getMutantString(){
        return "";
    }

    @Override
    public String mutantLog(){
        String mutantLog = "%s:%s_%s:%s:%s";
        return String.format(mutantLog, udChain.getDefMethod().getDeclaringClass().getName(),
                MutantsCode.JDC, 0, getLineNumber(), getMutantString());
    }




}