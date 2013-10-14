package jmint;


import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.DefinitionStmt;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;

public class MutantHeader{

    public final UseDefChain udChain;
    public final Pair<DefinitionStmt,Host> originalDefStmt; //non-mutated stmt
    public final MutantsCode mutantsCode;
    public final List<MutantInfo> mutants = new ArrayList<MutantInfo>();

    public MutantHeader(UseDefChain udChain, Pair<DefinitionStmt, Host> originalDefStmt, MutantsCode mutantsCode){
        this.udChain = udChain;

        this.originalDefStmt = originalDefStmt;
        this.mutantsCode = mutantsCode;
    }

    public String getKey(){

        String keyTemplate = "SootClass=[%s]:SootMethod=[%s]:MutantCode=[%s]:LineNo=[%s]";

        String sootClass = "";
        String sootMethod = "";
        String lineNo = SootUtilities.getTagOrDefaultValue(originalDefStmt.getO1().getTag("LineNumberTag"), "-1");

        if ( originalDefStmt.getO2() instanceof SootClass){
            sootClass = originalDefStmt.getO2().toString();
        }
        else
        {
            sootMethod = originalDefStmt.getO2().toString();
            sootClass = ((SootMethod)originalDefStmt.getO2()).getDeclaringClass().toString();
        }

        return String.format(keyTemplate, sootClass, sootMethod, mutantsCode, lineNo );



    }


}