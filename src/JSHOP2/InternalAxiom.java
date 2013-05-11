package JSHOP2;

import java.util.Vector;

/** Each axiom at compile time is represented as an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class InternalAxiom extends InternalElement
{
  /** A <code>Vector</code> of logical expressions each of which represents a
   *  branch of the axiom. Note that we use a <code>Vector</code> rather than
   *  an array because at compile time we do not know how many branches a
   *  particular axiom will have.
  */
  private Vector<LogicalPrecondition> branches;

  /** The number of objects already instantiated from this class.
  */
  private static int classCnt = 0;

  /** A <code>Vector</code> of <code>String</code>s each of which represents
   *  the label of a branch of this axiom.
  */
  private Vector<String> labels;

  /** To initialize an <code>InternalAxiom</code> object.
   *
   *  @param head
   *          head of the axiom.
   *  @param branchesIn
   *          a <code>Vector</code> of logical expressions each of which
   *          represents a branch of the axiom.
   *  @param labelsIn
   *          a <code>Vector</code> of <code>String</code> labels.
  */
  public InternalAxiom(Predicate head, Vector<LogicalPrecondition> branchesIn, Vector<String> labelsIn)
  {
    //-- Set the head of this InternalAxiom. Note the use of 'classCnt' to make
    //-- this object distinguishable from other objects instantiated from this
    //-- same class.
    super(head, classCnt++);

    //-- Set the branches of the axiom, and their labels.
    branches = branchesIn;
    labels = labelsIn;

    //-- For each branch, set the number of variables in the precondition for
    //-- that branch. This will be used to produce the code that will be used
    //-- to find bindings, since a binding is an array of this size.
    for (LogicalPrecondition pre : branchesIn)
      pre.setVarCount(getHead().getVarCount());
  }

  /** This function produces the Java code needed to implement this axiom.
  */
  public String toCode()
  {
    String s = "";

    //-- First produce the initial code for the preconditions of each branch.
    for (int i = 0; i < branches.size(); i++)
      s += ((LogicalPrecondition)branches.get(i)).getInitCode();

    //-- The header of the class for this axiom at run time. Note the use of
    //-- 'getCnt()' to make the name of this class unique.
    s += "class Axiom" + getCnt() + " extends Axiom" + endl + "{" + endl;

    //-- The constructor of the class.
    s += "\tpublic Axiom" + getCnt() + "()" + endl + "\t{" + endl;

    //-- Call the constructor of the base class (class 'Axiom') with the code
    //-- that produces the head of this axiom, and number of branches of this
    //-- axiom as its parameters.
    s += "\t\tsuper(" + getHead().toCode() + ", " + branches.size() + ");";
    s += endl + "\t}" + endl + endl;

    //-- The function that returns an iterator that can be used to find all the
    //-- bindings that satisfy a given precondition of this axiom and return
    //-- them one-by-one.
    s += "\tpublic Precondition getIterator(Term[] unifier, int which)";
    s += endl + "\t{" + endl + "\t\tPrecondition p;" + endl + endl;

    //-- The switch statement to choose the appropriate precondition.
    s += "\t\tswitch (which)" + endl + "\t\t{";

    //-- For each branch,
    for (int i = 0; i < branches.size(); i++)
    {
      //-- Retrieve the logical precondition.
      LogicalPrecondition pre = (LogicalPrecondition)branches.get(i);

      //-- Produce the code that will return the appropriate iterator.
      s += endl + "\t\t\tcase " + i + ":" + endl + "\t\t\t\tp = ";
      s += pre.toCode() + ";" + endl;

      //-- If the logical precondition is marker ':first', set the appropriate
      //-- flag.
      if (pre.getFirst())
        s += "\t\t\t\tp.setFirst(true);" + endl;

      s += "\t\t\tbreak;";
    }

    //-- Close the switch statement.
    s += endl + "\t\t\tdefault:" + endl + "\t\t\t\treturn null;" + endl;
    s += "\t\t}" + endl;

    //-- Reset the precondition and return it.
    s += endl + "\t\tp.reset();" + endl + endl + "\t\treturn p;" + endl;

    //-- This function returns the label of a given branch of this axiom.
    s += "\t}" + endl + endl + "\tpublic String getLabel(int which)" + endl;

    //-- The switch statement to choose the appropriate label.
    s += "\t{" + endl + "\t\tswitch (which)" + endl + "\t\t{";

    //-- For each branch;
    for (int i = 0; i < labels.size(); i++)
      //-- Return its associated label.
      s += endl + "\t\t\tcase " + i + ": return \"" + labels.get(i) + "\";";

    //-- Close the switch statement.
    s += endl + "\t\t\tdefault: return null;" + endl + "\t\t}" + endl;

    //-- Close the function definition and the class definition and return the
    //-- resulting string.
    return s + "\t}" + endl + "}" + endl + endl;
  }
}
