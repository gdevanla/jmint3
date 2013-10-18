package jmint;


import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;

public class MutantHeader {

    public final UseDefChain udChain;
    public final Pair<Stmt, Host> actualDefStmt;
    public final Pair<?,?> originalDefStmt; //non-mutated stmt
    public final MutantsCode mutantsCode;
    public final List<MutantInfo> mutants = new ArrayList<MutantInfo>();
    public String otherInfo="";
    public final String lineNoOriginalStmt;

    public MutantHeader(UseDefChain udChain, Pair<Stmt, Host> actualDefStmt, Pair<?, ?> originalDefStmt, MutantsCode mutantsCode){
        this.udChain = udChain;
        this.actualDefStmt = actualDefStmt;

        this.originalDefStmt = originalDefStmt;
        this.mutantsCode = mutantsCode;

        if (actualDefStmt.getO1() instanceof Stmt){
            this.lineNoOriginalStmt = SUtil.getTagOrDefaultValue(((Stmt) actualDefStmt.getO1()).getTag("LineNumberTag"), "-1");
        }
        else
        {
            this.lineNoOriginalStmt = "-1";
        }
    }

    public MutantHeader(UseDefChain udChain, Pair<Stmt, Host> actualDefStmt, Pair<?, ?> originalDefStmt, MutantsCode mutantsCode, String otherInfo){
       this(udChain, actualDefStmt, originalDefStmt, mutantsCode);
        this.otherInfo = otherInfo;
    }

    public String getKey(){

        String keyTemplate = "SootClass=[%s]:MutantCode=[%s]:LineNo=[%s]";

        String sootClass = "";
        String sootMethod = "";

        if ( originalDefStmt.getO2() instanceof SootClass){
            sootClass = originalDefStmt.getO2().toString();
        }
        else
        {
            sootMethod = originalDefStmt.getO2().toString();
            sootClass = ((SootMethod)originalDefStmt.getO2()).getDeclaringClass().toString();
        }
        return String.format(keyTemplate, sootClass, mutantsCode, lineNoOriginalStmt);
    }

    public String getMuJavaFormatKey(){

        String keyTemplate = "%s:%s:%s";

        String sootClass = "";
        String sootMethod = "";

        if ( originalDefStmt.getO2() instanceof SootClass){
            sootClass = originalDefStmt.getO2().toString();
        }
        else
        {
            sootMethod = originalDefStmt.getO2().toString();
            sootClass = ((SootMethod)originalDefStmt.getO2()).getDeclaringClass().toString();
        }

        return String.format(keyTemplate, sootClass, mutantsCode, lineNoOriginalStmt);

    }

}