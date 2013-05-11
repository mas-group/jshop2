// $ANTLR 2.7.2: "JSHOP2.g" -> "JSHOP2Parser.java"$

  package JSHOP2;

  import java.io.IOException;
  import java.util.LinkedList;
  import java.util.Vector;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class JSHOP2Parser extends antlr.LLkParser       implements JSHOP2TokenTypes
 {

  //-- The object that represents the domain being parsed.
  private InternalDomain domain;

  //-- The lexer object that does the lexing for this parser.
  private JSHOP2Lexer lexer;

  //-- A Vector of String names of variable symbols seen so far in the domain.
  private Vector<String> vars;

  //-- To store the maximum number of the variables seen in any variable scope.
  private int varsMaxSize;

  //-- The function to initialize this object. It must be called right after
  //-- the constructor.
  public void initialize(JSHOP2Lexer lexerIn, InternalDomain domainIn)
  {
    lexer = lexerIn;
    domain = domainIn;

    vars = new Vector<String>();
    varsMaxSize = 0;
  }

protected JSHOP2Parser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public JSHOP2Parser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected JSHOP2Parser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public JSHOP2Parser(TokenStream lexer) {
  this(lexer,2);
}

public JSHOP2Parser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void command() throws RecognitionException, TokenStreamException, IOException {
		
		
		prob();
	}
	
	public final void prob() throws RecognitionException, TokenStreamException, IOException {
		
		Token  pn = null;
		Token  dn = null;
		
		match(LP);
		match(DEFPROBLEM);
		pn = LT(1);
		match(ID);
		dn = LT(1);
		match(ID);
		
		//-- Set the problem and domain names.
		domain.setProbName(new String(pn.getText().toLowerCase()));
		domain.setName(new String(dn.getText().toLowerCase()));
		
		//-- Initialize the necessary data structures to compile the command,
		//-- e.g. read the names from the text file associated with this domain.
		domain.commandInitialize();
		
		//-- The list of initial world states, one for each planning problem in
		//-- this domain.
		LinkedList<Vector<Predicate>> states = new LinkedList<Vector<Predicate>>();
		
		//-- The list of task lists, one for each planning problem in this
		//-- domain.
		LinkedList<TaskList> taskLists = new LinkedList<TaskList>();
		
		{
		int _cnt8=0;
		_loop8:
		do {
			if ((LA(1)==LP||LA(1)==NIL)) {
				
				//-- The next atom to be added to the initial state of the world.
				Predicate p;
				
				//-- The initial state of the world as specified in the problem
				//-- description.
				Vector<Predicate> state = new Vector<Predicate>();
				
				//-- The task list to be achieved as specified in the problem
				//-- description.
				TaskList tList;
				
				{
				switch ( LA(1)) {
				case LP:
				{
					{
					match(LP);
					{
					_loop7:
					do {
						if ((LA(1)==LP||LA(1)==VARID)) {
							p=la();
							
							//-- Atoms in the initial state of the world must be ground.
							if (!p.isGround())
							{
							System.out.println("\nERROR: The atoms in the initial state of the "
							+ "world must be ground, non-ground atom found in line: "
							+ lexer.getLine() + "\n");
							System.exit(2);
							}
							
							//-- Add the read atom to the initial state of the world.
							state.add(p);
							
						}
						else {
							break _loop7;
						}
						
					} while (true);
					}
					match(RP);
					}
					break;
				}
				case NIL:
				{
					match(NIL);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				tList=tl();
				
				states.add(state);
				taskLists.add(tList);
				
			}
			else {
				if ( _cnt8>=1 ) { break _loop8; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt8++;
		} while (true);
		}
		match(RP);
		
		//-- Convert the command to Java code and write it.
		domain.commandToCode(states, taskLists);
		
	}
	
	public final Predicate  la() throws RecognitionException, TokenStreamException {
		Predicate retVal;
		
		Token  pn = null;
		Token  var = null;
		
		//-- The argument list of the logical atom.
		List l;
		
		
		switch ( LA(1)) {
		case LP:
		{
			match(LP);
			pn = LT(1);
			match(ID);
			l=terml();
			
			//-- Add the constant symbol to the list of constant symbols in the
			//-- domain.
			int index = domain.addConstant(pn.getText().toLowerCase());
			
			//-- Create the logical atom.
			retVal = new Predicate(index, vars.size(), new TermList(l));
			
			match(RP);
			break;
		}
		case VARID:
		{
			var = LT(1);
			match(VARID);
			
			//-- Create the variable predicate (i.e., a predicate that is only a
			//-- variable at compile time, but will be bound to a predicate at run
			//-- time.
			retVal = new Predicate(vars.indexOf(var.getText().toLowerCase()),
			vars.size());
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return retVal;
	}
	
	public final TaskList  tl() throws RecognitionException, TokenStreamException {
		TaskList retVal;
		
		
		//-- Whether or not the task list is ordered.
		boolean ordered = true;
		
		//-- The subtasks of the task list.
		Vector<TaskList> subtasks = new Vector<TaskList>();
		
		//-- The current child of the task list, in case it is an atomic task list.
		TaskAtom tAtom;
		
		//-- The current child of the task list, in case it is a list itself.
		TaskList tList;
		
		
		switch ( LA(1)) {
		case LP:
		{
			match(LP);
			{
			switch ( LA(1)) {
			case UNORDERED:
			{
				match(UNORDERED);
				
				//-- This is an unordered task list.
				ordered = false;
				
				break;
			}
			case LP:
			case RP:
			case NIL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop38:
			do {
				if ((LA(1)==LP||LA(1)==NIL) && (_tokenSet_0.member(LA(2)))) {
					tList=tl();
					
					//-- The next child of the task list is a list itself.
					subtasks.add(tList);
					
				}
				else if ((LA(1)==LP) && (_tokenSet_1.member(LA(2)))) {
					tAtom=ta();
					
					//-- The next child of the task list is an atomic task.
					subtasks.add(new TaskList(tAtom));
					
				}
				else {
					break _loop38;
				}
				
			} while (true);
			}
			match(RP);
			
			//-- Create the object that represents this task list.
			retVal = TaskList.createTaskList(subtasks, ordered);
			
			break;
		}
		case NIL:
		{
			match(NIL);
			
			//-- Empty task list.
			retVal = TaskList.empty;
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return retVal;
	}
	
	public final void domain() throws RecognitionException, TokenStreamException, IOException {
		
		Token  dn = null;
		
		
		String str;
		
		match(LP);
		match(DEFDOMAIN);
		dn = LT(1);
		match(ID);
		
		//-- Set the domain name.
		domain.setName(new String(dn.getText().toLowerCase()));
		
		match(LP);
		{
		int _cnt11=0;
		_loop11:
		do {
			if ((LA(1)==LP)) {
				pde();
			}
			else {
				if ( _cnt11>=1 ) { break _loop11; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt11++;
		} while (true);
		}
		
		//-- Convert the domain description to Java code and write it.
		domain.close(varsMaxSize);
		
		match(RP);
		match(RP);
	}
	
	public final void pde() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==LP) && (LA(2)==METHOD)) {
			method();
		}
		else if ((LA(1)==LP) && (LA(2)==OPERATOR)) {
			op();
		}
		else if ((LA(1)==LP) && (LA(2)==AXIOM)) {
			axiom();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void method() throws RecognitionException, TokenStreamException {
		
		Token  mn = null;
		Token  bn = null;
		
		
		//-- The current branch label.
		String branchLabel;
		
		//-- A Vector of String labels of branches.
		Vector<String> labels = new Vector<String>();
		
		//-- The logical precondition of the current branch of the method.
		LogicalPrecondition lPre;
		
		//-- A Vector of logical preconditions, one for each branch of the method.
		Vector<LogicalPrecondition> pres = new Vector<LogicalPrecondition>();
		
		//-- The task decomposition the current branch of the method represents.
		TaskList sub;
		
		//-- A Vector of possible task decompositions, one for each branch of the
		//-- method.
		Vector<TaskList> subs = new Vector<TaskList>();
		
		//-- The argument list of the head of the method.
		List tn;
		
		match(LP);
		match(METHOD);
		match(LP);
		mn = LT(1);
		match(ID);
		tn=terml();
		match(RP);
		{
		int _cnt16=0;
		_loop16:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				
				//-- Generate a default label for this branch.
				branchLabel = "Method" + domain.getMethodNo() + "Branch" + labels.size();
				
				{
				switch ( LA(1)) {
				case ID:
				{
					bn = LT(1);
					match(ID);
					
					branchLabel = bn.getText();
					
					break;
				}
				case LP:
				case NIL:
				case VARID:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				lPre=lp();
				sub=tl();
				
				//-- Add the label, the precondition, and the task decomposition
				//-- of this branch to the appropriate Vectors.
				labels.add(branchLabel);
				pres.add(lPre);
				subs.add(sub);
				
			}
			else {
				if ( _cnt16>=1 ) { break _loop16; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt16++;
		} while (true);
		}
		
		//-- Add the compound task this method decomposes to the list of compound
		//-- tasks in the domain.
		int index = domain.addCompoundTask(mn.getText().toLowerCase());
		
		//-- Create the head of the method.
		Predicate p = new Predicate(index, vars.size(), new TermList(tn));
		
		//-- Create the object that represents the method, and add it to the list
		//-- of the methods in the domain.
		domain.addMethod(new InternalMethod(p, labels, pres, subs));
		
		//-- The scope for the variables in a method is within that method, so as
		//-- soon as we get out of the method body we should empty our list of
		//-- variables after updating the value of 'varsMaxSize'.
		if (vars.size() > varsMaxSize)
		varsMaxSize = vars.size();
		
		vars.clear();
		
		match(RP);
	}
	
	public final void op() throws RecognitionException, TokenStreamException {
		
		Token  on = null;
		
		
		//-- The add list of the operator.
		Vector add;
		
		//-- Cost of the operator.
		Term cost = new TermNumber(1.0);
		
		//-- The delete list of the operator.
		Vector del;
		
		//-- The logical precondition of the operator.
		LogicalPrecondition pre;
		
		//-- The argument list of the head of the operator.
		List tn;
		
		match(LP);
		match(OPERATOR);
		match(LP);
		on = LT(1);
		match(OPID);
		tn=terml();
		match(RP);
		pre=lp();
		del=da();
		add=da();
		{
		switch ( LA(1)) {
		case LP:
		case ID:
		case NIL:
		case VARID:
		case NUM:
		{
			cost=term();
			break;
		}
		case RP:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
		//-- Add the primtive task this operator can achieve to the list of
		//-- primitive tasks in the domain.
		int index = domain.addPrimitiveTask(on.getText().toLowerCase());
		
		//-- Create the head of the operator.
		Predicate p = new Predicate(index, vars.size(), new TermList(tn));
		
		//-- Create the object that represents the operator, and add it to the
		//-- list of the operators in the domain.
		domain.addOperator(new InternalOperator(p, pre, del, add, cost));
		
		//-- The scope for the variables in an operator is within that operator,
		//-- so as soon as we get out of the operator body we should empty our
		//-- list of variables after updating the value of 'varsMaxSize'.
		if (vars.size() > varsMaxSize)
		varsMaxSize = vars.size();
		
		vars.clear();
		
		match(RP);
	}
	
	public final void axiom() throws RecognitionException, TokenStreamException {
		
		Token  ah = null;
		Token  bn = null;
		
		
		//-- A Vector each element of which represents one branch of the axiom,
		//-- (i.e., one way to prove its head).
		Vector<LogicalPrecondition> a = new Vector<LogicalPrecondition>();
		
		//-- The current branch label.
		String branchLabel;
		
		//-- A Vector of String labels of branches.
		Vector<String> labels = new Vector<String>();
		
		//-- The current branch of the axiom being considered.
		LogicalPrecondition lPre;
		
		//-- The head of the axiom (i.e., the predicate it can be used to prove).
		Predicate p;
		
		match(LP);
		ah = LT(1);
		match(AXIOM);
		p=la();
		{
		int _cnt34=0;
		_loop34:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				
				//-- Generate a default label for this branch.
				branchLabel = "Axiom" + domain.getAxiomNo() + "Branch" + labels.size();
				
				{
				switch ( LA(1)) {
				case ID:
				{
					bn = LT(1);
					match(ID);
					
					branchLabel = bn.getText();
					
					break;
				}
				case LP:
				case NIL:
				case VARID:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				lPre=lp();
				
				//-- Add the current branch of the axiom to the list of its branches
				//-- and its label to the list of labels.
				a.add(lPre);
				labels.add(branchLabel);
				
			}
			else {
				if ( _cnt34>=1 ) { break _loop34; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt34++;
		} while (true);
		}
		
		p.setVarCount(vars.size());
		
		//-- Create the object that represents the axiom, and add it to the list
		//-- of the axioms in the domain.
		domain.addAxiom(new InternalAxiom(p, a, labels));
		
		//-- The scope for the variables in an axiom is within that axiom, so as
		//-- soon as we get out of the operator body we should empty our list of
		//-- variables after updating the value of 'varsMaxSize'.
		if (vars.size() > varsMaxSize)
		varsMaxSize = vars.size();
		
		vars.clear();
		
		match(RP);
	}
	
	public final List  terml() throws RecognitionException, TokenStreamException {
		List retVal;
		
		
		
		//-- A LinkedList to store the terms in the term list.
		LinkedList<Term> list = new LinkedList<Term>();
		
		//-- The current term.
		Term tn;
		
		{
		_loop57:
		do {
			if ((_tokenSet_3.member(LA(1)))) {
				tn=term();
				
				//-- Add the current term to the list of terms seen so far.
				list.addFirst(tn);
				
			}
			else {
				break _loop57;
			}
			
		} while (true);
		}
		
		//-- Create the object that represents this term list.
		retVal = List.MakeList(list);
		
		return retVal;
	}
	
	public final LogicalPrecondition  lp() throws RecognitionException, TokenStreamException {
		LogicalPrecondition retVal;
		
		Token  vn = null;
		
		//-- The logical expression associated with this logical precondition.
		LogicalExpression lExp;
		
		//-- The name of the function used in a :sort-by logical precondition.
		String func = null;
		
		
		if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
			lExp=le();
			
			//-- Create the object that represents this logical precondition.
			retVal = new LogicalPrecondition(lExp, false);
			
		}
		else if ((LA(1)==LP) && (LA(2)==FIRST)) {
			match(LP);
			match(FIRST);
			lExp=le();
			match(RP);
			
			//-- Create the object that represents this logical precondition.
			retVal = new LogicalPrecondition(lExp, true);
			
		}
		else if ((LA(1)==LP) && (LA(2)==SORT)) {
			match(LP);
			match(SORT);
			vn = LT(1);
			match(VARID);
			{
			switch ( LA(1)) {
			case ID:
			case DIV:
			case EQUAL:
			case LESS:
			case LESSEQ:
			case MEMBER:
			case MINUS:
			case MORE:
			case MOREEQ:
			case MULT:
			case NOTEQ:
			case PLUS:
			case POWER:
			{
				func=fid();
				break;
			}
			case LP:
			case NIL:
			case VARID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			lExp=le();
			match(RP);
			
			String s = vn.getText().toLowerCase();
			
			//-- Add the variable to the variable list.
			if (!vars.contains(s))
			vars.add(s);
			
			//-- If no function is specified, use '<' as the default function.
			if (func == null || func.equals("StdLib.less"))
			func = "new CompLess(" + vars.indexOf(s) + ")";
			else if (func.equals("StdLib.more"))
			func = "new CompMore(" + vars.indexOf(s) + ")";
			else
			func = "new " + func + "(" + vars.indexOf(s) + ")";
			
			//-- Create the object that represents this logical precondition.
			retVal = new LogicalPrecondition(lExp, func);
			
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		return retVal;
	}
	
	public final Vector  da() throws RecognitionException, TokenStreamException {
		Vector retVal;
		
		Token  var = null;
		
		//-- The current delete/add element.
		DelAddElement delAdd = null;
		
		//-- The return value, a Vector of delete/add elements.
		retVal = new Vector();
		
		//-- The first element is set to NULL, meaning that this delete/add list is
		//-- a real list of predicates, and not a variable.
		retVal.add(null);
		
		
		switch ( LA(1)) {
		case LP:
		{
			match(LP);
			{
			_loop21:
			do {
				if ((LA(1)==LP||LA(1)==VARID)) {
					delAdd=dae();
					
					//-- Add the next delete/add element to the delete/add list.
					retVal.add(delAdd);
					
				}
				else {
					break _loop21;
				}
				
			} while (true);
			}
			match(RP);
			break;
		}
		case VARID:
		{
			var = LT(1);
			match(VARID);
			
			//-- In case the delete/add list is a variable (to be instantiated to a
			//-- list of predicates at run time) and not a real list, just clear the
			//-- Vector (of its first element set to NULL) to signal this, and add
			//-- the index of the variable to the Vector.
			retVal.clear();
			retVal.add(new Integer(vars.indexOf(var.getText().toLowerCase())));
			
			break;
		}
		case NIL:
		{
			match(NIL);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return retVal;
	}
	
	public final Term  term() throws RecognitionException, TokenStreamException {
		Term retVal;
		
		Token  vn = null;
		Token  in = null;
		Token  num = null;
		
		//-- The name of the function called in a call term.
		String func;
		
		//-- The term list.
		List list;
		
		//-- The current term.
		Term tn;
		
		retVal = null;
		
		
		switch ( LA(1)) {
		case VARID:
		{
			vn = LT(1);
			match(VARID);
			
			//-- Add the variable symbol to the variable list.
			if (!vars.contains(vn.getText().toLowerCase()))
			vars.add(vn.getText().toLowerCase());
			
			//-- Create the object that represents this variable symbol.
			retVal = new TermVariable(vars.indexOf(vn.getText().toLowerCase()));
			
			break;
		}
		case ID:
		{
			in = LT(1);
			match(ID);
			
			//-- Add the constant symbol to the list of constant symbols in the
			//-- domain.
			int index = domain.addConstant(in.getText().toLowerCase());
			
			//-- Create the object that represents this constant symbol.
			retVal = new TermConstant(index);
			
			break;
		}
		case NUM:
		{
			num = LT(1);
			match(NUM);
			
			//-- Create the object that represents this numerical term.
			retVal = new TermNumber(Double.parseDouble(num.getText().toLowerCase()));
			
			break;
		}
		case NIL:
		{
			match(NIL);
			
			//-- Empty list term.
			retVal = TermList.NIL;
			
			break;
		}
		default:
			if ((LA(1)==LP) && (_tokenSet_6.member(LA(2)))) {
				match(LP);
				list=terml();
				{
				switch ( LA(1)) {
				case DOT:
				{
					match(DOT);
					tn=term();
					
					//-- Append the current term to the end of the term list.
					retVal = new TermList(list.append(tn));
					
					break;
				}
				case RP:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				
				//-- If retVal is not already created, create it as a list term.
				if (retVal == null)
				retVal = new TermList(list);
				
				match(RP);
			}
			else if ((LA(1)==LP) && (LA(2)==CALL)) {
				match(LP);
				match(CALL);
				func=fid();
				list=terml();
				match(RP);
				
				//-- If this function call is not one implemented in the standard
				//-- library, add it to the list of user-defined external code calls.
				if (!func.startsWith("StdLib."))
				{
				domain.addCalc(func);
				func = domain.getName() + ".calculate" + func;
				}
				
				//-- Create the object that represents this call term.
				retVal = new TermCall(list, func);
				
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return retVal;
	}
	
	public final DelAddElement  dae() throws RecognitionException, TokenStreamException {
		DelAddElement retVal;
		
		
		//-- The logical atom to be deleted, added, protected or unprotected.
		Predicate p;
		
		
		if ((LA(1)==LP||LA(1)==VARID) && (_tokenSet_7.member(LA(2)))) {
			p=la();
			
			//-- The atomic delete/add element.
			retVal = new DelAddAtomic(p);
			
		}
		else if ((LA(1)==LP) && (LA(2)==PROTECTION)) {
			match(LP);
			match(PROTECTION);
			p=la();
			match(RP);
			
			//-- The protection delete/add element.
			retVal = new DelAddProtection(p);
			
		}
		else if ((LA(1)==LP) && (LA(2)==FORALL)) {
			match(LP);
			match(FORALL);
			
			//-- The precondition of the ForAll delete/add element.
			LogicalExpression exp;
			
			//-- A Vector of atoms to be deleted/added under this ForAll delete/add
			//-- element.
			Vector<Predicate> atoms = new Vector<Predicate>();
			
			{
			switch ( LA(1)) {
			case LP:
			{
				{
				match(LP);
				{
				_loop26:
				do {
					if ((LA(1)==VARID)) {
						match(VARID);
					}
					else {
						break _loop26;
					}
					
				} while (true);
				}
				match(RP);
				}
				break;
			}
			case NIL:
			{
				match(NIL);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			exp=le();
			{
			switch ( LA(1)) {
			case LP:
			{
				{
				match(LP);
				{
				_loop30:
				do {
					if ((LA(1)==LP||LA(1)==VARID)) {
						p=la();
						
						//-- Add the current predicate to the list of atoms to be
						//-- deleted/added under this ForAll delete/add element.
						atoms.add(p);
						
					}
					else {
						break _loop30;
					}
					
				} while (true);
				}
				match(RP);
				}
				break;
			}
			case NIL:
			{
				match(NIL);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
			//-- Create the delete/add element and return it.
			retVal = new DelAddForAll(exp, atoms);
			
			match(RP);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		return retVal;
	}
	
	public final LogicalExpression  le() throws RecognitionException, TokenStreamException {
		LogicalExpression retVal;
		
		Token  vn = null;
		
		//-- A Vector of conjuncts/disjuncts.
		Vector<LogicalExpression> vec = new Vector<LogicalExpression>();
		
		//-- The name of the function called in a call term.
		String func;
		
		//-- The current logical expression.
		LogicalExpression lExp, lExp2;
		
		//-- The logical atom, in case logical expression is an atomic one.
		Predicate p;
		
		//-- The argument list of the function call.
		List param;
		
		//-- The term a variable is assigned to in an assign term.
		Term t;
		
		
		if ((LA(1)==NIL)) {
			match(NIL);
			
			//-- Empty logical expression.
			retVal = new LogicalExpressionNil();
			
		}
		else if ((LA(1)==LP) && (_tokenSet_8.member(LA(2)))) {
			match(LP);
			{
			switch ( LA(1)) {
			case AND:
			{
				match(AND);
				break;
			}
			case LP:
			case RP:
			case NIL:
			case VARID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop47:
			do {
				if ((_tokenSet_4.member(LA(1)))) {
					lExp=le();
					
					//-- Add the current conjunct to the list of conjuncts.
					vec.add(lExp);
					
				}
				else {
					break _loop47;
				}
				
			} while (true);
			}
			
			//-- If there are no conjuncts, return an empty logical expression.
			if (vec.size() == 0)
			retVal = new LogicalExpressionNil();
			//-- If there is only one conjunct, return an atomic logical expression.
			else if (vec.size() == 1)
			retVal = vec.get(0);
			//-- If there are more than one conjuncts, return a conjunction.
			else
			retVal = new LogicalExpressionConjunction(vec);
			
			match(RP);
		}
		else if ((LA(1)==LP) && (LA(2)==OR)) {
			match(LP);
			match(OR);
			{
			int _cnt49=0;
			_loop49:
			do {
				if ((_tokenSet_4.member(LA(1)))) {
					lExp=le();
					
					//-- Add the current disjunct to the list of disjuncts.
					vec.add(lExp);
					
				}
				else {
					if ( _cnt49>=1 ) { break _loop49; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt49++;
			} while (true);
			}
			
			//-- If there is only one disjunct, return an atomic logical expression.
			if (vec.size() == 1)
			retVal = vec.get(0);
			//-- If there are more than one disjuncts, return a disjunction.
			else
			retVal = new LogicalExpressionDisjunction(vec);
			
			match(RP);
		}
		else if ((LA(1)==LP) && (LA(2)==NOT)) {
			match(LP);
			match(NOT);
			lExp=le();
			match(RP);
			
			//-- A negative logical expression.
			retVal = new LogicalExpressionNegation(lExp);
			
		}
		else if ((LA(1)==LP) && (LA(2)==IMPLY)) {
			match(LP);
			match(IMPLY);
			lExp=le();
			lExp2=le();
			match(RP);
			
			//-- A logical implication.
			
			//-- To hold the disjuncts of the disjunction equivalent to this logical
			//-- implication.
			Vector<LogicalExpression> disjunction = new Vector<LogicalExpression>();
			
			//-- The first disjunct is the negation of the premise of the implication.
			disjunction.add(new LogicalExpressionNegation(lExp));
			
			//-- The second disjunct is the consequence of the implication.
			disjunction.add(lExp2);
			
			//-- Each implication is equivalent to the disjunction of the negation of
			//-- its premise and its consequence.
			retVal = new LogicalExpressionDisjunction(disjunction);
			
		}
		else if ((LA(1)==LP||LA(1)==VARID) && (_tokenSet_9.member(LA(2)))) {
			p=la();
			
			//-- An atomic logical expression.
			retVal = new LogicalExpressionAtomic(p);
			
		}
		else if ((LA(1)==LP) && (LA(2)==FORALL)) {
			match(LP);
			match(FORALL);
			{
			switch ( LA(1)) {
			case LP:
			{
				{
				match(LP);
				{
				_loop53:
				do {
					if ((LA(1)==VARID)) {
						match(VARID);
					}
					else {
						break _loop53;
					}
					
				} while (true);
				}
				match(RP);
				}
				break;
			}
			case NIL:
			{
				match(NIL);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			lExp=le();
			lExp2=le();
			match(RP);
			
			//-- A ForAll logical expression
			retVal = new LogicalExpressionForAll(lExp, lExp2);
			
		}
		else if ((LA(1)==LP) && (LA(2)==ASSIGN)) {
			match(LP);
			match(ASSIGN);
			vn = LT(1);
			match(VARID);
			t=term();
			match(RP);
			
			String s = vn.getText().toLowerCase();
			
			//-- Add the variable to the variable list.
			if (!vars.contains(s))
			vars.add(s);
			
			//-- An assigment logical expression.
			retVal = new LogicalExpressionAssignment(vars.indexOf(s), t);
			
		}
		else if ((LA(1)==LP) && (LA(2)==CALL)) {
			match(LP);
			match(CALL);
			func=fid();
			param=terml();
			match(RP);
			
			//-- If this function call is not one implemented in the standard
			//-- library, add it to the list of user-defined external code calls.
			if (!func.startsWith("StdLib."))
			{
			domain.addCalc(func);
			func = domain.getName() + ".calculate" + func;
			}
			
			//-- A call logical expression.
			retVal = new LogicalExpressionCall(new TermCall(param, func));
			
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		return retVal;
	}
	
	public final TaskAtom  ta() throws RecognitionException, TokenStreamException {
		TaskAtom retVal;
		
		Token  ctn = null;
		Token  stn = null;
		
		//-- Whether or not this is an immediate task atom.
		boolean immediate = false;
		
		//-- Whether or not this is a primitive task atom.
		boolean isPrimitive;
		
		//-- The argument list of this task atom.
		List param;
		
		//-- The index of the head of this task atom.
		int tn;
		
		
		match(LP);
		{
		switch ( LA(1)) {
		case IMMEDIATE:
		{
			match(IMMEDIATE);
			
			//-- This is an immediate task.
			immediate = true;
			
			break;
		}
		case ID:
		case OPID:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case ID:
		{
			ctn = LT(1);
			match(ID);
			
			//-- Add this compound task to the list of compound tasks in the
			//-- domain.
			tn = domain.addCompoundTask(ctn.getText().toLowerCase());
			isPrimitive = false;
			
			break;
		}
		case OPID:
		{
			stn = LT(1);
			match(OPID);
			
			//-- Add this primitive task to the list of primitive tasks in the
			//-- domain.
			tn = domain.addPrimitiveTask(stn.getText().toLowerCase());
			isPrimitive = true;
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		param=terml();
		
		//-- Create the object that represents this task atom.
		retVal = new TaskAtom(
		new Predicate(tn,
		vars.size(),
		new TermList(param)),
		immediate,
		isPrimitive);
		
		match(RP);
		return retVal;
	}
	
	public final String  fid() throws RecognitionException, TokenStreamException {
		String retVal;
		
		Token  id = null;
		
		switch ( LA(1)) {
		case ID:
		{
			id = LT(1);
			match(ID);
			
			retVal = id.getText();
			
			break;
		}
		case DIV:
		{
			match(DIV);
			
			retVal = "StdLib.div";
			
			break;
		}
		case EQUAL:
		{
			match(EQUAL);
			
			retVal = "StdLib.equal";
			
			break;
		}
		case LESS:
		{
			match(LESS);
			
			retVal = "StdLib.less";
			
			break;
		}
		case LESSEQ:
		{
			match(LESSEQ);
			
			retVal = "StdLib.lessEq";
			
			break;
		}
		case MEMBER:
		{
			match(MEMBER);
			
			retVal = "StdLib.member";
			
			break;
		}
		case MINUS:
		{
			match(MINUS);
			
			retVal = "StdLib.minus";
			
			break;
		}
		case MORE:
		{
			match(MORE);
			
			retVal = "StdLib.more";
			
			break;
		}
		case MOREEQ:
		{
			match(MOREEQ);
			
			retVal = "StdLib.moreEq";
			
			break;
		}
		case MULT:
		{
			match(MULT);
			
			retVal = "StdLib.mult";
			
			break;
		}
		case NOTEQ:
		{
			match(NOTEQ);
			
			retVal = "StdLib.notEq";
			
			break;
		}
		case PLUS:
		{
			match(PLUS);
			
			retVal = "StdLib.plus";
			
			break;
		}
		case POWER:
		{
			match(POWER);
			
			retVal = "StdLib.power";
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return retVal;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"LP",
		"\"defproblem\"",
		"ID",
		"RP",
		"\"nil\"",
		"\"defdomain\"",
		"METHOD",
		"OPERATOR",
		"OPID",
		"VARID",
		"PROTECTION",
		"\"forall\"",
		"AXIOM",
		"UNORDERED",
		"IMMEDIATE",
		"FIRST",
		"SORT",
		"\"and\"",
		"\"or\"",
		"\"not\"",
		"\"imply\"",
		"\"assign\"",
		"\"call\"",
		"NUM",
		"DOT",
		"DIV",
		"EQUAL",
		"LESS",
		"LESSEQ",
		"\"member\"",
		"MINUS",
		"MORE",
		"MOREEQ",
		"MULT",
		"NOTEQ",
		"PLUS",
		"POWER",
		"\"def-problem-set\"",
		"\"stdlib\"",
		"WS",
		"COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 131472L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 266304L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 8528L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 134226256L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 8464L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 132162000L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 402661840L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 8400L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 2105744L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 8656L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	
	}
