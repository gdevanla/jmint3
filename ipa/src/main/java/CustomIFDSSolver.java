import com.google.common.collect.Table;
import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import soot.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.toolkits.scalar.Pair;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
Based of JimpleIFDSSolver
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

    public void saveResults(){
        for (Object o: val.cellSet()){
            Table.Cell<Unit, D, ?> entry = (Table.Cell<Unit, D, ?>)o;
            Unit unit = entry.getRowKey();
            SootMethod method = (SootMethod)icfg.getMethodOf(unit);

            if (! method.getDeclaringClass().getPackageName().contains("TestArtifact")
                && !method.getDeclaringClass().getPackageName().contains("MutantInjection"))
                continue;

            for(ValueBox b:unit.getUseBoxes()){
                //System.out.println(b.getValue());
                Pair<Value, Set<DefinitionStmt>> columnKey = (Pair<Value, Set<DefinitionStmt>>)entry.getColumnKey();

                Value v = (columnKey.getO1() instanceof EquivalentValue)?
                        ((EquivalentValue)columnKey.getO1()).getDeepestValue():columnKey.getO1();

                //System.out.println(b.getValue() + "," + columnKey.getO1() + "," + columnKey.getO2() + "," + unit);

                if (v.equivTo(b.getValue())){

                    Set<DefinitionStmt> defs = columnKey.getO2();
                    for (DefinitionStmt def:defs){
                        SootMethod calleeMethod = (SootMethod)icfg.getMethodOf(def);

                        Value rightOpValue = def.getRightOp();
                        //System.out.println("LeftOp values =====\n" +  unit + "," + rightOpValue.toString() + "," + rightOpValue.getClass().toString());
                        if (rightOpValue instanceof JInstanceFieldRef){

                            System.out.println(((JInstanceFieldRef)rightOpValue).getField().getDeclaringClass());
                            if ( !( ((JInstanceFieldRef)rightOpValue).getField().getDeclaringClass().equals(method.getDeclaringClass()))){
                                UseDefChain useDefChain = new UseDefChain(method, unit, b.getValue(), calleeMethod, def);
                                useDefChain.printInfo();
                                udChains.add(useDefChain);
                                //useDefChain.printInfo();
                                continue;
                            }
                        }

                        if (calleeMethod.getDeclaringClass().equals(method.getDeclaringClass()))
                            continue;

                        UseDefChain useDefChain = new UseDefChain(method, unit, b.getValue(), calleeMethod, def);
                        useDefChain.printInfo();
                        udChains.add(useDefChain);
                        //System.out.println("Size = " + udChains.size());
                        //System.out.println(b.getValue() + "," + columnKey.getO1() + "," + columnKey.getO2() + "," + unit);

                    }
                }
            }
        }
    }


    public void printFilteredResults(){

        for (Object o: val.cellSet()){
            Table.Cell<Unit, D, ?> entry = (Table.Cell<Unit, D, ?>)o;
            Unit unit = entry.getRowKey();
            SootMethod method = (SootMethod)icfg.getMethodOf(unit);

            if (!method.getDeclaringClass().getPackageName().contains("TestArtifact"))
                continue;

            for(ValueBox b:unit.getUseBoxes()){
                //System.out.println(b.getValue());
                Pair<Value, Set<DefinitionStmt>> columnKey = (Pair<Value, Set<DefinitionStmt>>)entry.getColumnKey();

                Value v = (columnKey.getO1() instanceof EquivalentValue)?
                        ((EquivalentValue)columnKey.getO1()).getDeepestValue():columnKey.getO1();

                if (v.equivTo(b.getValue())){

                   Set<DefinitionStmt> defs = columnKey.getO2();
                   for (DefinitionStmt def:defs){
                       SootMethod calleeMethod = (SootMethod)icfg.getMethodOf(def);
                       if (calleeMethod.getDeclaringClass().equals(method.getDeclaringClass()))
                           continue;

                       UseDefChain useDefChain = new UseDefChain(method, unit, b.getValue(), calleeMethod, def);
                       useDefChain.printInfo();
                       udChains.add(useDefChain);
                       System.out.println("Size = " + udChains.size());
                       System.out.println(b.getValue() + "," + columnKey.getO1() + "," + columnKey.getO2() + "," + unit);

                   }
                }
            }
        }
    }
}