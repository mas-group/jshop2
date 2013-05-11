//------------------------------- Header ----------------------------

header
{
  package JSHOP2;

  import java.io.IOException;
  import java.util.LinkedList;
  import java.util.Vector;
}

//------------------------------- Parser ----------------------------

class JSHOP2Parser extends Parser;

//------------------------------- Options ---------------------------

options
{
  k = 2;
  exportVocab = JSHOP2;
  codeGenMakeSwitchThreshold = 2;
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;
  buildAST = false;
}

//------------------------------- Parser Code -----------------------

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
}

//------------------------------- Grammar ---------------------------

//------------------------------- Command ---------------------------

//-- TODO: Other possible commands
command throws IOException :
  prob
;

//------------------------------- Problem ---------------------------

prob throws IOException :
  LP DEFPROBLEM pn:ID dn:ID
    {
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
    }
  (
    {
      //-- The next atom to be added to the initial state of the world.
      Predicate p;

      //-- The initial state of the world as specified in the problem
      //-- description.
      Vector<Predicate> state = new Vector<Predicate>();

      //-- The task list to be achieved as specified in the problem
      //-- description.
      TaskList tList;
    }
    (
      (LP
        (p = la
          {
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
        )*
      RP)
    |
      NIL
    ) tList = tl
    {
      states.add(state);
      taskLists.add(tList);
    }
  )+
  RP
    {
      //-- Convert the command to Java code and write it.
      domain.commandToCode(states, taskLists);
    }
;

//------------------------------- Domain ----------------------------

//-- Domain
domain throws IOException :
  {
    String str;
  }
  LP DEFDOMAIN dn:ID
    {
      //-- Set the domain name.
      domain.setName(new String(dn.getText().toLowerCase()));
    }
    LP (pde)+
      {
        //-- Convert the domain description to Java code and write it.
        domain.close(varsMaxSize);
      }
    RP
  RP
;

//-- Planning Domain Element
pde :
  method
|
  op
|
  axiom
;

//-------------------------------- Method ---------------------------

method :
  {
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
  }
  LP METHOD LP mn:ID tn = terml RP
    (
      {
        //-- Generate a default label for this branch.
        branchLabel = "Method" + domain.getMethodNo() + "Branch" + labels.size();
      }
      (
        bn:ID
          {
            branchLabel = bn.getText();
          }
      )?
      lPre = lp sub = tl
      {
        //-- Add the label, the precondition, and the task decomposition
        //-- of this branch to the appropriate Vectors.
        labels.add(branchLabel);
        pres.add(lPre);
        subs.add(sub);
      }
    )+
    {
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
    }
  RP
;

//-------------------------------- Operator -------------------------

op :
  {
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
  }
  LP OPERATOR LP on:OPID tn = terml RP pre = lp del = da add = da
    (
      cost = term
    )?
    {
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
    }
  RP
;

//-- Delete/Add
da returns [Vector retVal]
  {
    //-- The current delete/add element.
    DelAddElement delAdd = null;

    //-- The return value, a Vector of delete/add elements.
    retVal = new Vector();

    //-- The first element is set to NULL, meaning that this delete/add list is
    //-- a real list of predicates, and not a variable.
    retVal.add(null);
  }
:
  LP
    (delAdd = dae
      {
        //-- Add the next delete/add element to the delete/add list.
        retVal.add(delAdd);
      }
    )*
  RP
|
  var:VARID
    {
      //-- In case the delete/add list is a variable (to be instantiated to a
      //-- list of predicates at run time) and not a real list, just clear the
      //-- Vector (of its first element set to NULL) to signal this, and add
      //-- the index of the variable to the Vector.
      retVal.clear();
      retVal.add(new Integer(vars.indexOf(var.getText().toLowerCase())));
    }
|
  NIL
;

//-- Delete/Add Element
dae returns [DelAddElement retVal]
  {
    //-- The logical atom to be deleted, added, protected or unprotected.
    Predicate p;
  }
:
  p = la
    {
      //-- The atomic delete/add element.
      retVal = new DelAddAtomic(p);
    }
|
  LP PROTECTION p = la RP
    {
      //-- The protection delete/add element.
      retVal = new DelAddProtection(p);
    }
|
  LP FORALL
    {
      //-- The precondition of the ForAll delete/add element.
      LogicalExpression exp;

      //-- A Vector of atoms to be deleted/added under this ForAll delete/add
      //-- element.
      Vector<Predicate> atoms = new Vector<Predicate>();
    }
  (
    (LP (VARID)* RP)
  |
    NIL
  )
  exp = le
  (
    (
      LP
        (
          p = la
            {
              //-- Add the current predicate to the list of atoms to be
              //-- deleted/added under this ForAll delete/add element.
              atoms.add(p);
            }
        )*
      RP
    )
  |
    NIL
  )
    {
      //-- Create the delete/add element and return it.
      retVal = new DelAddForAll(exp, atoms);
    }
  RP
;

//-------------------------------- Axiom ----------------------------

axiom :
  {
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
  }
  LP ah:AXIOM p = la
    (
      {
        //-- Generate a default label for this branch.
        branchLabel = "Axiom" + domain.getAxiomNo() + "Branch" + labels.size();
      }
      (
        bn:ID
          {
            branchLabel = bn.getText();
          }
      )?
      lPre = lp
        {
          //-- Add the current branch of the axiom to the list of its branches
          //-- and its label to the list of labels.
          a.add(lPre);
          labels.add(branchLabel);
        }
    )+
    {
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
    }
  RP
;

//------------------------------- Task ------------------------------

//-- Task List
tl returns [TaskList retVal]
  {
    //-- Whether or not the task list is ordered.
    boolean ordered = true;

    //-- The subtasks of the task list.
    Vector<TaskList> subtasks = new Vector<TaskList>();

    //-- The current child of the task list, in case it is an atomic task list.
    TaskAtom tAtom;

    //-- The current child of the task list, in case it is a list itself.
    TaskList tList;
  }
:
  LP
    (
      UNORDERED
        {
          //-- This is an unordered task list.
          ordered = false;
        }
    )?
    (
      tList = tl
        {
          //-- The next child of the task list is a list itself.
          subtasks.add(tList);
        }
    |
      tAtom = ta
        {
          //-- The next child of the task list is an atomic task.
          subtasks.add(new TaskList(tAtom));
        }
    )*
  RP
    {
      //-- Create the object that represents this task list.
      retVal = TaskList.createTaskList(subtasks, ordered);
    }
|
  NIL
    {
      //-- Empty task list.
      retVal = TaskList.empty;
    }
;

//-- Task Atom
ta returns [TaskAtom retVal]
  {
    //-- Whether or not this is an immediate task atom.
    boolean immediate = false;

    //-- Whether or not this is a primitive task atom.
    boolean isPrimitive;

    //-- The argument list of this task atom.
    List param;

    //-- The index of the head of this task atom.
    int tn;
  }
:
  LP
    (
      IMMEDIATE
        {
          //-- This is an immediate task.
          immediate = true;
        }
    )?
    (
      ctn:ID
        {
          //-- Add this compound task to the list of compound tasks in the
          //-- domain.
          tn = domain.addCompoundTask(ctn.getText().toLowerCase());
          isPrimitive = false;
        }
    |
      stn:OPID
        {
          //-- Add this primitive task to the list of primitive tasks in the
          //-- domain.
          tn = domain.addPrimitiveTask(stn.getText().toLowerCase());
          isPrimitive = true;
        }
    )
    param = terml
      {
        //-- Create the object that represents this task atom.
        retVal = new TaskAtom(
                              new Predicate(tn,
                                            vars.size(),
                                            new TermList(param)),
                              immediate,
                              isPrimitive);
      }
  RP
;

//------------------------------- Logical precondition --------------

//-- Logical Precondition
lp returns [LogicalPrecondition retVal]
  {
    //-- The logical expression associated with this logical precondition.
    LogicalExpression lExp;

    //-- The name of the function used in a :sort-by logical precondition.
    String func = null;
  }
:
  lExp = le
    {
      //-- Create the object that represents this logical precondition.
      retVal = new LogicalPrecondition(lExp, false);
    }
|
  LP FIRST lExp = le RP
    {
      //-- Create the object that represents this logical precondition.
      retVal = new LogicalPrecondition(lExp, true);
    }
|
  LP SORT vn:VARID (func = fid)? lExp = le RP
    {
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
;

//-- Logical Expression
le returns [LogicalExpression retVal]
  {
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
  }
:
  NIL
    {
      //-- Empty logical expression.
      retVal = new LogicalExpressionNil();
    }
|
  LP
    (AND)?
    (
      lExp = le
        {
          //-- Add the current conjunct to the list of conjuncts.
          vec.add(lExp);
        }
    )*
    {
      //-- If there are no conjuncts, return an empty logical expression.
      if (vec.size() == 0)
        retVal = new LogicalExpressionNil();
      //-- If there is only one conjunct, return an atomic logical expression.
      else if (vec.size() == 1)
        retVal = vec.get(0);
      //-- If there are more than one conjuncts, return a conjunction.
      else
        retVal = new LogicalExpressionConjunction(vec);
    }
  RP
|
  LP
    OR
    (
      lExp = le
        {
          //-- Add the current disjunct to the list of disjuncts.
          vec.add(lExp);
        }
    )+
    {
      //-- If there is only one disjunct, return an atomic logical expression.
      if (vec.size() == 1)
        retVal = vec.get(0);
      //-- If there are more than one disjuncts, return a disjunction.
      else
        retVal = new LogicalExpressionDisjunction(vec);
    }
  RP
|
  LP NOT lExp = le RP
    {
      //-- A negative logical expression.
      retVal = new LogicalExpressionNegation(lExp);
    }
|
  LP IMPLY lExp = le lExp2 = le RP
  {
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
|
  p = la
  {
    //-- An atomic logical expression.
    retVal = new LogicalExpressionAtomic(p);
  }
|
  LP FORALL
  (
    (LP (VARID)*  RP)
  |
    NIL
  ) lExp = le lExp2 = le RP
  {
    //-- A ForAll logical expression
    retVal = new LogicalExpressionForAll(lExp, lExp2);
  }
|
  LP ASSIGN vn:VARID t = term RP
    {
      String s = vn.getText().toLowerCase();

      //-- Add the variable to the variable list.
      if (!vars.contains(s))
        vars.add(s);

      //-- An assigment logical expression.
      retVal = new LogicalExpressionAssignment(vars.indexOf(s), t);
    }
|
  LP CALL func = fid param = terml RP
    {
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
;

//-- Logical Atom
la returns [Predicate retVal]
  {
    //-- The argument list of the logical atom.
    List l;
  }
:
  LP pn:ID l = terml
    {
      //-- Add the constant symbol to the list of constant symbols in the
      //-- domain.
      int index = domain.addConstant(pn.getText().toLowerCase());

      //-- Create the logical atom.
      retVal = new Predicate(index, vars.size(), new TermList(l));
    }
  RP
|
  var:VARID
    {
      //-- Create the variable predicate (i.e., a predicate that is only a
      //-- variable at compile time, but will be bound to a predicate at run
      //-- time.
      retVal = new Predicate(vars.indexOf(var.getText().toLowerCase()),
                             vars.size());
    }
;

//------------------------------- Term ------------------------------

//-- Term List
terml returns [List retVal] :
  {
    //-- A LinkedList to store the terms in the term list.
    LinkedList<Term> list = new LinkedList<Term>();

    //-- The current term.
    Term tn;
  }
  (
    tn = term
      {
        //-- Add the current term to the list of terms seen so far.
        list.addFirst(tn);
      }
  )*
  {
    //-- Create the object that represents this term list.
    retVal = List.MakeList(list);
  }
;

//-- Term
term returns [Term retVal]
  {
    //-- The name of the function called in a call term.
    String func;

    //-- The term list.
    List list;

    //-- The current term.
    Term tn;

    retVal = null;
  }
:
  vn:VARID
    {
      //-- Add the variable symbol to the variable list.
      if (!vars.contains(vn.getText().toLowerCase()))
        vars.add(vn.getText().toLowerCase());

      //-- Create the object that represents this variable symbol.
      retVal = new TermVariable(vars.indexOf(vn.getText().toLowerCase()));
    }
|
  in:ID
    {
      //-- Add the constant symbol to the list of constant symbols in the
      //-- domain.
      int index = domain.addConstant(in.getText().toLowerCase());

      //-- Create the object that represents this constant symbol.
      retVal = new TermConstant(index);
    }
|
  num:NUM
    {
      //-- Create the object that represents this numerical term.
      retVal = new TermNumber(Double.parseDouble(num.getText().toLowerCase()));
    }
|
  LP list = terml
    (
      DOT tn = term
        {
          //-- Append the current term to the end of the term list.
          retVal = new TermList(list.append(tn));
        }
    )?
    {
      //-- If retVal is not already created, create it as a list term.
      if (retVal == null)
        retVal = new TermList(list);
    }
  RP
|
  NIL
    {
      //-- Empty list term.
      retVal = TermList.NIL;
    }
|
  LP CALL func = fid list = terml RP
    {
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
;

//-- Function ID
fid returns [String retVal] :
  id:ID
    {
      retVal = id.getText();
    }
|
  DIV
    {
      retVal = "StdLib.div";
    }
|
  EQUAL
    {
      retVal = "StdLib.equal";
    }
|
  LESS
    {
      retVal = "StdLib.less";
    }
|
  LESSEQ
    {
      retVal = "StdLib.lessEq";
    }
|
  MEMBER
    {
      retVal = "StdLib.member";
    }
|
  MINUS
    {
      retVal = "StdLib.minus";
    }
|
  MORE
    {
      retVal = "StdLib.more";
    }
|
  MOREEQ
    {
      retVal = "StdLib.moreEq";
    }
|
  MULT
    {
      retVal = "StdLib.mult";
    }
|
  NOTEQ
    {
      retVal = "StdLib.notEq";
    }
|
  PLUS
    {
      retVal = "StdLib.plus";
    }
|
  POWER
    {
      retVal = "StdLib.power";
    }
;

//------------------------------- Lexer -----------------------------

class JSHOP2Lexer extends Lexer;

//------------------------------- Options ---------------------------

options
{
  charVocabulary = '\0'..'\377';
  exportVocab = JSHOP2;
  testLiterals = false;
  k = 4;
  caseSensitive = false;
  caseSensitiveLiterals = false;
}

//-- Keywords
tokens
{
  AND            = "and";
  ASSIGN         = "assign";
  CALL           = "call";
  DEFDOMAIN      = "defdomain";
  DEFPROBLEM     = "defproblem";
  DEFPROBLEMSET  = "def-problem-set";
  FORALL         = "forall";
  IMPLY          = "imply";
  MEMBER         = "member";
  NIL            = "nil";
  NOT            = "not";
  OR             = "or";
  STDLIB         = "stdlib";
}

//-- Grammar Terminals
AXIOM       : ":-";
DIV         : '/';
DOT         : '.';
EQUAL       : '=';
LESS        : '<';
LESSEQ      : "<=";
LP          : '(';
MINUS       : '-' WS;
MORE        : '>';
MOREEQ      : ">=";
MULT        : '*';
NOTEQ       : "!=";
PLUS        : '+' WS;
POWER       : '^';
RP          : ')';

FIRST       : ":first";
IMMEDIATE   : ":immediate";
METHOD      : ":method";
OPERATOR    : ":operator";
PROTECTION  : ":protection";
SORT        : ":sort-by";
UNORDERED   : ":unordered";

//-- Whitespace (ignored)
WS :
  (
    ' '
  |
    '\t'
  |
    '\f'
  |
    //-- Handle newlines
    (
      "\r\n"  //-- DOS
    |
      '\r'    //-- Macintosh
    |
      '\n'    //-- Unix
    )
      {
        newline();
      }
  )
  {
    $setType(Token.SKIP);
  }
;

//-- Comments, LISP style (ignored)
COMMENT :
  ';' (~('\n' | '\r'))*
  {
    $setType(Token.SKIP);
  }
;

//-- Identifier
ID
  options
  {
    testLiterals = true;   //-- Keywords can't be used as identifiers
  }
:
  ('a'..'z' | '_') ('a'..'z' | '-' | '_' | '?' | '!' | '0'..'9')*
;

//-- Operator name
OPID :
  '!' ('a'..'z' | '-' | '_' | '?' | '!' | '0'..'9')*
;

//-- Variable symbol identifier
VARID :
  '?' ('a'..'z' | '-' | '_' | '?' | '!' | '0'..'9')*
;

//-- Numerical value
NUM :
  ('-' | '+')? ('0'..'9')+ ('.' ('0' .. '9')+)? ('e' ('-' | '+')? ('0' .. '9')+ )?
;
