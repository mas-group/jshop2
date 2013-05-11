package JSHOP2;

import java.util.Vector;

/** Each disjunction at compile time is represented as an instance of this
 *  class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class LogicalExpressionDisjunction extends LogicalExpression
{
  /** The number of objects instantiated from this class before this object was
   *  instantiated. Used to make the name of the precondition class that
   *  implements this disjunction unique.
  */
  private int cnt;

  /** An array of logical expressions the disjunction of which is represented
   *  by this object.
  */
  private LogicalExpression[] le;

  /** To initialize this disjunction.
   *
   *  @param leIn
   *          a <code>Vector</code> of logical expressions the disjunction of
   *          which is represented by this object. Note that we use a
   *          <code>Vector</code> rather than an array since at compile time
   *          we do not know how many disjuncts there are in this particular
   *          disjunction.
  */
  public LogicalExpressionDisjunction(Vector<LogicalExpression> leIn)
  {
    le = new LogicalExpression[leIn.size()];

    for (int i = 0; i < leIn.size(); i++)
      le[i] = leIn.get(i);

    cnt = getClassCnt();
  }

  /** This function produces Java code that implements the classes any object
   *  of which can be used at run time to represent the disjuncts of this
   *  disjunction, and the disjunction itself.
  */
  public String getInitCode()
  {
    String s = "";
    int i;

    //-- First produce any code needed by the disjuncts.
    for (i = 0; i < le.length; i++)
      s += le[i].getInitCode();

    //-- The header of the class for this disjunction at run time. Note the use
    //-- of 'cnt' to make the name of this class unique.
    s += "class Precondition" + cnt + " extends Precondition" + endl;

    //-- Defining two arrays for storing the iterators for each disjunct and
    //-- the current binding.
    s += "{" + endl + "\tPrecondition[] p;" + endl + "\tTerm[] b;" + endl;

    //-- Defining an integer to keep track of which disjunct has already been
    //-- considered.
    s += "\tint whichClause;" + endl + endl;

    //-- The constructor of the class.
    s += "\tpublic Precondition" + cnt + "(Term[] unifier)" + endl + "\t{";

    //-- Allocate the array of iterators.
    s += endl + "\t\tp = new Precondition[" + le.length + "];" + endl;

    //-- For each disjunct,
    for (i = 0; i < le.length; i++)
      //-- Set the corresponding element in the array to the code that produces
      //-- that disjunct.
      s += "\t\tp[" + i + "] = " + le[i].toCode() + ";" + endl + endl;

    //-- A conjucntion can be potentially satisfied more than once, so the
    //-- default for the 'isFirstCall' flag is false.
    s += "\t\tsetFirst(false);" + endl + "\t}" + endl + endl;

    //-- Define the 'bind' function.
    s += "\tpublic void bind(Term[] binding)" + endl + "\t{" + endl;

    //-- Implement the 'bind' function by:
    for (i = 0; i < le.length; i++)
      //-- Binding each disjunct in this disjunction.
      s += "\t\tp[" + i + "].bind(binding);" + endl;

    //-- Define the 'nextBindingHelper' function.
    s += "\t}" + endl + endl + "\tprotected Term[] nextBindingHelper()" + endl;
    s += "\t{";

    //-- Implement the 'nextBindingHelper' function by iterating over all
    //-- disjuncts:
    s += endl + "\t\twhile (whichClause < " + le.length + ")" + endl;

    //-- Look for the next binding for the current disjunct.
    s += "\t\t{" + endl + "\t\t\tb = p[whichClause].nextBinding();" + endl;

    //-- If there is such a binding, return it.
    s += "\t\t\tif (b != null)" + endl + "\t\t\t\t return b;" + endl;

    //-- Otherwise, try the next disjunct.
    s += "\t\t\twhichClause++;" + endl + "\t\t}";

    //-- If there are no more disjuncts left, return null.
    s += endl + endl + "\t\treturn null;" + endl + "\t}" + endl + endl;

    //-- Define the 'resetHelper' function.
    s += "\tprotected void resetHelper()" + endl + "\t{" + endl;

    //-- Implement the 'resetHelper' function by resetting all the disjuncts
    //-- and set the varaible that keeps track of which disjuncts have already
    //-- been considered to 0.
    for (i = 0; i < le.length; i++)
      s += "\t\tp[" + i + "].reset();" + endl;

    return s + "\t\twhichClause = 0;" + endl + "\t}" + endl + "}" + endl +
           endl;
  }
  
  /** To propagate the variable count to all the logical expressions the
   *  disjunction of which this object represents.
  */
  protected void propagateVarCount(int varCount)
  {
    for (int i = 0; i < le.length; i++)
      le[i].setVarCount(varCount);
  }

  /** This function produces the Java code to create an object of the class
   *  that was implemented to represent this disjunction at run time.
  */
  public String toCode()
  {
    return "new Precondition" + cnt + "(unifier)";
  }


}
