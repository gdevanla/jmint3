package jmint;

import com.google.common.collect.Table;
import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.toolkits.scalar.Pair;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import sun.jvm.hotspot.oops.Klass;

import java.util.*;

/*
Based on JimpleIFDSSolver
*/

public class CustomIFDSSolver<D,  I extends InterproceduralCFG<Unit, SootMethod>> extends JimpleIFDSSolver {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

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
        if (! SUtil.isClassIncludedInAnalysis(defMethod.getDeclaringClass())){
            return false;
        }

        Value rightOpValue = defStmt.getRightOp();
        //logger.debug("LeftOp values =====\n" +  unit + "," + rightOpValue.toString() + "," + rightOpValue.getClass().toString());
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
            if (!SUtil.isClassIncludedInAnalysis(method.getDeclaringClass()))
                continue;

/*
            if (! method.getDeclaringClass().getPackageName().contains("TestArtifact")
                    && !method.getDeclaringClass().getPackageName().contains("MutantInjection")
                    //&& !method.getDeclaringClass().getPackageName().contains("org.apache.bcel")
                    //&& !method.getDeclaringClass().getPackageName().contains("org.apache.tools.ant")
                    //&& !method.getDeclaringClass().getPackageName().contains("org.jfree")
                      &&  !method.getDeclaringClass().getPackageName().contains("com.google.test")
                    )
                continue;
*/

            for(ValueBox b:unit.getUseBoxes()){
                //logger.debug("Use Boxes = " +  b.getValue() + ":" + unit);

                Value v = (columnKey.getO1() instanceof EquivalentValue)?
                        ((EquivalentValue)columnKey.getO1()).getDeepestValue():columnKey.getO1();

                //logger.debug(b.getValue() + "," + columnKey.getO1() + "," + columnKey.getO2() + "," + unit);

                if (v.equivTo(b.getValue())){

                    for (DefinitionStmt def:defs){
                        SootMethod defMethod = (SootMethod)icfg.getMethodOf(def);
                        if ( isCrossBoundaryDefUse(def,method) ){

                            UseDefChain useDefChain = new UseDefChain(method, unit, b.getValue(),
                                    defMethod,def, getDefStmtMethodPairs(def));
                            //logger.debug("Line Number Use Unit=" + unit.getTag("LineNumberTag") +
                            //        "Line Number of DefStmt=" + def.getTag("LineNumberTag"));
                            //useDefChain.printInfo();
                            udChains.add(useDefChain);

                        }
                        //logger.debug("Size = " + udChains.size());
                        //logger.debug(b.getValue() + "," + columnKey.getO1() + "," + columnKey.getO2() + "," + unit);

                    }
                }
            }
        }
    }

    private void printReachingDefs(DefinitionStmt def) {
        logger.debug("This is the def=" + def);
        Map cols = val.row(def);
        for(Object o:cols.keySet()){
            logger.debug("{}", (Pair<Value, DefinitionStmt>) o);
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


    //print unique set of ud-pairs by class
    //print unique set of ud-pairs by use-statement-method
    //print unique set of ud-pairs by def-statement-method

    public void printAllClassPairs(){
        HashMap<Pair<SootClass, SootClass>, Integer> class_pairs = new HashMap<Pair<SootClass, SootClass>, Integer>();
        HashMap<SootClass, Integer > useClasses = new HashMap<SootClass, Integer>();
        HashMap<SootClass, Integer > defClasses = new HashMap<SootClass, Integer>();


        for (UseDefChain ud:udChains){
            SootClass klassDef = ud.getDefMethod().getDeclaringClass();
            SootClass klassUse = ud.getUseMethod().getDeclaringClass();
            Pair<SootClass, SootClass> p = new Pair<SootClass, SootClass>(klassUse, klassDef);
            if (class_pairs.containsKey(p))
            {
                class_pairs.put(new Pair<SootClass, SootClass>(klassUse, klassDef),
                        class_pairs.get(p)+1);
            }
            else
            {
                class_pairs.put(new Pair<SootClass, SootClass>(klassUse, klassDef), 1);
            }

            if (useClasses.containsKey(klassUse)){
                useClasses.put(klassUse, useClasses.get(klassUse)+1);
            }
            else
            {
                useClasses.put(klassUse, 1);
            }

            if (defClasses.containsKey(klassDef)){
                defClasses.put(klassDef, defClasses.get(klassDef)+1);
            }
            else
            {
                defClasses.put(klassDef, 1);
            }
        }

        String template = "ClassMetrics: {} = {}";
        logger.debug(template, "Unique pairs", class_pairs.size());
        logger.debug(template, "Use Classes", useClasses.size());
        logger.debug(template, "Def Classes", defClasses.size());

        logger.debug(template, "UseClasses", "UseClasses");
        for (SootClass c:useClasses.keySet()){
            logger.debug(template, c.toString(), useClasses.get(c));
        }

        logger.debug(template, "DefClasses", "DefClasses");
        for (SootClass c:defClasses.keySet()){
            logger.debug(template, c.toString(), defClasses.get(c));
        }

        logger.debug(template, "ClassPairs", "ClassPairs");
        for (Pair p:class_pairs.keySet()){
            logger.debug(template, p.toString(), class_pairs.get(p));
        }


    }
}

