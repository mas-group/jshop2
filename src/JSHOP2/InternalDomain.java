package JSHOP2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

/** Each domain at compile time is represented as an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class InternalDomain
{
  /** The number of solution plans per planning problem that the user has
   *  requested from this object.
  */
  private int planNo;

  /** A <code>Vector</code> of axioms seen so far in the domain description.
   *  Each member is of type <code>InternalAxiom</code>.
  */
  private Vector<InternalAxiom> axioms;

  /** A <code>Vector</code> of <code>String</code> names of user-defined
   *  external code calls that must be imported before being used in the
   *  domain description.
  */
  private Vector<String> calcs;

  /** A <code>Vector</code> of <code>String</code> names of compound tasks seen
   *  so far in the domain.
  */
  private Vector<String> compoundTasks;

  /** A <code>Vector</code> of <code>String</code> names of constant symbols
   *  seen so far in the domain.
  */
  private Vector<String> constants;

  /** The number of constant symbols already seen in the planning domain. Any
   *  number of constant symbols in the planning problem more than this
   *  indicates presence of constant symbols that appear exclusively in the
   *  problem description.
  */
  private int constantsSize;

  /** The new line character in the platform JSHOP2 is running on.
  */
  final static String endl = System.getProperty("line.separator");

  /** A <code>Vector</code> of methods seen so far in the domain description.
   *  Each member is of type <code>InternalMethod</code>.
  */
  private Vector<InternalMethod> methods;

  /** The <code>String</code> name of the domain.
  */
  private String name;

  /** A <code>Vector</code> of operators seen so far in the domain description.
   *  Each member is of type <code>InternalOperator</code>.
  */
  private Vector<InternalOperator> operators;

  /** The parser object that will parse this domain.
  */
  private JSHOP2Parser parser;

  /** A <code>Vector</code> of <code>String</code> names of primitive tasks
   *  seen so far in the domain.
  */
  private Vector<String> primitiveTasks;

  /** The <code>String</code> name of the planning problem.
  */
  private String probName;

  /** To initialize this domain.
   *
   *  @param fin
   *          the file from which the domain description is to be read.
   *  @param planNoIn
   *          the number of solution plans per planning problem that the user
   *          has requested from this object.
   *  @throws IOException
  */
  public InternalDomain(File fin, int planNoIn) throws IOException
  {
    planNo = planNoIn;

    axioms = new Vector<InternalAxiom>();

    calcs = new Vector<String>();

    compoundTasks = new Vector<String>();

    constants = new Vector<String>();

    methods = new Vector<InternalMethod>();

    operators = new Vector<InternalOperator>();

    //-- Initialize the lexer and the parser associated with this object.
    JSHOP2Lexer lexer = new JSHOP2Lexer(new FileInputStream(fin));
    parser = new JSHOP2Parser(lexer);
    parser.initialize(lexer, this);

    primitiveTasks = new Vector<String>();
  }

  /** To add an axiom to the list of axioms read from the file.
   *
   *  @param axiom
   *          the axiom to be added.
  */
  public void addAxiom(InternalAxiom axiom)
  {
    axioms.add(axiom);
  }

  /** To add a <code>String</code> used as a name of a compound task in the
   *  domain description to the list of compound task names, in case it has not
   *  been added before.
   *
   *  @param s
   *          the <code>String</code> to be added.
   *  @return
   *          the index assigned to this name.
  */
  public int addCompoundTask(String s)
  {
    int index;

    //-- If this name has not been added before, add it to the end of the
    //-- Vector and return its index.
    if ((index = compoundTasks.indexOf(s)) == -1)
    {
      compoundTasks.add(s);
      return compoundTasks.size() - 1;
    }

    //-- Otherwise, just return its index.
    return index;
  }

  /** To add a <code>String</code> used as a constant symbol in the domain
   *  description to the list of constant symbols, in case it has not been
   *  added before.
   *
   *  @param s
   *          the <code>String</code> to be added.
   *  @return
   *          the index assigned to this name.
  */
  public int addConstant(String s)
  {
    int index;

    //-- If this name has not been added before, add it to the end of the
    //-- Vector and return its index.
    if ((index = constants.indexOf(s)) == -1)
    {
      constants.add(s);
      return constants.size() - 1;
    }

    //-- Otherwise, just return its index.
    return index;
  }

  /** To add the <code>String</code> name of an external code call to the list
   *  of such code calls.
   *
   *  @param what
   *          the name of the code call being added.
  */
  public void addCalc(String what)
  {
    if (!calcs.contains(what))
      calcs.add(what);
  }

  /** To add a method to the list of methods read from the file.
   *
   *  @param method
   *          the method to be added.
  */
  public void addMethod(InternalMethod method)
  {
    methods.add(method);
  }

  /** To add an operator to the list of operators read from the file.
   *
   *  @param op
   *          the operator to be added.
  */
  public void addOperator(InternalOperator op)
  {
    operators.add(op);
  }

  /** To add a <code>String</code> used as a name of a primitive task in the
   *  domain description to the list of primitive task names, in case it has not
   *  been added before.
   *
   *  @param s
   *          the <code>String</code> to be added.
   *  @return
   *          the index assigned to this name.
  */
  public int addPrimitiveTask(String s)
  {
    int index;

    //-- If this name has not been added before, add it to the end of the
    //-- Vector and return its index.
    if ((index = primitiveTasks.indexOf(s)) == -1)
    {
      primitiveTasks.add(s);
      return primitiveTasks.size() - 1;
    }

    //-- Otherwise, just return its index.
    return index;
  }

  /** This function writes the Java code necessary to produce this domain at
   *  run time in the appropriate file.
   *
   *  @param varsMaxSize
   *          the maximum number of variables seen in any variable scope in
   *          this domain.
   *  @throws IOException
  */
  public void close(int varsMaxSize) throws IOException
  {
    //-- To hold the String to be written.
    String s;

    //-- JSHOP2 classes should be imported first.
    s = "import JSHOP2.*;" + endl + endl;

    //-- Produce the classes that represent the operators.
    for (int i = 0; i < operators.size(); i++)
      s += operators.get(i).toCode();

    //-- Produce the classes that represent the methods.
    for (int i = 0; i < methods.size(); i++)
      s += methods.get(i).toCode();

    //-- Produce the classes that represent the axioms.
    for (int i = 0; i < axioms.size(); i++)
      s += axioms.get(i).toCode();

    //-- Produce the class that represents the domain itself.
    s += "public class " + name + " extends Domain" + endl + "{" + endl;

    //-- Take care of the user-defined external code calls first by
    //-- instantiating an  object of that class to do the calculations.
    for (int i = 0; i < calcs.size(); i++)
    {
      String imp = (String)calcs.get(i);

      s += "\tpublic static " + imp + " calculate" + imp +
           " = new " + imp + "();" + endl + endl;
    }

    //-- Produce the constructor for the class that represents this domain.
    s += "\tpublic " + name + "()" + endl + "\t{" + endl;

    //-- To initialize an array of the variable symbols the size of which is
    //-- equal to the maximum number of variables seen in any scope in the
    //-- domain. This way, all the variable symbols that have the same index
    //-- will point to the same thing rather than pointing to duplicate copies.
    s += "\t\tTermVariable.initialize(" + varsMaxSize + ");" + endl + endl;

    //-- Produce the array that maps constant symbols to integers.
    s += vectorToCode(constants, "constants");
    //-- Produce the array that maps compound tasks to integers.
    s += vectorToCode(compoundTasks, "compoundTasks");
    //-- Produce the array that maps primitive tasks to integers.
    s += vectorToCode(primitiveTasks, "primitiveTasks");

    //-- Allocate an array of type 'Method[]'. The size of the array is the
    //-- number of compound tasks in the domain, and each element of the array
    //-- represents all the methods that can be used to decompose the
    //-- corresponding compound task.
    s += "\t\tmethods = new Method[" + compoundTasks.size() + "][];" + endl
         + endl;

    //-- For each compound task,
    for (int i = 0; i < compoundTasks.size(); i++)
    {
      //-- To store the number of methods that can decompose this compound
      //-- task.
      int j = 0;

      //-- To iterate over the methods.
      //-- First iterate over the methods to find out how many methods can
      //-- decompose this compound task.
      for (InternalMethod m : methods)
      {
        if (m.getHead().getHead() == i)
          j++;
      }

      //-- Allocate an array of right size.
      s += "\t\tmethods[" + i + "] = new Method[" + j + "];" + endl;

      j = 0;
      
      //-- Next, iterate over the methods again, this time to add the methods
      //-- that can decompose this compound task to the array.
      for (InternalMethod m : methods)
      {
        if (m.getHead().getHead() == i)
          s += "\t\tmethods[" + i + "][" + j++ + "] = new Method" + m.getCnt() +
              "();" + endl;
      }

      s += endl;
    }

    //-- Allocate an array of type 'Operator[]'. The size of the array is the
    //-- number of primitive tasks in the domain, and each element of the array
    //-- represents all the operators that can be used to achieve the
    //-- corresponding primitive task.
    s += endl + "\t\tops = new Operator[" + primitiveTasks.size() + "][];" +
         endl + endl;

    //-- For each primitive task,
    for (int i = 0; i < primitiveTasks.size(); i++)
    {
      //-- To store the number of operators that can achieve this primitive
      //-- task.
      int j = 0;

      //-- To iterate over the operators.
      //-- First iterate over the operators to find out how many operators can
      //-- achieve this primitive task.
      for (InternalOperator o : operators)
      {
        if (o.getHead().getHead() == i)
          j++;
      }

      //-- Allocate an array of the right size.
      s += "\t\tops[" + i + "] = new Operator[" + j + "];" + endl;

      j = 0;
      //-- Next, iterate over the operators again, this time to add the
      //-- operators that can achieve this primitive task to the array.
      for (InternalOperator o : operators)
      {
        if (o.getHead().getHead() == i)
          s += "\t\tops[" + i + "][" + j++ + "] = new Operator" + o.getCnt() +
               "();" + endl;
      }

      s += endl;
    }

    //-- Allocate an array of type 'Axiom[]'. The size of the array is the
    //-- number of constant symbols in the domain, and each element of the
    //-- array represents all the axioms that can be used to prove predicates
    //-- which start with the corresponding constant symbol.
    s += "\t\taxioms = new Axiom[" + constants.size() + "][];" + endl + endl;

    //-- For each constant symbol,
    for (int i = 0; i < constants.size(); i++)
    {
      //-- To store the number of axioms that can prove predicates that start
      //-- with this constant symbol.
      int j = 0;

      //-- To iterate over the axioms.
      //-- First iterate over the axioms to find out how many axioms can be
      //-- used to prove the predicates that start with this constant symbol.
      for (InternalAxiom a : axioms) 
      {
        if (a.getHead().getHead() == i)
          j++;
      }

      //-- Allocate an array of the right size.
      s += "\t\taxioms[" + i + "] = new Axiom[" + j + "];" + endl;

      j = 0;
      
      //-- Next, iterate over the axioms again, this time to add the axioms
      //-- that can be used to prove the predicates that start with this
      //-- constant symbol to the array.
      for (InternalAxiom a : axioms) 
      {
        if (a.getHead().getHead() == i)
          s += "\t\taxioms[" + i + "][" + j++ + "] = new Axiom" + a.getCnt() +
               "();" + endl;
      }

      s += endl;
    }

    //-- Close the constructor and the class.
    s += "\t}" + endl + "}";

    //-- Open the file with the appropriate name.
    BufferedWriter dest = new BufferedWriter(new FileWriter(name + ".java"));

    //-- Write the String.
    dest.write(s, 0, s.length());

    //-- Close the file.
    dest.close();

    //-- Open another file with extension '.txt' to store the String names of
    //-- the constant symbols, the compound tasks and the primitive tasks in
    //-- the domain description. This data will be used when compiling planning
    //-- problems in this domain.
    dest = new BufferedWriter(new FileWriter(name + ".txt"));

    //-- Store the constant symbols.
    dumpStringArray(dest, constants);

    //-- Store the compound tasks.
    dumpStringArray(dest, compoundTasks);

    //-- Store the primitive tasks.
    dumpStringArray(dest, primitiveTasks);

    //-- Close the file.
    dest.close();
  }

  /** This function performs some necessary initialization when a problem file
   *  is being compiled, mainly reading and parsing the text file associated
   *  with the domain the planning problem is defined in.
   *
   *  @throws IOException
  */
  public void commandInitialize() throws IOException
  {
    //-- To read the text file that stores the names of the constant symbols
    //-- that appeared in the domain description.
    BufferedReader src;

    //-- Open the file.
    src = new BufferedReader(new FileReader(name + ".txt"));

    //-- Read in the constant symbols.
    constantsSize = readStringArray(src, constants);

    //-- Read in the compound task names.
    readStringArray(src, compoundTasks);

    //-- Read in the primitive task names.
    readStringArray(src, primitiveTasks);

    //-- Close the file.
    src.close();
  }

  /** This function writes the Java code necessary to produce these planning
   *  problems at run time in the appropriate file.
   *
   *  @param states
   *          the list of initial state of the world, one per each planning
   *          problem.
   *  @param taskLists
   *          the list of the task lists to be achieved, one per each planning
   *          problem.
   *  @throws IOException
  */
  public void commandToCode(LinkedList<Vector<Predicate>> states, LinkedList<TaskList> taskLists)
              throws IOException
  {
    //-- To hold the String to be written.
    String s;

    //-- Import the appropriate packages.
    s = "import java.util.LinkedList;" + endl + "import JSHOP2.*;" + endl +
        endl;

    //-- Define the class that represents this planning problem.
    s += "public class " + probName + endl + "{" + endl;

    //-- This function defines and allocate the array that will hold the String
    //-- names of the constant symbols that appeared in the problem description
    //-- but not in the domain description.
    s += "\tprivate static String[] defineConstants()" + endl + "\t{" + endl;
    s += "\t\tString[] problemConstants = new String[" +
         (constants.size() - constantsSize) + "];" + endl + endl;

    //-- Set the values of elements of that array.
    for (int i = constantsSize; i < constants.size(); i++)
      s += "\t\tproblemConstants[" + (i - constantsSize) + "] = \"" +
           (String)constants.get(i) + "\";" + endl;

    s += endl + "\t\treturn problemConstants;" + endl + "\t}" + endl + endl;

    //-- For each planning problem, initialize the current state of the world
    //-- to the initial state of the world in the problem description.

    //-- The index of the problem being solved.
    int problemIdx = 0;

    //-- For each problem,
    for (Vector<Predicate> state : states)
    {
      s += "\tprivate static void createState" + problemIdx++ + "(State s)"
           + "\t{" + endl;

      
      //-- For each predicate, in the initial world state of the problem
      for (Predicate p : state)
      {
        //-- Check if the predicate's head appears in the domain too. If not,
        //-- we don't need to add it to the world state because it doesn't make
        //-- a difference.
        if (p.getHead() < constantsSize)
          s += "\t\ts.add(" + p.toCode() + ");" + endl;
      }

      s += "\t}" + endl + endl;
    }

    //-- Define the main function.
    s += "\tpublic static LinkedList<Plan> getPlans()" + endl + "\t{" + endl;
    //-- List for all plans to be stored in
    s += "\t\tLinkedList<Plan> returnedPlans = new LinkedList<Plan>();" + endl;
    
    //-- To initialize an array of the constant symbols that we already know
    //-- exist so that there will be no duplicate copies of those constant
    //-- symbols.
    s += "\t\tTermConstant.initialize(" + constants.size() + ");" + endl +
         endl;

    //-- Instantiate an object of the class that represents the planning
    //-- domain.
    s += "\t\tDomain d = new " + name + "();" + endl + endl;

    //-- Call the function that passes this array to the the object that
    //-- represents the domain.
    s += "\t\td.setProblemConstants(defineConstants());" + endl + endl;

    //-- Initialize the object that will represent the current state of the
    //-- world.
    s += "\t\tState s = new State(" + constantsSize + ", d.getAxioms());" +
         endl;

    //-- Pass the domain description and the initial state of the world to the
    //-- JSHOP2 algorithm.
    s += endl + "\t\tJSHOP2.initialize(d, s);" + endl + endl;

    //-- Define the task list variable and the thread that solves the problems.
    s += "\t\tTaskList tl;" + endl + "\t\tSolverThread thread;" + endl + endl;

    //-- The index of the problem being solved.
    problemIdx = 0;

    //-- For each problem,
    for (TaskList tl : taskLists)
    {
      //-- If this is not the first problem, clear the variable that represents
      //-- the initial world state.
      if (problemIdx != 0)
        s += endl + "\t\ts.clear();" + endl;

      //-- Create the world state for this problem.
      s += "\t\tcreateState" + problemIdx + "(s);" + endl;

      //-- Create the initial task list.
      s += endl + tl.getInitCode("tl") + endl;

      //-- Define the thread that will solve this planning problem.
      s += "\t\tthread = new SolverThread(tl, " + planNo + ");" + endl;

      //-- Start the thread that will solve this planning problem.
      s += "\t\tthread.start();" + endl + endl;

      //-- Wait till thread is done, since JSHOP2's data members are static and
      //-- can handle only one problem at a time.
      s += "\t\ttry {" + endl + "\t\t\twhile (thread.isAlive())" + endl;
      s += "\t\t\t\tThread.sleep(500);" + endl;
      s += "\t\t} catch (InterruptedException e) {" + endl + "\t\t}" + endl;
      s += endl + "\t\treturnedPlans.addAll( thread.getPlans() );" + endl + endl;

      problemIdx++;
    }
    s += "\t\treturn returnedPlans;" + endl;
    s += "\t}" + endl + endl + "\tpublic static LinkedList<Predicate> getFirstPlanOps() {";
    s += endl + "\t\treturn getPlans().getFirst().getOps();" + endl;
    s += "\t}" + endl + "}";

    BufferedWriter dest;

    //-- Open the file with the appropriate name.
    dest = new BufferedWriter(new FileWriter(probName + ".java"));

    //-- Write the String.
    dest.write(s, 0, s.length());

    //-- Close the file.
    dest.close();
  }

  /** This function saves a given <code>Vector</code> of <code>String</code>s
   *  in a given file.
   *
   *  @param dest
   *          the file where the <code>Vector</code> is to be saved.
   *  @param list
   *          the <code>Vector</code> to be saved.
   *  @throws IOException
  */
  public void dumpStringArray(BufferedWriter dest, Vector<?> list)
              throws IOException
  {
    String buff;

    //-- First write the size of the Vector.
    buff = list.size() + endl;
    dest.write(buff, 0, buff.length());

    //-- Then, write the elements of the Vector one-by-one.
    for (int i = 0; i < list.size(); i++)
    {
      buff = list.get(i) + endl;
      dest.write(buff, 0, buff.length());
    }
  }

  /** This function returns the number of axioms in this domain.
   *
   *  @return
   *          the number of axioms in this domain.
  */
  public int getAxiomNo()
  {
    return axioms.size();
  }

  /** This function returns the <code>Vector</code> where the
   *  <code>String</code> names of the compound tasks in this domain are
   *  stored.
   *
   *  @return
   *          the <code>Vector</code> where the <code>String</code> names of
   *          the compound tasks in this domain are stored.
  */
  public Vector<String> getCompoundTasks()
  {
    return compoundTasks;
  }

  /** This function returns the <code>Vector</code> where the
   *  <code>String</code> names of the constant symbols in this domain are
   *  stored.
   *
   *  @return
   *          the <code>Vector</code> where the <code>String</code> names of
   *          the constant symbols in this domain are stored.
  */
  public Vector<String> getConstants()
  {
    return constants;
  }

  /** This function returns the number of methods in this domain.
   *
   *  @return
   *          the number of methods in this domain.
  */
  public int getMethodNo()
  {
    return methods.size();
  }

  /** This function returns the <code>String</code> name of this domain.
   *
   *  @return
   *          the <code>String</code> name of this domain.
  */
  public String getName()
  {
    return name;
  }

  /** This function returns the <code>Vector</code> where the
   *  <code>String</code> names of the primitive tasks in this domain are
   *  stored.
   *
   *  @return
   *          the <code>Vector</code> where the <code>String</code> names of
   *          the primitive tasks in this domain are stored.
  */
  public Vector<String> getPrimitiveTasks()
  {
    return primitiveTasks;
  }

  /** The main function that is called to do the compilation.
   *
   *  @param args
   *          the command line arguments.
   *  @throws Exception
  */
  public static void main(String[] args) throws Exception
  {
    //-- The number of solution plans to be returned.
    int planNo = -1;

    //-- Handle the number of solution plans the user wants to be returned.
    if (args.length == 2 || args[0].substring(0, 2).equals("-r")) {
      if (args[0].equals("-r"))
        planNo = 1;
      else if (args[0].equals("-ra"))
        planNo = Integer.MAX_VALUE;
      else try {
        planNo = Integer.parseInt(args[0].substring(2));
      } catch (NumberFormatException e) {
      }
    }

    //-- Check the number of arguments.
    if (((args.length != 2) || planNo <= 0) && (args.length != 1))
    {
      System.err.println("usage: java JSHOP2Parser " +
                         "[-r|-ra|-rSomePositiveInteger] input");
      System.exit(1);
    }

    //-- If this is a planning problem, call the 'command' rule in the parser.
    if (args.length == 2)
      (new InternalDomain(new File(args[1]), planNo)).parser.command();
    //-- If this is a planning domain, call the 'domain' rule in the parser.
    else
      (new InternalDomain(new File(args[0]), -1)).parser.domain();
  }

  /** This function reads a <code>Vector</code> of <code>String</code>s from
   *  a given file.
   *
   *  @param src
   *          the input file.
   *  @param list
   *          the <code>Vector</code> to be read.
   *  @return
   *          the number of the elements in the <code>Vector</code>.
   *  @throws IOException
  */
  public int readStringArray(BufferedReader src, Vector<String> list)
             throws IOException
  {
    //-- Read in the first line,
    String buff = src.readLine();
    //-- Which holds the size of the Vector to be read.
    int j = Integer.valueOf(buff).intValue();

    //-- Read in the 'j' elements of the Vector as Strings.
    for (int i = 0; i < j; i++)
    {
      buff = src.readLine();
      list.add(buff);
    }

    //-- Return the number of elements read.
    return j;
  }

  /** To set the name of this planning domain.
   *
   *  @param nameIn
   *          the name of this planning domain.
  */
  public void setName(String nameIn)
  {
    name = nameIn;
  }

  /** To set the name of this planning problem.
   *
   *  @param probNameIn
   *          the name of this planning problem.
  */
  public void setProbName(String probNameIn)
  {
    probName = probNameIn;
  }

  /** This function produces the Java code needed to allocate and initialize an
   *  array the elements of which are drawn from a given <code>Vector</code> of
   *  <code>String</code>s.
   *
   *  @param list
   *          the <code>Vector</code> the elements of which are to be stored in
   *          the resulting array.
   *  @param name
   *          the name of the array where the elements of the
   *          <code>Vector</code> are to be stored.
   *  @return
   *          the produced Java code.
  */
  public String vectorToCode(Vector<String> list, String name)
  {
    String retVal;

    //-- First, allocate the array.
    retVal = "\t\t" + name + " = new String[" + list.size() + "];" + endl;

    //-- Then, assign the elements of the array one by one.
    for (int i = 0; i < list.size(); i++)
      retVal += "\t\t" + name + "[" + i + "] = \"" + list.get(i) + "\";" + endl;

    return retVal + endl;
  }
}
