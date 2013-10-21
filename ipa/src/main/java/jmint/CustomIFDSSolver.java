package jmint;

import com.google.common.collect.Table;
import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import soot.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.toolkits.scalar.Pair;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;

import java.util.*;

/*
Based on JimpleIFDSSolver
*/

public class CustomIFDSSolver<D,  I extends InterproceduralCFG<Unit, SootMethod>> extends JimpleIFDSSolver {

    public List<UseDefChain> udChains = new ArrayList<UseDefChain>();

    public CustomIFDSSolver(IFDSTabulationProblem<Unit, D, SootMethod, I> problem) {
        this(problem,false);
    }

    public CustomIFDSSolver(IFDSTabulationProblem<Unit,D,SootMethod,I> problem, boolean dumpResults) {
        super(problem, true);
    }

    @Override
    public void solve() {
        super.solve();
        saveResults();
    }

    public boolean isCrossBoundaryDefUse(DefinitionStmt defStmt, SootMethod useMethod){
        SootMethod defMethod = (SootMethod)icfg.getMethodOf(defStmt);
        Value rightOpValue = defStmt.getRightOp();
        //System.out.println("LeftOp values =====\n" +  unit + "," + rightOpValue.toString() + "," + rightOpValue.getClass().toString());
        if (defStmt.getRightOp() instanceof JInstanceFieldRef
            && !(((JInstanceFieldRef)rightOpValue).getField().getDeclaringClass().equals(useMethod.getDeclaringClass())))
        {
            return true;
        }
        else if (!(defStmt.getRightOp() instanceof JInstanceFieldRef)
        && !(defMethod.getDeclaringClass().equals(useMethod.getDeclaringClass()))){
            return true;
        }

        return false;

    }

    public void saveResults(){
        for (Object o: val.cellSet()){

            Table.Cell<Unit, D, ?> entry = (Table.Cell<Unit, D, ?>)o;
            Unit unit = entry.getRowKey();
            Pair<Value, Set<DefinitionStmt>> columnKey = (Pair<Value, Set<DefinitionStmt>>)entry.getColumnKey();
            Set<DefinitionStmt> defs = columnKey.getO2();

            //Set<Pair<DefinitionStmt, SootMethod>> allReachingDefs = getDefStmtMethodPairs(defs);

            SootMethod method = (SootMethod)icfg.getMethodOf(unit);
            if (! method.getDeclaringClass().getPackageName().contains("TestArtifact")
                    && !method.getDeclaringClass().getPackageName().contains("MutantInjection")
                    && !method.getDeclaringClass().getPackageName().contains("org.apache.bcel")
                    )
                continue;

            for(ValueBox b:unit.getUseBoxes()){
                //System.out.println("Use Boxes = " +  b.getValue() + ":" + unit);

                Value v = (columnKey.getO1() instanceof EquivalentValue)?
                        ((EquivalentValue)columnKey.getO1()).getDeepestValue():columnKey.getO1();

                //System.out.println(b.getValue() + "," + columnKey.getO1() + "," + columnKey.getO2() + "," + unit);

                if (v.equivTo(b.getValue())){

                    for (DefinitionStmt def:defs){
                        SootMethod defMethod = (SootMethod)icfg.getMethodOf(def);
                        if ( isCrossBoundaryDefUse(def,method) ){

                            UseDefChain useDefChain = new UseDefChain(method, unit, b.getValue(),
                                    defMethod,def, getDefStmtMethodPairs(def));
                            //System.out.println("Line Number Use Unit=" + unit.getTag("LineNumberTag") +
                            //        "Line Number of DefStmt=" + def.getTag("LineNumberTag"));
                            //useDefChain.printInfo();
                            udChains.add(useDefChain);

                        }
                        //System.out.println("Size = " + udChains.size());
                        //System.out.println(b.getValue() + "," + columnKey.getO1() + "," + columnKey.getO2() + "," + unit);

                    }
                }
            }
        }
    }

    private void printReachingDefs(DefinitionStmt def) {
        System.out.println("This is the def=" + def);
        Map cols = val.row(def);
        for(Object o:cols.keySet()){
            System.out.println((Pair<Value,DefinitionStmt>)o);
        }
    }

    private Set getDefStmtMethodPairs(DefinitionStmt def) {
        Map cols = val.row(def);
        Set<Pair<Pair<Value, Set<DefinitionStmt>>, SootMethod>> l =
                new HashSet<Pair<Pair<Value, Set<DefinitionStmt>>, SootMethod>>();

        for(Object o:cols.keySet()){
            assert(((Pair<Value, Set<DefinitionStmt>>)o).getO2().size() <= 1);
            if (((Pair<Value, Set<DefinitionStmt>>)o).getO2().size() == 1){
                l.add(new Pair<Pair<Value, Set<DefinitionStmt>>, SootMethod>((Pair<Value, Set<DefinitionStmt>>) o,
                        (SootMethod)icfg.getMethodOf(((Pair<Value, Set<DefinitionStmt>>) o).getO2().iterator().next())));
            }
        }

        return l;
    }

}