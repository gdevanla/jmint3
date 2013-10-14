package jmint.mutants.progmistakes;

import jmint.*;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.spark.ondemand.pautil.SootUtil;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Units;
import soot.jimple.toolkits.pointer.DumbPointerAnalysis;
import soot.tagkit.Host;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.util.Chain;

import java.util.*;


/* For a given def-use chain see if the definition is in a method different from current method,
that is in the form of an getter */

public class EAM extends BaseMutantInjector {
    public EAM(UseDefChain udChain) {
        super(udChain);
    }

    private boolean keyPresent(Unit u) {
        for (MutantHeader header:generatedMutants.rowKeySet()){
            if (header.originalDefStmt.getO1().toString().equals(u.toString())
                    && header.originalDefStmt.getO2().toString().equals(udChain.getUseMethod().toString())){

                System.out.println(generatedMutants.containsRow(new Pair<DefinitionStmt, Host>((DefinitionStmt)u, udChain.getUseMethod())));
                System.out.println(header.originalDefStmt.getO1().equals(u));
                return true;
            }
        }

        return false;
    }


    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<DefinitionStmt, SootMethod> parent) {

      List<MutantInfo> mutants = new ArrayList<MutantInfo>();
      if ( parent.getO2().getName().matches("get.*")){
          Set<Unit> units = findStatementsInvokingGetter(parent.getO2());

          //TODO: Generate the actual mutants
          for(Unit u:units){
              MutantHeader header = new MutantHeader(udChain, new Pair<DefinitionStmt, Host>((DefinitionStmt)u, udChain.getUseMethod()));

              if (!keyPresent(u)){
                  generatedMutants.put(header, MutantsCode.EAM, mutants);
              }



          }

      }
      return null;
    }

    public Set<Unit> findStatementsInvokingGetter(SootMethod getterMethod){
        Set<Unit> mutableUnits = new HashSet<Unit>();
        //assert(udChain.useValue instanceof Local);
        if (!(udChain.useValue instanceof Local)){
            System.out.println("useValue other than Local found=" + udChain.useUnit + ":" + udChain.useValue.getClass());
            return mutableUnits;
        }


        Local l = (Local) udChain.useValue;
        PatchingChain<Unit> units = SootUtilities.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        PatchingChain<Unit> units2 = SootUtilities.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        SimpleLocalDefs localDefs = new SimpleLocalDefs(
                new ExceptionalUnitGraph(SootUtilities.getResolvedMethod(
                        udChain.getUseMethod()).getActiveBody()));


        //Soot JimpleLocal.equals method is not implemented, and I got grief with this.
        //Plus LocalDefs does not work well with Units since Units does not implement hashCode.
        for (Unit u:units){
            // I hope this is not bad. Unit.equals Soot is mysteriously missing
            if ( SootUtilities.AreTheseObjectEqualAsStrings(u, udChain.useUnit)
                && SootUtilities.DoesUnitUseThisLocalAsString(u, l)){
                Local equivLocal = SootUtilities.getEquivalentLocal(u, l);
                if ( localDefs.hasDefsAt(equivLocal, u)){
                    for (Unit def:localDefs.getDefsOfAt(equivLocal, u)){
                        if (def instanceof JAssignStmt &&
                                SootUtilities.isThisMethodInvoked((JAssignStmt)def, getterMethod) &&
                                SootUtilities.areOtherGetterMethodsAvailable(getterMethod.getDeclaringClass(), getterMethod)) {


                            if (! mutableUnits.contains(def)){
                                mutableUnits.add(def);
                            }
                            else
                            {
                                //System.out.println(def);
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
                SootUtilities.getResolvedMethod(udChain.getUseMethod()).getActiveBody());



        Iterator<Edge> edgeList = callGraph.edgesOutOf(SootUtilities.getResolvedMethod(udChain.getUseMethod()));

        while(edgeList.hasNext()){
            Edge  e = edgeList.next();
            if (e.tgt().equivHashCode() == getterMethod.equivHashCode() &&
                    ){
                System.out.println("Returning unit=" +  e.srcUnit() + " calling getter " + getterMethod);
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