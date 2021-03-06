package jmint;


import org.slf4j.Logger;
import soot.*;
import soot.JastAddJ.AssignShiftExpr;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

import java.util.*;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;


/* TODO: This util file was put together as a prototype. Many of the work these functions
perform can be better replaced with some Soot calls
 */

public class SUtil {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(SUtil.class);

    public static Unit getFirstNonIdentityStmt(SootMethod m)
    {

        for (Unit u:m.getActiveBody().getUnits()){
            if (u instanceof IdentityStmt)
                continue;
            return u;
        }

        //is this possible during jimple transform, I doubt
        return null;

    }


    public static boolean isTypeIncludedInAnalysis(RefType r){
        for ( String s:Configuration.packageUnderTest){
            if ( r.toString().startsWith(s))
                return true;
        }
        return false;
    }

    public static boolean eqAsStr(Object o1, Object o2){
       return o1.toString().equals(o2.toString());
    }

    public static boolean isClassIncludedInAnalysis(SootClass r){
        for ( String s:Configuration.getPackageUnderTest()){
            if (r.getPackageName().startsWith(s)){
                return true;
            }
        }
        return false;
    }


    public static boolean isSpecialInvokeExprOnValue(Value r, Unit u){
        if ( (u instanceof InvokeStmt))
        {
            if (((InvokeStmt)u).getInvokeExpr() instanceof SpecialInvokeExpr){
                SpecialInvokeExpr expr = (SpecialInvokeExpr) ((InvokeStmt)u).getInvokeExpr();

                if (expr.getBase().equals(r)){
                    return true;
                }
            }
        }
        return false;
    }



    public static Unit getUnitInvokingDefaultConstructor(Value r, SootMethod method){
        for (Unit u:getResolvedMethod(method).getActiveBody().getUnits()){
            if (isSpecialInvokeExprOnValue(r, u))
            {
                if ( isUnitInvokingDefaultConstructor(u)){
                    return u;
                }
            }
        }
        return null;
    }

    private static boolean isUnitInvokingDefaultConstructor(Unit u) {
        return ((InvokeStmt)u).getInvokeExpr().getArgCount() == 0;
    }

    public static Unit getUnitInvokingAnConstructor(Value r, SootMethod method){
        for (Unit u:getResolvedMethod(method).getActiveBody().getUnits()){
            if (isSpecialInvokeExprOnValue(r, u)) { return u; }
        }
        return null;
    }

    public static SootMethod getResolvedMethod(String methodName) {
        //Assume Soot scene is currently active
        SootClass klass = Scene.v().forceResolve(methodName,
                SootClass.SIGNATURES);
        return klass.getMethodByName(methodName);
    }

    public static SootMethod getResolvedMethod(SootMethod method) {
        //Assume Soot scene is currently active
        SootClass klass = Scene.v().forceResolve(method.getDeclaringClass().getName(),
                SootClass.SIGNATURES);
        //logger.debug(method.getName() + ":" + method.getSignature() + ":" + method.getSubSignature());
        return klass.getMethod(method.getSubSignature());
    }

    public static SootClass getResolvedClass(String className) {
        //Assume Soot scene is currently active
        SootClass klass = Scene.v().forceResolve(className,
                SootClass.BODIES | SootClass.SIGNATURES);
        return klass;
    }

    public static SootClass getResolvedClass(SootMethod method) {
        //Assume Soot scene is currently active
        return getResolvedClass(method.getDeclaringClass().getName());
    }

    public static boolean doesClassHasMember(SootClass klass, String name, Type type) {

        //TODO: Need to handle super class
        for (SootField fields:klass.getFields()){
            if ( fields.getName().equals(name) &&
                    !Modifier.isStatic(fields.getModifiers())
                    && fields.getType().toString().equals(fields.getType().toString())){
                return true;
            }
        }


        return false;
    }

    /* This method is used to get around Unit in Soot not implementing hashcode or equals method
    properly.
     */
    public static boolean AreTheseObjectEqualAsStrings(Object o1, Object o2) {
        if (o1.toString().equals(o2.toString())) return true;
        else return false;
    }

    //Careful using this. Use it if you know the calls are scoped to the same methods
    //where l and u belong to.
    public static boolean DoesUnitUseThisLocalAsString(Unit u, Local l) {
        List<ValueBox> boxes = u.getUseBoxes();
        for (ValueBox b:boxes){
            if (b.getValue().toString().equals(l.toString())) return true;
        }
        return false;
    }

    public static Local getEquivalentLocal(Unit u, Local l) {
        List<ValueBox> boxes = u.getUseAndDefBoxes();
        for (ValueBox b:boxes){
            if (b.getValue().hashCode() == l.hashCode()){
                return (Local)b.getValue();
            }
        }
        return null;

    }

    public static boolean isThisMethodInvoked(JAssignStmt def, SootMethod getterMethod) {
        /*TODO: This method does not support method invocations as part of invoke interface
        since the full signature returned by getSignature would be different*/
        return def.containsInvokeExpr()  && (def.getRightOp() instanceof InstanceInvokeExpr) &&
                ((InvokeExpr)def.getRightOp()).getMethod().getSignature().equals(getterMethod.getSignature());
    }

    public static boolean areOtherGetterMethodsAvailable(SootClass declaringClass, SootMethod excludeMethod) {
        List<SootMethod> methods = declaringClass.getMethods();
        for (SootMethod method: methods){
            if (method.getParameterCount() == 0 &&
                    method.getName().startsWith("get")
                    && !method.isStatic()
                    && !method.getName().equals(excludeMethod.getName()) && !method.isAbstract()
                    && method.getReturnType().toString().equals(excludeMethod.getReturnType().toString())){
                return true;
            }
        }

        return false;
    }

    public static List<SootMethod> getAlternateGetterMethods(SootClass declaringClass, SootMethod excludeMethod) {
        List<SootMethod> availMethods = new ArrayList<SootMethod>();

        for (SootMethod method: declaringClass.getMethods()){
            if (method.getParameterCount() == 0 &&
                    method.getName().startsWith("get")  && !method.isStatic()
                    && !method.getName().equals(excludeMethod.getName()) && !method.isAbstract()
                    && method.getReturnType().toString().equals(excludeMethod.getReturnType().toString())){
                availMethods.add(method);
            }
        }

        return availMethods;
    }

    public static String getTagOrDefaultValue(Tag tag, String defaultValue){
        if (tag == null)
            return defaultValue;
        else
            return tag.toString();
    }


    public static Unit getSpecialInvokeToBaseClassNonDefaultConstructor(SootMethod initMethod) {
        PatchingChain<Unit> units = initMethod.getActiveBody().getUnits();

        for ( Unit u:units){
            if (u instanceof InvokeStmt){
                if (((InvokeStmt)u).getInvokeExpr() instanceof SpecialInvokeExpr){
                    SpecialInvokeExpr expr =  (SpecialInvokeExpr)((InvokeStmt)u).getInvokeExpr();
                    if (expr.getMethod().getDeclaringClass().equals(initMethod.getDeclaringClass().getSuperclass())
                            && expr.getMethod().getName().contains("<init>") && expr.getArgCount() > 0){
                        return u;
                    }
                }
            }
        }

        return null;
    }

    public static boolean doesClassHaveDefaultConstructor(SootClass c){

        List<SootMethod> methods = c.getMethods();
        for (SootMethod m:methods){
            if ( m.getName().contains("<init>") && m.getParameterCount() == 0){
                return true;
            }
        }
        return false;
    }

    public static Set<Unit> findUnitInvokingOverloadedMethods(UseDefChain udChain, SootMethod method){
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

        for (Unit u:units){
            // I hope this is not bad. Unit.equals Soot is mysteriously missing
            if ( u.equals(udChain.useUnit)
                    && SUtil.DoesUnitUseThisLocalAsString(u, l)){
                Local equivLocal = SUtil.getEquivalentLocal(u, l);
                if ( localDefs.hasDefsAt(equivLocal, u)){
                    for (Unit def:localDefs.getDefsOfAt(equivLocal, u)){
                        if (def instanceof JAssignStmt &&
                                ((JAssignStmt) def).containsInvokeExpr() &&
                                SUtil.isThisMethodOverloaded((((JAssignStmt) def).getInvokeExpr()))){
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

    private static boolean isThisMethodOverloaded(InvokeExpr invokeExpr) {
        SootClass klass = SUtil.getResolvedClass(invokeExpr.getMethod().getDeclaringClass().toString());
        SootMethod method = invokeExpr.getMethod();

        for(SootMethod m : klass.getMethods()){
            if (m.getName().equals(method.getName()) && !m.getSubSignature().equals(method.getSubSignature())){
                return true; //we have found atleast one overloaded method
            }
        }
        return false;
    }

    public static Multiset<Type> getTypesInMethod(SootMethod method){
        Multiset<Type> argumentTypes = HashMultiset.create();
        for (Type t:method.getParameterTypes()){
            argumentTypes.add(t);
        }
        return argumentTypes;
    }

    public static boolean isTypeListASubset(Multiset<Type> t, Multiset<Type> ts ,
                                            boolean argTypeCountEquals){
        //TODO: Check for base/sub class relationships.
        for (Type type:ts){
            if (!t.contains(type)){
                return false;
            }
            else if (argTypeCountEquals && ts.count(type) != t.count(type)){
                return false;
            }
            else if (!argTypeCountEquals && ts.count(type) > t.count(type)){
                return false;
            }

        }
        return true;
    }

    public static List<SootMethod> alternateMethodsForOMR(SootMethod method){
        List<SootMethod> methods = new ArrayList<SootMethod>();
        SootClass klass = method.getDeclaringClass();
        Multiset<Type> origTypes = getTypesInMethod(method);

        for (SootMethod m:klass.getMethods()){
            Multiset<Type> types = getTypesInMethod(m);

            if ( !m.equals(method)
                    && m.getReturnType().equals(method.getReturnType())
                    && m.getName().equals(method.getName())
                    && !m.isStatic()
                    && isTypeListASubset(origTypes, types, false)){
                logger.debug("Overloaded method=" + m + "can be substituted for " + method);
                methods.add(m);
            }
        }
        return methods;
    }

    public static boolean areMethodsAvailableFor(SootMethod method, boolean argTypesCount){

        SootClass klass = method.getDeclaringClass();
        Multiset<Type> origTypes = getTypesInMethod(method);

        for (SootMethod m:klass.getMethods()){
            Multiset<Type> types = getTypesInMethod(m);

            if ( !m.equals(method)
                    && m.getReturnType().equals(method.getReturnType())
                    && m.getName().equals(method.getName())
                    && isTypeListASubset(origTypes, types, argTypesCount)){
                logger.debug("Overloaded method=" + m + "can be substituted for " + method);
                return true;
            }
        }
        return false;

    }

    public static Set<SootClass> getSubClassesWithDefaultConstructor(Type type) {
        Set<SootClass> classes =  new HashSet<SootClass>();

        Collection<SootClass> subClasses = Scene.v().getOrMakeFastHierarchy().
                getSubclassesOf(SUtil.getResolvedClass(type.toString()));

        //check if atleast one subClass has a default constructor
        for (SootClass c:subClasses){
            for (SootMethod m:c.getMethods()){
                if ( m.getName().equals("<init>") && m.getParameterCount() == 0){
                    classes.add(c);
                }
            }
        }

        return classes;
    }


    public static Set<Unit> findAssignStmtsWithInvoke(UseDefChain udChain, SootMethod getterMethod){
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

        for (Unit u:units){
            if ( u.equals(udChain.useUnit)
                    && SUtil.DoesUnitUseThisLocalAsString(u, l)){
                //Local equivLocal = SUtil.getEquivalentLocal(u, l);
                if ( localDefs.hasDefsAt(l, u)){
                    for (Unit def:localDefs.getDefsOfAt(l, u)){
                        if (def instanceof JAssignStmt &&
                                ((JAssignStmt) def).containsInvokeExpr() &&
                                areParametersGeneralizable(((JAssignStmt) def).getInvokeExpr())){

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

    public static boolean areParametersGeneralizable(InvokeExpr invokeExpr) {
        if (!(invokeExpr instanceof JVirtualInvokeExpr)){
            return false;
        }

        JVirtualInvokeExpr expr = (JVirtualInvokeExpr) invokeExpr;
        SootClass klass = SUtil.getResolvedClass(expr.getBase().getType().toString());
        SootMethod method = expr.getMethod();
        List<Type> parameterTypes = method.getParameterTypes();

        for (Type t:parameterTypes){
            SootClass c = SUtil.getResolvedClass(t.toString());
            if (c.hasSuperclass()) return true;
        }

        return false;
    }


    public static Set<Pair<Type, Type>> getGeneralizableParameters(InvokeExpr invokeExpr) {
        Set<Pair<Type, Type>> params = new HashSet<Pair<Type, Type>>();
        if (!(invokeExpr instanceof JVirtualInvokeExpr)){
            return params;
        }

        JVirtualInvokeExpr expr = (JVirtualInvokeExpr) invokeExpr;
        SootClass klass = SUtil.getResolvedClass(expr.getBase().getType().toString());
        SootMethod method = expr.getMethod();
        List<Type> parameterTypes = method.getParameterTypes();

        for (Type t:parameterTypes){
            SootClass c = SUtil.getResolvedClass(t.toString());
            if (c.hasSuperclass())
            {
             params.add(new Pair<Type, Type>(c.getType(), c.getSuperclass().getType()));
            }
        }

        return params;
    }


    public static boolean isTypeIncludedInAnalysis(Type t) {
        for ( String s:Configuration.packageUnderTest){
            if ( t.toString().startsWith(s))
                return true;
        }
        return false;
    }

    public static Set<Type> getAllTypesInLocal(Chain<Local> locals, boolean filterByAnalyzedPackage) {
        Set<Type> types = new HashSet<Type>();

        for(Local l:locals){
            if (filterByAnalyzedPackage && !SUtil.isTypeIncludedInAnalysis(l.getType())) continue;
            else types.add(l.getType());

        }
        return types;

    }


    public static Set<Type> getAllTypesInInstance(SootClass klass, boolean filterByAnalyzedPackage) {
        Set<Type> types = new HashSet<Type>();

        for (SootField f:klass.getFields()){
            if (filterByAnalyzedPackage && !SUtil.isTypeIncludedInAnalysis(f.getType()) && !f.isStatic()) continue;
            types.add(f.getType());
        }

        return types;
    }

    //Note that this method handles case where t1 is child of t2
    public static boolean doTypesShareParentClass(Type t1, Type t2) {

        SootClass c1 = SUtil.getResolvedClass(t1.toString());
        SootClass c2 = SUtil.getResolvedClass(t2.toString());

        if (!c1.hasSuperclass()) return false;
        if (!c2.hasSuperclass()) return false;
        if (c1.equals(c2)) return false;

        if ( c1.getSuperclass().equals(c2.getSuperclass())){
            return true;
        }

        return false;
    }

    public static boolean areOtherSetterMethodsAvailable(SootClass declaringClass, SootMethod excludeMethod) {
        List<SootMethod> methods = declaringClass.getMethods();

        Multiset<Type> typesInExcludeMethod = HashMultiset.create();
        typesInExcludeMethod.addAll(excludeMethod.getParameterTypes());

        for (SootMethod method: methods){
            Multiset<Type> typesInMethod = HashMultiset.create();
            typesInMethod.addAll(method.getParameterTypes());

            if (method.getName().startsWith("set")
                    && !method.getName().equals(excludeMethod.getName()) && !method.isAbstract()
                    && method.getReturnType().toString().equals(excludeMethod.getReturnType().toString())
                    && isTypeListASubset(typesInExcludeMethod, typesInMethod, true)){
                return true;
            }
        }

        return false;
    }

    public static boolean isAlternateMethodAvailForOMD(SootMethod method) {

        SootClass klass = method.getDeclaringClass();

        List<Type> types = method.getParameterTypes();

        for (SootMethod m:klass.getMethods()){
            if (method.equals(m))
                continue;
            if (method.getName().equals(m.getName())
                    && method.getReturnType().toString().equals(m.getReturnType().toString())
                    && canEachTypeBeUpCast(types, m)) {
                return true;
            }
        }
        return false;

    }

    public static List<SootMethod> alternateMethodsForOMD(SootMethod method) {

        List<SootMethod> methods = new ArrayList<SootMethod>();
        SootClass klass = method.getDeclaringClass();

        List<Type> types = method.getParameterTypes();

        for (SootMethod m:klass.getMethods()){
            if (method.equals(m))
                continue;
            if (method.getName().equals(m.getName())
                    && method.getReturnType().toString().equals(m.getReturnType().toString())
                    && canEachTypeBeUpCast(types, m)) {
                methods.add(m);
            }
        }
        return methods;

    }

    private static boolean canEachTypeBeUpCast(List<Type> types, SootMethod m) {

        if (types.size() != m.getParameterCount()) return false;

        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
        for(int i=0; i< types.size(); i++){

            Type t1 = types.get(i);
            Type t2 = m.getParameterType(i);

          if (SUtil.isPrimitive(t1) || SUtil.isPrimitive(t2)){
                if (!canCastPrimitive(t1, t2))
                    return false;
          }
          else {
              SootClass klass1 = Scene.v().forceResolve(t1.toString(),
                      SootClass.SIGNATURES);
              SootClass klass2 = Scene.v().forceResolve(t2.toString(),
                      SootClass.SIGNATURES);
              //System.out.println(t1);
              //System.out.println(t2);
              //System.out.println("klass1=" + klass1);
              //System.out.println("klass2=" + klass2);

              if ( !SUtil.isClassIncludedInAnalysis(klass1) || !SUtil.isClassIncludedInAnalysis(klass2))
              {
                  return false;
              }

              if ( klass1.isPhantom())
              {
                  logger.debug("klass1 =" + klass1 + "was a phantom class");
                  return false;
              }
              if (klass2.isPhantom()){
                  logger.debug("klass2 =" + klass2 + "was a phantom class");
                  return false;
              }

              try {
                  if (!fh.isSubclass(klass1, klass2))
                      return false;
              }
              catch (Exception ex){
                  logger.debug("IsSubClass exception for klasses=" + klass1 + ":" + klass2);
              }




          }
        }

        return true;
    }

    private static boolean canCastPrimitive(Type from, Type to){
        if ( from instanceof IntType && (to instanceof FloatType || to instanceof DoubleType)){
            return true;
        }

        if ( from instanceof FloatType && to instanceof DoubleType){
            return true;
        }

        return false;
    }


    public static boolean isPrimitive(soot.Type t) {
        return t instanceof IntType ||t instanceof FloatType
                || t instanceof FloatType ||  t instanceof DoubleType
                || t instanceof LongType || t instanceof soot.BooleanType
                || t instanceof soot.CharType || t instanceof soot.ByteType
                || t instanceof soot.ShortType;
    }

    public static SootMethod getDefaultConstructor(SootClass c){
        for (SootMethod m: c.getMethods()){
            if ( m.getName().contains("<init>") && m.getParameterCount() == 0){
                return m;
            }
        }
        return null;
    }

    public static List<Local> createLocals(SootMethod m){
        List<Local> locals = new ArrayList<Local>();
        int i=0;
        for(Type t:m.getParameterTypes()){
            locals.add(new JimpleLocal( "mutant_local0" + i ,t));
            i++;
        }
        return locals;
    }

    public static List<Unit> getUseUnitsInvokingMethodForADef(SootMethod m, Unit defStmt, SootMethod defMethod){

        List<Unit> units = new ArrayList<Unit>();

        ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(SUtil.getResolvedMethod(
                defMethod).getActiveBody());
        SimpleLocalDefs localDefs = new SimpleLocalDefs(unitGraph);
        SimpleLocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);

        localUses.getUsesOf(defStmt);
        for (Object o:localUses.getUsesOf(defStmt)){
            Unit useUnit = ((UnitValueBoxPair)o).unit;

            if (doesUnitInvokeMethod(useUnit, m)){
                units.add(useUnit);
            }
        }
        return units;

    }


    public static List<Unit> getDefUnitsInvokingMethodForAUse(SootMethod m, Unit useStmt, SootMethod useMethod, Local useValue){

        List<Unit> units = new ArrayList<Unit>();

        ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(SUtil.getResolvedMethod(
                useMethod).getActiveBody());
        SimpleLocalDefs localDefs = new SimpleLocalDefs(unitGraph);
        //SimpleLocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);

        for (Unit defUnit:localDefs.getDefsOfAt(useValue, useStmt)){
            if (doesUnitInvokeMethod(defUnit, m)){
                units.add(defUnit);
            }
        }
        return units;

    }






    public static List<Unit> getUsesOfDef(SootMethod m, Unit defStmt){

        List<Unit> units = new ArrayList<Unit>();

        ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(SUtil.getResolvedMethod(
                m).getActiveBody());
        SimpleLocalDefs localDefs = new SimpleLocalDefs(unitGraph);
        SimpleLocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);

        localUses.getUsesOf(defStmt);
        for (Object o:localUses.getUsesOf(defStmt)){
            Unit useUnit = ((UnitValueBoxPair)o).unit;
            units.add(useUnit);
        }
        return units;
    }

    public static InvokeExpr getInvokeExpr(AssignStmt stmt, SootMethod m){

        InvokeExpr expr = null;
        for(ValueBox b:stmt.getUseBoxes()){
            if (b.getValue() instanceof InvokeExpr
                    && ((InvokeExpr) b.getValue()).getMethod().equals(m)){
                return (InvokeExpr)b.getValue();
            }
        }

        return expr;

    }

    public static boolean doesUnitInvokeMethod(Unit u, SootMethod m){

        if (u instanceof InvokeStmt){
            InvokeExpr is = ((InvokeStmt) u).getInvokeExpr();
            if (is.getMethod().equals(m)){
                return true;
            }
        }
        else if ( (u instanceof AssignStmt) &&
            getInvokeExpr((AssignStmt)u, m) != null) {
                return true;
            }
        return false;
    }
}


