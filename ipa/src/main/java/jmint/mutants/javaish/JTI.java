package jmint.mutants.javaish;

import jmint.*;
import jmint.mutants.MutantsCode;
import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.List;

public class JTI extends BaseMutantInjector {
    final Logger logger = LoggerFactory.getLogger(BaseMutantInjector.class);


    public JTI(UseDefChain udChain) {
        super(udChain);
    }

    public void writeMutant(MutantHeader h){

    }

    public void writeMutantForRightOp(MutantHeader h){
        try{
            Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;

            List<JimpleLocal> locals = gatherUsedLocals(((JAssignStmt)origStmt.getO1()).getRightOp());
            for ( Local l:locals){
                if (!isMatchingInstanceAvailable((JimpleLocal)l, origStmt.getO2())) { continue;}
                JAssignStmt newAssignStmt = new JAssignStmt(l,
                        new JInstanceFieldRef(origStmt.getO2().getActiveBody().getThisLocal(),
                                origStmt.getO2().getDeclaringClass().getFieldByName(l.getName()).makeRef()));
                try{
                    origStmt.getO2().getActiveBody().getUnits().insertBefore(newAssignStmt, origStmt.getO1());
                    MutantGenerator.write(origStmt.getO2().getDeclaringClass(), MutantsCode.JTI);
                }
                finally {
                    origStmt.getO2().getActiveBody().getUnits().remove(newAssignStmt);
                }
            }
        }
        catch (Exception ex)  {
            logger.debug("Mutant Generation-Errors={}:{}", ex.toString(), ex.getStackTrace() );
        }
    }

    public void writeMutantForLeftOp(MutantHeader h){
        Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;


        //local to replace
        JimpleLocal l = (JimpleLocal)((JAssignStmt)origStmt.getO1()).getLeftOp();

        //create temporary local to help assign the right Op
        JimpleLocal tempLocal = new JimpleLocal("$temp_local", l.getType());
        JAssignStmt tempAssignStmt = new JAssignStmt(tempLocal,
                ((JAssignStmt)origStmt.getO1()).getRightOp());
        JAssignStmt newAssignStmt = new JAssignStmt(new JInstanceFieldRef(
                origStmt.getO2().getActiveBody().getThisLocal(),
                origStmt.getO2().getDeclaringClass().getFieldByName(l.getName()).makeRef()),
                tempLocal);

        try{
            origStmt.getO2().getActiveBody().getLocals().add(tempLocal);
            origStmt.getO2().getActiveBody().getUnits().insertBefore(tempAssignStmt, origStmt.getO1());
            origStmt.getO2().getActiveBody().getUnits().swapWith(origStmt.getO1(), newAssignStmt);
            MutantGenerator.write(origStmt.getO2().getDeclaringClass(), MutantsCode.JTI);
        }
        finally {
            origStmt.getO2().getActiveBody().getUnits().swapWith(newAssignStmt, origStmt.getO1());
            origStmt.getO2().getActiveBody().getUnits().remove(tempAssignStmt);
            origStmt.getO2().getActiveBody().getLocals().remove(tempLocal);
        }
    }

    private String getText(List<JimpleLocal> locals, Local leftLocal){
        String template = "List of locals being replaced [%s]";
        StringBuilder localsList = new StringBuilder();
        for (Local l:locals){
            localsList.append(l.getType()).append(",");
        }

        if (leftLocal != null) localsList.append(leftLocal.getName());

        return localsList.toString();
    }


    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent){

        SootMethod method = (SootMethod)parent.getO2();

        if (method.isStatic()) { return null;} //nothing to do here.

        //locals on right of assign stmt
        List<JimpleLocal> locals = filterByReplacebleLocals(
                gatherUsedLocals(stmt.getRightOp()),
                stmt, method);

        // Locals on left of assign stmt
        Local leftLocal = null;
        //check if left op can be replaced.
        if ((stmt.getLeftOp() instanceof JimpleLocal)
                && isMatchingInstanceAvailable((JimpleLocal)stmt.getLeftOp(), method)){
            leftLocal = (JimpleLocal)stmt.getLeftOp();
        }
        // nothing found, just returned
        if (locals.size() == 0 && leftLocal == null) { return null;}

        //we create just one MutantHeader object for all locals. even though
        //the writeMutant..() will create multiple mutant version for each
        //local that can be substituted.
        MutantHeader header = new MutantHeader(udChain,
                parent,
                new Pair<Stmt, SootMethod>((Stmt)stmt, method),
                MutantsCode.JTI,
                //fix string, multiple locals can actually be substituted with this.[local]
                getText(locals, leftLocal));

        if (!allMutants.containsKey(header.getKey())){
            writeMutantForRightOp(header);
            //TODO: needs some more thought!
            //TODO: since replacing y = <exp> with this.y = <exp>. leaves y uninitialized
            //and JVM verifier does not like it.
            //if (leftLocal!=null) writeMutantForLeftOp(header);
            allMutants.put(header.getKey(), header);
        }

        return null;
    }

    private List<JimpleLocal> gatherUsedLocals(Value rightOp) {

        List<JimpleLocal> locals = new ArrayList<JimpleLocal>();

        if (rightOp instanceof  JimpleLocal){
            if (!((JimpleLocal)rightOp).getName().startsWith("$"))
                locals.add((JimpleLocal)rightOp);
        }
        else
        {
            for(ValueBox useBox : rightOp.getUseBoxes()){
                if (useBox.getValue() instanceof  JimpleLocal){
                    if (!((JimpleLocal)useBox.getValue()).getName().startsWith("$"))
                        locals.add((JimpleLocal)useBox.getValue());
                }
                else
                {
                    logger.debug("Unsupported type other than JimpleLocal found in valuebox.= {}", useBox.getValue());
                }

            }
        }

        return locals;

    }

    public List<JimpleLocal> filterByReplacebleLocals(List<JimpleLocal> locals, DefinitionStmt defStmt, SootMethod method){

        List<JimpleLocal> newLocals = new ArrayList<JimpleLocal>();
        for (JimpleLocal l:locals){
            if (SUtil.doesClassHasMember(
                method.getDeclaringClass(),
                l.getName(),
                l.getType()))
                newLocals.add(l);
        }


        return newLocals;

    }



    public boolean isMatchingInstanceAvailable(JimpleLocal l, SootMethod method){

        if (SUtil.doesClassHasMember(
                method.getDeclaringClass(),
                l.getName(),
                l.getType())) return true;


        return false;

    }


}
