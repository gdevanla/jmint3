package jmint.mutants.progmistakes;

import jmint.*;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.tagkit.Host;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.SimpleLocalDefs;

import java.util.*;


/* For a given def-use chain see if the definition is in a method different from current method,
that is in the form of an getter */

public class EAM extends BaseMutantInjector {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public EAM(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

     SootMethod method = (SootMethod)parent.getO2();

     if (method.isStatic()) {return null;}
     if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
         return null;
     }

      List<MutantInfo> mutants = new ArrayList<MutantInfo>();
      if ( method.getName().matches("get.*")){
          Set<Unit> units = findStatementsInvokingGetter(method);

          //generating mutants

          //TODO: Generate the actual mutants
          for(Unit u:units){
              MutantHeader header = new MutantHeader(udChain, parent, new Pair<Stmt, Host>((Stmt)u, udChain.getUseMethod()), MutantsCode.EAM);
              if (!allMutants.containsKey(header.getKey())){
                  generateMutant(header);
                  allMutants.put(header.getKey(), header);
              }
          }

      }
      return null;
    }


    public void generateMutant(MutantHeader h){
        Pair<Stmt,SootMethod> origStmt = (Pair<Stmt, SootMethod>)h.originalDefStmt;
        if (origStmt.getO2().isStatic()) { return;}

        List<SootMethod> availMethods = SUtil.getAlternateGetterMethods(udChain.getDefMethod().getDeclaringClass(), udChain.getDefMethod());

        SootClass klass = SUtil.getResolvedClass(udChain.getUseMethod().getDeclaringClass().getName());

        PatchingChain<Unit> units = udChain.getUseMethod().getActiveBody().getUnits();

        if (!(h.originalDefStmt.getO1() instanceof JAssignStmt)){
            logger.warn("Not Supported : {} not instance of JAssignStmt" , h.originalDefStmt.getO1().toString());
        }

        JAssignStmt stmt = (JAssignStmt)h.originalDefStmt.getO1();

        InvokeExpr originalExpr = stmt.getInvokeExpr();

        for (SootMethod m: availMethods){

            InvokeExpr newExpr = null;

            if (originalExpr instanceof VirtualInvokeExpr){
                newExpr = new JVirtualInvokeExpr(((VirtualInvokeExpr) originalExpr).getBase(),
                        m.makeRef(), new ArrayList());
            }
            else if (originalExpr instanceof InterfaceInvokeExpr){
                new JInterfaceInvokeExpr(((InterfaceInvokeExpr) originalExpr).getBase(), m.makeRef(),
                        new ArrayList());
            }
            else
            {
                continue;
            }

            assert(newExpr != null);
            JAssignStmt newStmt = new JAssignStmt(stmt.getLeftOp(), newExpr);
            units.swapWith(stmt, newStmt);
            MutantGenerator.writeJimple(klass, MutantsCode.EAM);
            MutantGenerator.writeClass(klass, MutantsCode.EAM);
            units.swapWith(newStmt, stmt);

        }

    }


    public Set<Unit> findStatementsInvokingGetter(SootMethod getterMethod){
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

        SimpleLocalDefs localDefs = new SimpleLocalDefs(
                new ExceptionalUnitGraph(SUtil.getResolvedMethod(
                        udChain.getUseMethod()).getActiveBody()));

        //Soot JimpleLocal.equals method is not implemented, and I got grief with this.
        //Plus LocalDefs does not work well with Units since Units does not implement hashCode.
        for (Unit u:units){
            // I hope this is not bad. Unit.equals Soot is mysteriously missing
            if ( SUtil.AreTheseObjectEqualAsStrings(u, udChain.useUnit)
                && SUtil.DoesUnitUseThisLocalAsString(u, l)){
                Local equivLocal = SUtil.getEquivalentLocal(u, l);
                if ( localDefs.hasDefsAt(l, u)){
                    for (Unit def:localDefs.getDefsOfAt(equivLocal, u)){
                        if (def instanceof JAssignStmt &&

                                SUtil.isThisMethodInvoked((JAssignStmt) def, getterMethod) &&
                                SUtil.areOtherGetterMethodsAvailable(getterMethod.getDeclaringClass(), getterMethod)) {

                            if (! mutableUnits.contains(def)){
                                mutableUnits.add(def);
                            }
                            else
                            {
                                //logger.debug(def);
                            }
                        }
                    }
                }
            }
        }
        return mutableUnits;

    }

  /*  public Unit findStatementInvokingGetter(SootMethod getterMethod){
        //TODO: Fingers crossed to see if this dumb version of callgraph is sufficient
        CallGraphBuilder callGraphBuilder = new CallGraphBuilder(DumbPointerAnalysis.v());
        callGraphBuilder.build();
        CallGraph callGraph = callGraphBuilder.getCallGraph();

        ExceptionalUnitGraph expUnitGraph = new ExceptionalUnitGraph(
                SUtil.getResolvedMethod(udChain.getUseMethod()).getActiveBody());



        Iterator<Edge> edgeList = callGraph.edgesOutOf(SUtil.getResolvedMethod(udChain.getUseMethod()));

        while(edgeList.hasNext()){
            Edge  e = edgeList.next();
            if (e.tgt().equivHashCode() == getterMethod.equivHashCode() &&
                    ){
                logger.debug("Returning unit=" +  e.srcUnit() + " calling getter " + getterMethod);
                return e.srcUnit();

            }
        }
        return null;
    }*/



    public boolean canInject(){
      ;
        String methodName = udChain.getDefMethod().getName();

        //TODO: Fix some more details.
        // check for no arguments,
        // check for available methods to replace
        // also check for if the method is the callee, We could get away with this
        // since getXXX may usually be called methods!
        if ( methodName.matches("get.*")){
            return true;
        }
        else {
            return false;
        }
    }


    public String getMutantString(){
        return "";
    }

    @Override
    public String mutantLog(){
        String mutantLog = "%s:%s_%s:%s:%s";
        return String.format(mutantLog, udChain.getDefMethod().getDeclaringClass().getName(),
                MutantsCode.EAM, 0, udChain.getDefStmt().getTag("LineNumberTag"), getMutantString());
    }









}