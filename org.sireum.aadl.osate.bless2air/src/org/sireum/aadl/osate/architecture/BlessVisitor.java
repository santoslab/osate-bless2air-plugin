package org.sireum.aadl.osate.architecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.CalledSubprogram;
import org.osate.aadl2.DataClassifier;
import org.osate.aadl2.Element;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Port;
import org.osate.aadl2.Subprogram;
import org.osate.aadl2.SubprogramAccess;
import org.osate.aadl2.instance.ComponentInstance;
import org.sireum.IS;
import org.sireum.Option;
import org.sireum.Z;
import org.sireum.Z$;
import org.sireum.aadl.osate.util.BAUtils;
import org.sireum.aadl.osate.util.SlangUtils;
import org.sireum.hamr.ir.*;
import org.sireum.hamr.ir.Annex;
import org.sireum.hamr.ir.Annex$;
import org.sireum.hamr.ir.AnnexLib;
import org.sireum.hamr.ir.BLESSIntConst;
import org.sireum.hamr.ir.BTSAccessExp;
import org.sireum.hamr.ir.BTSAccessExp$;
import org.sireum.hamr.ir.BTSAction;
import org.sireum.hamr.ir.BTSAssertedAction;
import org.sireum.hamr.ir.BTSAssertedAction$;
import org.sireum.hamr.ir.BTSAssertion;
import org.sireum.hamr.ir.BTSAssignmentAction;
import org.sireum.hamr.ir.BTSAssignmentAction$;
import org.sireum.hamr.ir.BTSBLESSAnnexClause;
import org.sireum.hamr.ir.BTSBLESSAnnexClause$;
import org.sireum.hamr.ir.BTSBehaviorActions;
import org.sireum.hamr.ir.BTSBehaviorActions$;
import org.sireum.hamr.ir.BTSBehaviorTime;
import org.sireum.hamr.ir.BTSBinaryExp;
import org.sireum.hamr.ir.BTSBinaryExp$;
import org.sireum.hamr.ir.BTSBinaryOp;
import org.sireum.hamr.ir.BTSClassifier$;
import org.sireum.hamr.ir.BTSConditionalActions;
import org.sireum.hamr.ir.BTSConditionalActions$;
import org.sireum.hamr.ir.BTSDispatchCondition;
import org.sireum.hamr.ir.BTSDispatchCondition$;
import org.sireum.hamr.ir.BTSDispatchConjunction;
import org.sireum.hamr.ir.BTSDispatchConjunction$;
import org.sireum.hamr.ir.BTSDispatchTrigger;
import org.sireum.hamr.ir.BTSDispatchTriggerPort$;
import org.sireum.hamr.ir.BTSDispatchTriggerStop;
import org.sireum.hamr.ir.BTSDispatchTriggerTimeout$;
import org.sireum.hamr.ir.BTSExecuteCondition;
import org.sireum.hamr.ir.BTSExecuteConditionExp$;
import org.sireum.hamr.ir.BTSExecutionOrder;
import org.sireum.hamr.ir.BTSExp;
import org.sireum.hamr.ir.BTSFormalExpPair;
import org.sireum.hamr.ir.BTSFormalExpPair$;
import org.sireum.hamr.ir.BTSGuardedAction;
import org.sireum.hamr.ir.BTSGuardedAction$;
import org.sireum.hamr.ir.BTSIfBAAction;
import org.sireum.hamr.ir.BTSIfBAAction$;
import org.sireum.hamr.ir.BTSIfBLESSAction;
import org.sireum.hamr.ir.BTSIfBLESSAction$;
import org.sireum.hamr.ir.BTSInternalCondition;
import org.sireum.hamr.ir.BTSLiteralExp$;
import org.sireum.hamr.ir.BTSLiteralType;
import org.sireum.hamr.ir.BTSModeCondition;
import org.sireum.hamr.ir.BTSNameExp;
import org.sireum.hamr.ir.BTSNameExp$;
import org.sireum.hamr.ir.BTSPortInAction;
import org.sireum.hamr.ir.BTSPortInAction$;
import org.sireum.hamr.ir.BTSPortOutAction;
import org.sireum.hamr.ir.BTSPortOutAction$;
import org.sireum.hamr.ir.BTSSkipAction$;
import org.sireum.hamr.ir.BTSStateCategory;
import org.sireum.hamr.ir.BTSStateDeclaration;
import org.sireum.hamr.ir.BTSStateDeclaration$;
import org.sireum.hamr.ir.BTSSubprogramCallAction$;
import org.sireum.hamr.ir.BTSTransition;
import org.sireum.hamr.ir.BTSTransition$;
import org.sireum.hamr.ir.BTSTransitionCondition;
import org.sireum.hamr.ir.BTSTransitionLabel;
import org.sireum.hamr.ir.BTSTransitionLabel$;
import org.sireum.hamr.ir.BTSType;
import org.sireum.hamr.ir.BTSUnaryExp;
import org.sireum.hamr.ir.BTSUnaryExp$;
import org.sireum.hamr.ir.BTSVariableCategory;
import org.sireum.hamr.ir.BTSVariableDeclaration;
import org.sireum.hamr.ir.BTSVariableDeclaration$;
import org.sireum.hamr.ir.Classifier;
import org.sireum.hamr.ir.Component;
import org.sireum.hamr.ir.Name;
import org.sireum.hamr.ir.Property;
import org.sireum.hamr.ir.ValueProp;
import org.sireum.message.Position;

import com.multitude.aadl.bless.bLESS.AddSub;
import com.multitude.aadl.bless.bLESS.Alternative;
import com.multitude.aadl.bless.bLESS.AssertedAction;
import com.multitude.aadl.bless.bLESS.Assertion;
import com.multitude.aadl.bless.bLESS.Assignment;
import com.multitude.aadl.bless.bLESS.BAAlternative;
import com.multitude.aadl.bless.bLESS.BLESSAlternative;
import com.multitude.aadl.bless.bLESS.BLESSSubclause;
import com.multitude.aadl.bless.bLESS.BasicAction;
import com.multitude.aadl.bless.bLESS.BehaviorActions;
import com.multitude.aadl.bless.bLESS.BehaviorState;
import com.multitude.aadl.bless.bLESS.BehaviorTime;
import com.multitude.aadl.bless.bLESS.BehaviorTransition;
import com.multitude.aadl.bless.bLESS.CommunicationAction;
import com.multitude.aadl.bless.bLESS.Conjunction;
import com.multitude.aadl.bless.bLESS.Constant;
import com.multitude.aadl.bless.bLESS.Disjunction;
import com.multitude.aadl.bless.bLESS.DispatchCondition;
import com.multitude.aadl.bless.bLESS.DispatchConjunction;
import com.multitude.aadl.bless.bLESS.DispatchTrigger;
import com.multitude.aadl.bless.bLESS.ElseAlternative;
import com.multitude.aadl.bless.bLESS.ElseifAlternative;
import com.multitude.aadl.bless.bLESS.ExecuteCondition;
import com.multitude.aadl.bless.bLESS.Exp;
import com.multitude.aadl.bless.bLESS.FormalActual;
import com.multitude.aadl.bless.bLESS.GhostVariable;
import com.multitude.aadl.bless.bLESS.GuardedAction;
import com.multitude.aadl.bless.bLESS.InternalCondition;
import com.multitude.aadl.bless.bLESS.ModeCondition;
import com.multitude.aadl.bless.bLESS.MultDiv;
import com.multitude.aadl.bless.bLESS.NamedAssertion;
import com.multitude.aadl.bless.bLESS.NamelessAssertion;
import com.multitude.aadl.bless.bLESS.NamelessEnumeration;
import com.multitude.aadl.bless.bLESS.NamelessFunction;
import com.multitude.aadl.bless.bLESS.PartialName;
import com.multitude.aadl.bless.bLESS.PortInput;
import com.multitude.aadl.bless.bLESS.PortOutput;
import com.multitude.aadl.bless.bLESS.Quantity;
import com.multitude.aadl.bless.bLESS.Relation;
import com.multitude.aadl.bless.bLESS.Subexpression;
import com.multitude.aadl.bless.bLESS.SubprogramCall;
import com.multitude.aadl.bless.bLESS.TransitionLabel;
import com.multitude.aadl.bless.bLESS.TriggerLogicalExpression;
import com.multitude.aadl.bless.bLESS.Type;
import com.multitude.aadl.bless.bLESS.TypeDeclaration;
import com.multitude.aadl.bless.bLESS.UnaryOperator;
import com.multitude.aadl.bless.bLESS.Value;
import com.multitude.aadl.bless.bLESS.ValueName;
import com.multitude.aadl.bless.bLESS.Variable;
import com.multitude.aadl.bless.bLESS.VariableDeclaration;
import com.multitude.aadl.bless.bLESS.impl.BLESSSubclauseImpl;
import com.multitude.aadl.bless.bLESS.util.BLESSSwitch;

public class BlessVisitor extends BLESSSwitch<Boolean> implements AnnexVisitor {

	protected final static org.sireum.hamr.ir.AadlASTFactory factory = new org.sireum.hamr.ir.AadlASTFactory();

	Visitor v = null;

	public final static String BLESS = "BLESS";

	private List<String> path = null;

	private List<String> featureNames = null;
	private List<String> subcomponentNames = null;
	private Map<String, Classifier> resolvedBlessTypes = new HashMap<>();

	public static Option<Position> buildPosInfo(EObject object) 
	  {
  Position p = VisitorUtil.buildPosInfo(object);
  return p == null ? SlangUtils.toNone() : SlangUtils.toSome(p);
    }

	public BlessVisitor(Visitor v) {
		this.v = v;
	}

//BRL
//because data components cannot be BLESS types, do we need to
//make fake data components for each type definition in typedef libraries?	
	private Option<Classifier> resolveBlessType(TypeDeclaration t) {
		String blessTypeName = t.getQualifiedName();
		if (resolvedBlessTypes.containsKey(blessTypeName)) {
			return toSome(resolvedBlessTypes.get(blessTypeName));
		}

		for (Entry<String, Component> e : v.getDataComponents().entrySet()) {
			List<Property> props = VisitorUtil.isz2IList(e.getValue().properties());
			for (Property p : props) {
				String propertyName = p.name().name().apply(SlangUtils.toZ(0)).string();
				if (propertyName.equalsIgnoreCase("BLESS::Typed")) {
					assert (p.getPropertyValues().size().toInt() == 1);
					ValueProp value = (ValueProp) p.getPropertyValues().apply(SlangUtils.toZ(0));
					if (value.getValue().equals(blessTypeName)) {
						resolvedBlessTypes.put(blessTypeName, e.getValue().getClassifier().get());
						return e.getValue().getClassifier();
					}
				}
			}
		}
		return toNone();
	}

	private void baseTypesHack(ResourceSet rs) {
		URI u = URI.createURI("platform:/plugin/org.osate.contribution.sei/resources/packages/Base_Types.aadl");
		Resource r = rs.getResource(u, true);
		AadlPackage baseTypes = (AadlPackage) r.getContents().get(0);
		for (org.osate.aadl2.Classifier c : baseTypes.getOwnedPublicSection().getOwnedClassifiers()) {
			if (!c.getName().equalsIgnoreCase("natural")) {
				org.sireum.hamr.ir.Component comp = v.processDataType((DataClassifier) c);
				resolvedBlessTypes.put("Base_Types_" + c.getName(), comp.classifier().get());
			}
		}
	}

	 @Override
	  public List<Annex> visit(org.osate.aadl2.Classifier c, List<String> path) {
	    return new ArrayList<>();
	  }
	 
	@Override
	public List<Annex> visit(ComponentInstance ci, List<String> path) {
		List<Annex> ret = new ArrayList<>();
		if (ci.getClassifier() != null) {
			List<BLESSSubclauseImpl> bas = EcoreUtil2.eAllOfType(ci.getClassifier(), BLESSSubclauseImpl.class);
			assert (bas.size() <= 1);

			if (bas.size() == 1) {
				this.path = path;

				featureNames = ci.getFeatureInstances().stream().map(f -> f.getName()).collect(Collectors.toList());

				subcomponentNames = ci.getComponentInstances()
						.stream()
						.map(c -> c.getName())
						.collect(Collectors.toList());

				baseTypesHack(ci.eResource().getResourceSet());

				visit(bas.get(0));

				ret.add(Annex$.MODULE$.apply(BLESS, pop()));
			}
		}
		return ret;
	}

	void handle(Object o) {
		System.err.println("Need to handle " + o.getClass().getCanonicalName());

		int i = 0;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			System.err.println("  " + ste);
			if (i++ > 9) {
				break;
			}
		}
	}

	<T> IS<Z, T> l2is(List<T> l) {
		return VisitorUtil.toISZ(l);
	}

	Name toName(List<String> _path) {
		return factory.name(_path, null);
	}

	Name toName(String n, List<String> _path) {
		return factory.name(VisitorUtil.add(_path, n), null);
	}

	Name toName(String n) {
		return toName(n, path);
	}

	Name toName(Port p) {
		return toName(p.getName());
	}

	Name toSimpleName(String s) {
		if (featureNames.contains(s) || subcomponentNames.contains(s)) {
			return toName(s, path);
		} else {
			return toName(s, VisitorUtil.iList());
		}
	}

	<T> org.sireum.None<T> toNone() {
		return org.sireum.None$.MODULE$.apply();

	}

	<T> org.sireum.Some<T> toSome(T t) {
		return org.sireum.Some$.MODULE$.apply(t);
	}

	static BTSBinaryOp.Type toBinaryOp(String r) {
		if (r.equals("+")) {
			return BTSBinaryOp.byName("PLUS").get();
		} else if (r.equals("-")) {
			return BTSBinaryOp.byName("MINUS").get();
		} else if (r.equals("*")) {
			return BTSBinaryOp.byName("MULT").get();
		} else if (r.equals("/")) {
			return BTSBinaryOp.byName("DIV").get();
		} else if (r.equals("mod")) {
			return BTSBinaryOp.byName("MOD").get();
		} else if (r.equals("rem")) {
			return BTSBinaryOp.byName("REM").get();
		} else if (r.equals("**")) {
			return BTSBinaryOp.byName("EXP").get();
		} else if (r.equals("and")) {
			return BTSBinaryOp.byName("AND").get();
		} else if (r.equals("or")) {
			return BTSBinaryOp.byName("OR").get();
		} else if (r.equals("xor")) {
			return BTSBinaryOp.byName("XOR").get();
		} else if (r.equals("then")) {
			return BTSBinaryOp.byName("ANDTHEN").get();
		} else if (r.equals("else")) {
			return BTSBinaryOp.byName("ORELSE").get();
		} else if (r.equals("=")) {
			return BTSBinaryOp.byName("EQ").get();
		} else if (r.equals("<>")) {
			return BTSBinaryOp.byName("NEQ").get();
		} else if (r.equals("<")) {
			return BTSBinaryOp.byName("LT").get();
		} else if (r.equals("<=")) {
			return BTSBinaryOp.byName("LTE").get();
		} else if (r.equals(">=")) {
			return BTSBinaryOp.byName("GTE").get();
		} else if (r.equals(">")) {
			return BTSBinaryOp.byName("GT").get();
		}
		throw new RuntimeException();
	}

	@Override
	public Boolean caseBLESSSubclause(BLESSSubclause object) {

		boolean doNotProve = object.isNo_proof();

		List<BTSAssertion> _assertions = new ArrayList<>();
		if (object.getAssert_clause() != null) {
			for (NamedAssertion na : object.getAssert_clause().getAssertions()) {
				visit(na);
				_assertions.add(pop());
			}
		}

		Option<BTSAssertion> _invariant = toNone();
		if (object.getInvariant() != null) {
			visit(object.getInvariant().getInv());
			_invariant = toSome(pop());
		}

		List<BTSVariableDeclaration> _variables = new ArrayList<>();
		if (object.getVariables() != null) {

			for (VariableDeclaration bv : object.getVariables().getBehavior_variables()) {
				visit(bv);
				_variables.add(pop());
			}
		}

		List<BTSStateDeclaration> _states = new ArrayList<>();
		for (BehaviorState bs : object.getStatesSection().getStates()) {
			visit(bs);
			_states.add(pop());
		}

		List<BTSTransition> _transitions = new ArrayList<>();
		for (BehaviorTransition bt : object.getTransitions().getBt()) {
			visit(bt);
			_transitions.add(pop());
		}

		BTSBLESSAnnexClause b = BTSBLESSAnnexClause$.MODULE$.apply(doNotProve, l2is(_assertions), _invariant,
				l2is(_variables), l2is(_states), l2is(_transitions));
		push(b);

		return false;
	}

	@Override
	public Boolean caseAssertion(Assertion object) {

		BTSAssertion ret = null;
		if (object.getNamedassertion() != null) {
			visit(object.getNamedassertion());
			ret = pop();
		}

		if (object.getNamelessassertion() != null) {
			visit(object.getNamelessassertion());
			ret = pop();
		}

		if (object.getNamelessenumeration() != null) {
			visit(object.getNamelessenumeration());
			ret = pop();
		}

		if (object.getNamelessfunction() != null) {
			visit(object.getNamelessfunction());
			ret = pop();
		}

		push(ret);

		return false;
	}

	@Override
	public Boolean caseNamedAssertion(NamedAssertion object) {
//		handle(object);
//BRL
	//name=ID
  String id = object.getName();
  //formals=VariableList
  List<BTSVariable> variableList = new ArrayList<>();
  if (object.getFormals() != null)
    {
    visit(object.getFormals().getFirst());
    variableList.add(pop());
    for (Variable parameter: object.getFormals().getParameter())
      {
      visit(parameter);
      variableList.add(pop());     
      }    
    } 
  //predicate=Predicate
  Option<BTSExp> predicate = toNone();
  if (object.getPredicate() != null)
    {
    visit(object.getPredicate());
    predicate = toSome(pop());
    }
  //'returns' tod=TypeOrReference
  Option<BTSType> tod = toNone();
  if (object.getTod() != null)
    {
    visit(object.getTod());
    tod = toSome(pop());
    }
  //':='  functionvalue=AssertionFunctionValue
  Option<BTSAssertionFunctionValue> functionvalue = toNone();
  if (object.getFunctionvalue() != null)
    {
    visit(object.getFunctionvalue());
    functionvalue = toSome(pop());
    }
  //assertionvariable=ID 
  Option<org.sireum.String> assertionvariable = toNone();
  if (object.getAssertionvariable() != null)
    {
    assertionvariable = toSome(new org.sireum.String(object.getAssertionvariable()));
    }
  //enumerationType=[TypeDeclaration] 
  Option<BTSEnumerationType> enumerationType = toNone();
  if (object.getEnumerationType() != null)
    {
    visit(object.getEnumerationType());
    enumerationType = toSome(pop());
    }
  //enumer?='+=>' enumeration=AssertionEnumeration
  Option<BTSAssertionEnumeration> enumeration = toNone();
  if (object.getEnumerationType() != null)
    {
    visit(object.getEnumeration());
    enumeration = toSome(pop());
    }
  BTSAssertion na = BTSNamedAssertion$.MODULE$.apply(id,VisitorUtil.toISZ(variableList),
      predicate,tod,functionvalue,assertionvariable,enumerationType,enumeration,buildPosInfo(object));
  push(na);
		return false;
	}

//NamelessAssertion:  '<<' predicate=Predicate '>>' ;	
  @Override
  public Boolean caseNamelessAssertion(NamelessAssertion object) {

    visit(object.getPredicate());
    BTSExp exp = pop();

    // TODO: I guess we never fleshed out assertion subtypes
    BTSAssertion a = BTSNamelessAssertion$.MODULE$.apply(exp,
        buildPosInfo(object));
    push(a);

    return false;
  }

//NamelessFunction:  '<<' 'returns' tod=TypeOrReference func?=':=' functionvalue=AssertionFunctionValue '>>' ;
  @Override
  public Boolean caseNamelessFunction(NamelessFunction object) {

    visit(object.getTod());
    BTSType tod = pop();
    visit(object.getFunctionvalue());
    BTSAssertionFunctionValue functionvalue = pop();
    

    // TODO: I guess we never fleshed out assertion subtypes
    BTSAssertion a = BTSNamelessFunction$.MODULE$.apply(tod,functionvalue,
        buildPosInfo(object));
    push(a);

    return false;
  }

//NamelessEnumeration:  '<<' '+=>' enumeration=Invocation '>>';  
  @Override
  public Boolean caseNamelessEnumeration(NamelessEnumeration object) {

    visit(object.getEnumeration());
    BTSInvocation enumeration = pop();

    // TODO: I guess we never fleshed out assertion subtypes
    BTSAssertion a = BTSNamelessEnumeration$.MODULE$.apply(enumeration,
        buildPosInfo(object));
    push(a);

    return false;
  }

	@Override
	public Boolean caseValue(Value object) {

		if (object.getConstant() != null) {
			visit(object.getConstant());
		} else if (object.getValue_name() != null) {
			visit(object.getValue_name());
		} else if (object.getEnum_val() != null) {
			TypeDeclaration td = object.getEnum_val().getEnumeration_type();
			Option<Classifier> classifier = resolveBlessType(td);

			if (classifier.nonEmpty()) {

				String enumValue = object.getEnum_val().getEnumeration_value();

				BTSNameExp t = BTSNameExp$.MODULE$.apply(toSimpleName(classifier.get().getName()), toNone());
				BTSAccessExp ae = BTSAccessExp$.MODULE$.apply(t, enumValue, toNone());
				push(ae);
			} else {
				throw new RuntimeException("Couldn't resolve enum type " + td.getFullName());
			}
		} else {
			throw new RuntimeException("need to handle other Value types");
		}

		return false;
	}

	@Override
	public Boolean caseValueName(ValueName object) {
		List<String> name = null;

		NamedElement id = object.getId();
		if (id instanceof Port) {
			// get path name
			name = VisitorUtil.add(path, id.getName());
		} else if (id instanceof Variable) {
			// use simple name
			name = VisitorUtil.toIList(id.getName());
		} 
    else if (id instanceof Subprogram)
      {
      // use simple name
      name = VisitorUtil.toIList(id.getName());
      }   
    else if (id instanceof GhostVariable)
      {
      // use simple name
      name = VisitorUtil.toIList(id.getName());
      }   
		else {
			throw new RuntimeException("what is " + id);
		}

		Name n = toName(name);

		BTSExp ret = null;
		BTSExp objectName = BTSNameExp$.MODULE$.apply(n, toNone());

		if (object.isDot()) {
			List<PartialName> attributeIds = object.getPn();

			ret = BTSAccessExp$.MODULE$.apply(objectName, attributeIds.get(0).getRecord_id(), toNone());
			for (int i = 1; i < attributeIds.size(); i++) {
				ret = BTSAccessExp$.MODULE$.apply(ret, attributeIds.get(i).getRecord_id(), toNone());
			}

		} else {
			ret = objectName;
		}

		push(ret);

		return false;
	}

	@Override
	public Boolean caseConstant(Constant object) {
		BTSLiteralType.Type typ = null;
		String exp = null;

		if (object.getF() != null) {
			typ = BTSLiteralType.byName("BOOLEAN").get();
			exp = "false";
		} else if (object.getT() != null) {
			typ = BTSLiteralType.byName("BOOLEAN").get();
			exp = "true";
		} else if (object.getNumeric_constant() != null) {
			Quantity q = object.getNumeric_constant();

			assert !q.isScalar() : "Hmm, I'd think isScalar would be true for a single number";
			assert q.getUnit() == null : "Need to handle the case where unit isn't null";

			typ = BTSLiteralType.byName("INTEGER").get();
			exp = q.getNumber().getLit();

			assert q.getNumber().getProperty() == null && q.getNumber().getPropertyConstant() == null
					: "What are these?";
		} else if (object.getNul() != null) {
			throw new RuntimeException("nul isn't supported");
		} else if (object.getString_literal() != null) {
			typ = BTSLiteralType.byName("STRING").get();
			exp = object.getString_literal();
		} else {
			throw new RuntimeException("Need to handle other types of Constant");
		}

		push(BTSLiteralExp$.MODULE$.apply(typ, exp, toNone()));
		return false;
	}

	@Override
	public Boolean caseBehaviorState(BehaviorState object) {
		Name id = toSimpleName(object.getName());

		List<BTSStateCategory.Type> categories = new ArrayList<>();
		if (object.isInitial()) {
			categories.add(BTSStateCategory.byName("Initial").get());
		}
		if (object.isComplete()) {
			categories.add(BTSStateCategory.byName("Complete").get());
		}
		if (object.isFinal()) {
			categories.add(BTSStateCategory.byName("Final").get());
		}

		if (categories.isEmpty()) {
			categories.add(BTSStateCategory.byName("Execute").get());
		}

		Option<BTSAssertion> assertion = toNone();
		if (object.getState_assertion() != null) {
			visit(object.getState_assertion());
			assertion = toSome(pop());
		}

		BTSStateDeclaration bsd = BTSStateDeclaration$.MODULE$.apply(id, l2is(categories), assertion);
		push(bsd);

		return false;
	}

//BRL	
	@Override
	public Boolean caseVariableDeclaration(VariableDeclaration object) {

		Option<BTSVariableCategory.Type> category = toNone();
		if (object.isNonvolatile()) {
			category = toSome(BTSVariableCategory.byName("Nonvolatile").get());
		} else if (object.isShared()) {
			category = toSome(BTSVariableCategory.byName("Shared").get());
		} else if (object.isConstant()) {
			category = toSome(BTSVariableCategory.byName("Constant").get());
		} else if (object.isSpread()) {
			category = toSome(BTSVariableCategory.byName("Spread").get());
		} else if (object.isFinal()) {
			category = toSome(BTSVariableCategory.byName("Final").get());
		}

		Option<BTSExp> assignExpression = toNone();
		if (object.getExpression() != null) {
			visit(object.getExpression());
			assignExpression = toSome(pop());
		}

		Option<BTSAssertion> variableAssertion = toNone();
		if (object.getAssertion() != null) {
			visit(object.getAssertion());
			variableAssertion = toSome(pop());
		}

		Name name = toSimpleName(object.getVariable().getQualifiedName());

		if (object.getVariable().getTod().getTy() != null) {
			Type t = object.getVariable().getTod().getTy();

			throw new RuntimeException("Need to handle Type " + t);

		} else {
			TypeDeclaration t = object.getVariable().getTod().getRef();

			if (t.getQualifiedName() == null) {
				throw new RuntimeException("qualified name is null");
			} else {
				Option<Classifier> classifier = resolveBlessType(t);
				if (classifier.nonEmpty()) {
					push(BTSClassifier$.MODULE$.apply(classifier.get()));
				} else {
					throw new RuntimeException(
							"Couldn't resolve bless type " + t.getFullName() + " to an AADL data component");
				}
			}
		}

		BTSType varType = pop();

//BRL array size not part of variable declaration		
		Option<BLESSIntConst> arraySize = toNone();

		BTSVariableDeclaration vd = BTSVariableDeclaration$.MODULE$.apply(name, category, varType, assignExpression,
				arraySize, variableAssertion);

		push(vd);

		return false;
	}

	@Override
	public Boolean caseBehaviorTransition(BehaviorTransition object) {

		visit(object.getTransition_label());
		BTSTransitionLabel label = pop();

		List<Name> _sourceStates = new ArrayList<>();
		for (BehaviorState bs : object.getSources()) {
			String srcName = bs.getName(); // just need name
			_sourceStates.add(toSimpleName(srcName));
		}

		BehaviorState dest = object.getDestination();
		String destName = dest.getName(); // just need name
		Name destState = toSimpleName(destName);

		Option<BTSTransitionCondition> _transitionCondition = null;
		if (object.getDispatch() != null) {
			assert (_transitionCondition == null);
			visit(object.getDispatch());
			_transitionCondition = toSome(pop());
		}
		if (object.getExecute() != null) {
			assert (_transitionCondition == null);
			visit(object.getExecute());
			_transitionCondition = toSome(pop());
		}

		if (object.getMode() != null) {
			assert (_transitionCondition == null);
			visit(object.getMode());
			_transitionCondition = toSome(pop());
		}

		if (object.getInternal() != null) {
			assert (_transitionCondition == null);
			visit(object.getInternal());
			_transitionCondition = toSome(pop());
		}

		if (_transitionCondition == null) {
			_transitionCondition = toNone();
		}

		Option<BTSBehaviorActions> actions = toNone();
		if (object.getActions() != null) {
			// assert (!object.getActions().isAmp()); // TODO
			// assert (object.getActions().isSemi()); // TODO

			visit(object.getActions());
			actions = toSome(pop());
		}

		Option<BTSAssertion> assertion = toNone();
		if (object.getAss() != null) {
			visit(object.getAss());
			assertion = toSome(pop());
		}

		BTSTransition bt = BTSTransition$.MODULE$.apply(label, l2is(_sourceStates), destState, _transitionCondition,
				actions, assertion);
		push(bt);

		return false;
	}

	@Override
	public Boolean caseBehaviorActions(BehaviorActions object) {
		BTSExecutionOrder.Type executionOrder = BTSExecutionOrder.byName("Sequential").get();
		if (object.isAmp()) {
			executionOrder = BTSExecutionOrder.byName("Concurrent").get();
		}

		List<BTSAssertedAction> actions = new ArrayList<>();
		for (AssertedAction a : object.getAction()) {
			visit(a);
			actions.add(pop());
		}

		BTSBehaviorActions a = BTSBehaviorActions$.MODULE$.apply(executionOrder, l2is(actions));
		push(a);

		return false;
	}

	@Override
	public Boolean caseAssertedAction(AssertedAction object) {
		Option<BTSAssertion> precondition = toNone();
		if (object.getPrecondition() != null) {
			visit(object.getPrecondition());
			precondition = toSome(pop());
		}

		Option<BTSAssertion> postcondition = toNone();
		if (object.getPostcondition() != null) {
			visit(object.getPostcondition());
			postcondition = toSome(pop());
		}

		visit(object.getAction());
		BTSAction action = pop();

		BTSAssertedAction a = BTSAssertedAction$.MODULE$.apply(precondition, action, postcondition);
		push(a);

		return false;
	}

	@Override
	public Boolean caseAlternative(Alternative object) {
		visit(object.getGuard());
		BTSExp guard = pop();

		if (object.getBaalt() != null) {
			BAAlternative baa = object.getBaalt();

			visit(baa.getActions());
			BTSBehaviorActions ifActions = pop();

			BTSConditionalActions ifBranch = BTSConditionalActions$.MODULE$.apply(guard, ifActions);

			List<BTSConditionalActions> elseIfBranches = VisitorUtil.iList();

			for (ElseifAlternative ea : baa.getElseifalt()) {
				visit(ea.getTest());
				BTSExp elsifCond = pop();

				visit(ea.getActions());
				BTSBehaviorActions elsifActions = pop();

				BTSConditionalActions bca = BTSConditionalActions$.MODULE$.apply(elsifCond, elsifActions);
				elseIfBranches = VisitorUtil.add(elseIfBranches, bca);
			}

			Option<BTSBehaviorActions> elseBranch = toNone();
			if (baa.getElsealt() != null) {
				ElseAlternative ea = baa.getElsealt();

				visit(ea.getActions());
				elseBranch = toSome(pop());
			}

			BTSIfBAAction ret = BTSIfBAAction$.MODULE$.apply(ifBranch, VisitorUtil.toISZ(elseIfBranches), elseBranch);
			push(ret);
		} else {
			BLESSAlternative ba = object.getBlessalt();

			List<BTSGuardedAction> alternatives = VisitorUtil.iList();

			visit(ba.getAction());
			BTSAssertedAction action = pop();

			BTSGuardedAction bga = BTSGuardedAction$.MODULE$.apply(guard, action);
			alternatives = VisitorUtil.add(alternatives, bga);

			for (GuardedAction ga : ba.getAlternative()) {
				visit(ga.getGuard());
				BTSExp altGuard = pop();

				visit(ga.getAction());
				BTSAssertedAction altAction = pop();

				BTSGuardedAction altbga = BTSGuardedAction$.MODULE$.apply(altGuard, altAction);
				alternatives = VisitorUtil.add(alternatives, altbga);
			}

			BTSIfBLESSAction biba = BTSIfBLESSAction$.MODULE$.apply(toNone(), VisitorUtil.toISZ(alternatives));
			push(biba);
		}
		return false;
	}

	@Override
	public Boolean caseBasicAction(BasicAction object) {

		if (object.getSkip() != null) {
			push(BTSSkipAction$.MODULE$.apply());
			return false;
		} else {
			// visit children via default case
			return null;
		}
	}

	@Override
	public Boolean caseAssignment(Assignment object) {
		visit(object.getLhs());
		BTSExp lhs = pop();

		visit(object.getRhs());
		BTSExp rhs = pop();

		BTSAssignmentAction a = BTSAssignmentAction$.MODULE$.apply(lhs, rhs);
		push(a);

		return false;
	}

	@Override
	public Boolean caseCommunicationAction(CommunicationAction object) {

		if (object.getFp() != null) {
			throw new RuntimeException("Need to handle frozen ports");
		} else {
			defaultCase(object); // visit children via default case
		}

		return false;
	}

	private String convertRequiresSubprogramToSubcomponent(String s) {
		return s.replaceAll("_REQUIRES_HACK", "");
	}

	@Override
	public Boolean caseSubprogramCall(SubprogramCall object) {
		CalledSubprogram cs = object.getProcedure();

		String qname = null;
		if (cs instanceof Subprogram) {
			qname = ((Subprogram) cs).getFullName();
		} else if(cs instanceof SubprogramAccess) {
			qname = ((SubprogramAccess) cs).getFullName();
		} else {
			throw new RuntimeException("Unexpected: " + cs);
		}

		if (!subcomponentNames.contains(qname)) {
			if (object.getProcedure() != null) {
				// SubprogramClassifier st = object.getProcedure();

				// TODO: Need to add subprogram from the declarative model
				// v.processSubprogramClassifier(st);

				qname = convertRequiresSubprogramToSubcomponent(qname);
			} else {
				throw new RuntimeException("Missing SubprogramClassifier for " + qname);
			}
		}

		assert subcomponentNames.contains(qname) : qname + " not found";

		// TODO: handle correctly
		Name name = toSimpleName(qname);

		List<BTSFormalExpPair> params = new ArrayList<>();
		if (object.getParameters() != null) {
			for (FormalActual fa : object.getParameters().getVariables()) {
				Option<Name> paramName = toNone();
				if (fa.getFormal() != null) {
					paramName = toSome(toName(fa.getFormal().getFullName()));
				}

				Option<BTSExp> exp = toNone();
				if (fa.getActual() != null) {
					if (fa.getActual().getValue() != null) {
						Name actualName = toSimpleName(fa.getActual().getValue().getId().getFullName());
						exp = toSome(BTSNameExp$.MODULE$.apply(actualName, toNone()));
					} else if (fa.getActual().getConstant() != null) {
						visit(fa.getActual().getConstant());
						exp = toSome(pop());
					} else {
						throw new RuntimeException("Unexpected");
					}
				}

				assert object.getParameters().getVariables().size() == 1 || paramName.nonEmpty() : "paramName is empty";

				params.add(BTSFormalExpPair$.MODULE$.apply(paramName, exp, toNone()));
			}
		}
		push(BTSSubprogramCallAction$.MODULE$.apply(name, l2is(params)));

		return false;
	}  //end of caseSubprogramCall


	@Override
	public Boolean casePortInput(PortInput object) {
		Name name = toName(object.getPort());

		visit(object.getPort());
		BTSExp variable = pop();

		BTSPortInAction a = BTSPortInAction$.MODULE$.apply(name, variable);
		push(a);

		return false;
	}

	@Override
	public Boolean casePortOutput(PortOutput object) {
		Name name = toName(object.getPort().getName());

		Option<BTSExp> exp = toNone();
		if (object.getEor() != null) {
			visit(object.getEor());
			exp = toSome(pop());
		}

		BTSPortOutAction a = BTSPortOutAction$.MODULE$.apply(name, exp);
		push(a);

		return false;
	}

	@Override
	public Boolean caseTransitionLabel(TransitionLabel object) {
		Name id = toSimpleName(object.getId());

		Option<Z> priority = null;
		if (object.getPriority() != null) {
			Z value = Z$.MODULE$.apply(Integer.parseInt(object.getPriority().getPriority()));
			priority = toSome(value);
		} else {
			priority = toNone();
		}

		BTSTransitionLabel label = BTSTransitionLabel$.MODULE$.apply(id, priority);
		push(label);

		return false;
	}

	@Override
	public Boolean caseDispatchCondition(DispatchCondition object) {
		List<BTSDispatchConjunction> dispatchTriggers = new ArrayList<>();
		if (object.getDe() != null) {
			for (DispatchConjunction dc : object.getDe().getDc()) {
				visit(dc);
				dispatchTriggers.add(pop());
			}
		}

		List<Name> frozenPorts = new ArrayList<>();
		if (object.getFrozen() != null) {
			for (Port p : object.getFrozen().getFrozen()) {
				frozenPorts.add(toName(p));
			}
		}

		BTSDispatchCondition bdc = BTSDispatchCondition$.MODULE$.apply(l2is(dispatchTriggers), l2is(frozenPorts));
		push(bdc);

		return false;
	}

	@Override
	public Boolean caseDispatchConjunction(DispatchConjunction object) {

		List<BTSDispatchTrigger> conjunction = new ArrayList<>();
		for (DispatchTrigger t : object.getTrigger()) {
			visit(t);
			conjunction.add(pop());
		}

		BTSDispatchConjunction dc = BTSDispatchConjunction$.MODULE$.apply(l2is(conjunction));
		push(dc);

		return false;
	}

	@Override
	public Boolean caseDispatchTrigger(DispatchTrigger object) {

		BTSDispatchTrigger ret = null;
		if (object.getStop() != null) {
			assert ret == null;

			ret = BTSDispatchTriggerStop.apply();
		}

		if (object.getTimeout() != null) {
			assert ret == null;
			List<Name> ports = new ArrayList<>();
			if (object.isLp()) {
				for (NamedElement p : object.getPorts()) {
					ports.add(toName(p.getFullName()));
				}
			}

			Option<BTSBehaviorTime> time = toNone();
			if (object.getTime() != null) {
				visit(object.getTime());
				time = toSome(pop());
			}

			ret = BTSDispatchTriggerTimeout$.MODULE$.apply(l2is(ports), time);
		}

		if (object.getPort() != null) {
			assert ret == null;

			ret = BTSDispatchTriggerPort$.MODULE$.apply(toName(object.getPort().getPort().getFullName()));
		}

		assert ret != null;
		push(ret);

		return false;
	}

//BRL
  @Override
  public Boolean caseBehaviorTime(BehaviorTime object) {
  
  if (object.getQuantity() != null)
    {
    visit(object.getQuantity());
    BTSQuantity quantity = pop();
    push(quantity);
    }
  
  if (object.getValue() != null)
    {
    visit(object.getValue());
    BTSValue value = pop();
    push(value);
    }
  
  if (object.getDuration() != null)
    {
    visit(object.getDuration());
    BTSExp duration = pop();
    push(duration);
    }
  
  return false;
  }

  @Override
  public Boolean caseExecuteCondition(ExecuteCondition object) {

		visit(object.getEor());
		BTSExp e = pop();

		BTSExecuteCondition c = BTSExecuteConditionExp$.MODULE$.apply(e);
		push(c);

		return false;
	}

	@Override
	public Boolean caseDisjunction(Disjunction object) {
  //push all operands onto a stack
  Stack<BTSExp> exps = new Stack<>();
  visit(object.getL());
  exps.push(pop());
  for (Conjunction r: object.getR())
    {
    visit(r);
    exps.push(pop());     
    }
  
  //replace top two elements on stack with op(l,r) until only one remains
  while (exps.size() > 1) 
    {
    BTSExp lhs = exps.pop();
    BTSExp rhs = exps.pop();
    BTSBinaryOp.Type op = BAUtils.toBinaryOp(object.getSym());
    exps.push(BTSBinaryExp$.MODULE$.apply(op, lhs, rhs, buildPosInfo(object)));
    }
  
  //just one BTSExp on stack
  push(exps.pop());
  return false;
	}

	@Override
	public Boolean caseConjunction(Conjunction object) {
  //push all operands onto a stack
  Stack<BTSExp> exps = new Stack<>();
  visit(object.getL());
  exps.push(pop());
  for (Relation r: object.getR())
    {
    visit(r);
    exps.push(pop());     
    }
  
  //replace top two elements on stack with op(l,r) until only one remains
  while (exps.size() > 1) 
    {
    BTSExp lhs = exps.pop();
    BTSExp rhs = exps.pop();
    BTSBinaryOp.Type op = BAUtils.toBinaryOp(object.getSym());
    exps.push(BTSBinaryExp$.MODULE$.apply(op, lhs, rhs, buildPosInfo(object)));
    }
  
  //just one BTSExp on stack
  push(exps.pop());
  return false;
	}

	@Override
	public Boolean caseSubexpression(Subexpression object) {

		if (object.getUnary() != null) {
			UnaryOperator uo = object.getUnary();
			if (uo.getNot() != null) {
				visit(object.getTimed_expression());
				BTSExp boolExp = pop();

				BTSUnaryExp bue = BTSUnaryExp$.MODULE$.apply(BAUtils.toUnaryOp("!"), boolExp, toNone());
				push(bue);
			} else {
				throw new RuntimeException("Need to handle other types of unary exp " + uo);
			}
		} else {
			visit(object.getTimed_expression());
		}

		return false;
	}

  @Override
  public Boolean caseAddSub(AddSub object) {
    //push all operands onto a stack
    Stack<BTSExp> exps = new Stack<>();
    visit(object.getL());
    exps.push(pop());
    for (MultDiv r: object.getR())
      {
      visit(r);
      exps.push(pop());     
      }
    
    //replace top two elements on stack with op(l,r) until only one remains
    while (exps.size() > 1) 
      {
      BTSExp lhs = exps.pop();
      BTSExp rhs = exps.pop();
      BTSBinaryOp.Type op = BAUtils.toBinaryOp(object.getSym());
      exps.push(BTSBinaryExp$.MODULE$.apply(op, lhs, rhs, buildPosInfo(object)));
      }
    
    //just one BTSExp on stack
    push(exps.pop());
    return false;
  }

  @Override
  public Boolean caseMultDiv(MultDiv object) {
    //push all operands onto a stack
    Stack<BTSExp> exps = new Stack<>();
    visit(object.getL());
    exps.push(pop());
    for (Exp r: object.getR())
      {
      visit(r);
      exps.push(pop());     
      }
    
    //replace top two elements on stack with op(l,r) until only one remains
    while (exps.size() > 1) 
      {
      BTSExp lhs = exps.pop();
      BTSExp rhs = exps.pop();
      BTSBinaryOp.Type op = BAUtils.toBinaryOp(object.getSym());
      exps.push(BTSBinaryExp$.MODULE$.apply(op, lhs, rhs, buildPosInfo(object)));
      }
    
    //just one BTSExp on stack
    push(exps.pop());
    return false;
  }

	@Override
	public Boolean caseRelation(Relation object) {
		visit(object.getL());
		BTSExp lhs = pop();

		if (object.getR() == null) {
			push(lhs);
			return false;
		}

		visit(object.getR());
		BTSExp rhs = pop();

		BTSBinaryOp.Type op = toBinaryOp(object.getSym());

		BTSBinaryExp be = BTSBinaryExp$.MODULE$.apply(op, lhs, rhs, toNone());
		push(be);

		return false;
	}

	@Override
	public Boolean caseModeCondition(ModeCondition object) {
		visit(object.getTle());
		BTSTriggerLogicalExpression tle = pop();
		BTSModeCondition c = BTSModeCondition$.MODULE$.apply(tle);
		push(c);
		return false;
	}

	@Override
	public Boolean caseTriggerLogicalExpression(TriggerLogicalExpression object)
	{
	
	return false;
	}
	
	@Override
	public Boolean caseInternalCondition(InternalCondition object) {
    ArrayList<Name> ports = new ArrayList<Name>();
    ports.add(toName(object.getFirst()));
    for (Port p: object.getPorts())
      ports.add(toName(p));
    
		BTSInternalCondition c = BTSInternalCondition$.MODULE$.apply(VisitorUtil.toISZ(ports));
		push(c);

		return false;
	}

	@Override
	public Boolean defaultCase(EObject o) {
		for (EObject child : o.eContents()) {
			visit(child);
		}
		return null;
	}

	public Boolean visit(EObject o) {
		assert (isSwitchFor(o.eClass().getEPackage()));
		return doSwitch(o);
	}

	Object result = null;

	void push(Object o) {
		if (result != null) {
			assert result == null : "Trying to push " + o + " but result isn't null " + result;
		}
		result = o;
	}

	@SuppressWarnings("unchecked")
	<T> T pop() {
		assert (result != null);
		T ret = (T) result;
		result = null;
		return ret;
	}

	@Override
	public List<AnnexLib> buildAnnexLibraries(Element arg0) {
		return VisitorUtil.iList();
	}
}
