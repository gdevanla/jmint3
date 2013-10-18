package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class JDC extends BaseMutantInjector {
    public JDC(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<Stmt, Host> parent) {

        if ( !SUtil.isTypeIncludedInAnalysis(
                expr.getBaseType())) return null;

        Unit u = SUtil.getUnitInvokingDefaultConstructor(
                parent.getO1().getDefBoxes().get(0).getValue(),
                (SootMethod) parent.getO2());

        if ( u != null ){
            //TODO: Right now store the SpecialInvoke in DefStmt. Later this can be
            //replaced with more generic reference to the constructor.
            MutantHeader header = new MutantHeader(udChain, parent, new Pair<Stmt, Host>((InvokeStmt)u, udChain.getUseMethod()), MutantsCode.JDC);
            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }
        }

        return null;
    }


    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);

        for(Pair<Stmt, Host> o :udChain.getAllDefStmts()){

            DefinitionStmt defStmt = (DefinitionStmt)o.getO1();
            if (!(defStmt instanceof AssignStmt))
                continue; //for jdc we are not interested in any other statements

            //we are interested in statements like these
            // $r = new someclass
            if (!(defStmt.getRightOp() instanceof NewExpr)) continue;

            //missing

            //now go find the constructor being invoked and see if it is default constructor.
            Unit u = SUtil.getUnitInvokingDefaultConstructor(
                    defStmt.getDefBoxes().get(0).getValue(),
                    (SootMethod) o.getO2());
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