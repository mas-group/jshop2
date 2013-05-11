package JSHOP2;

import java.util.Vector;

/** Each conjunction at compile time is represented as an instance of this
 *  class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class LogicalExpressionConjunction extends LogicalExpression
{
  /** The number of objects instantiated from this class before this object was
   *  instantiated. Used to make the name of the precondition class that
   *  implements this conjunction unique.
  */
  private int cnt;

  /** An array of logical expressions the conjunction of which is represented
   *  by this object.
  */
  private LogicalExpression[] le;

  /** To initialize this conjunction.
   *
   *  @param leIn
   *          a <code>Vector</code> of logical expressions the conjunction of
   *          which is represented by this object. Note that we use a
   *          <code>Vector</code> rather than an array since at compile time
   *          we do not know how many conjuncts there are in this particular
   *          conjunction.
  */
  public LogicalExpressionConjunction(Vector<LogicalExpression> leIn)
  {
    le = new LogicalExpression[leIn.size()];

    for (int i = 0; i < leIn.size(); i++)
      le[i] = leIn.get(i);

    cnt = getClassCnt();
  }

  /** This function produces Java code that implements the classes any object
   *  of which can be used at run time to represent the conjuncts of this
   *  conjunction, and the conjunction itself.
  */
  public String getInitCode()
  {
    String s = "";

    //-- First produce any code needed by the conjuncts.
    for (int i = 0; i < le.length; i++)
      s += le[i].getInitCode();

    //-- The header of the class for this conjunction at run time. Note the use
    //-- of 'cnt' to make the name of this class unique.
    s += "class Precondition" + cnt + " extends Precondition" + endl;

    //-- Defining two arrays for storing the iterators and bindings for each
    //-- conjunct.
    s += "{" + endl + "\tPrecondition[] p;" + endl + "\tTerm[][] b;" + endl;

    //-- The constructor of the class.
    s += endl+ "\tpublic Precondition" + cnt + "(Term[] unifier)" + endl;

    //-- Allocate the array of iterators.
    //-- Set to one more than the length, and the first one will be blank
    //-- Meant to match up with the bindings, where the first binding will
    //-- be the initial binding.
    s += "\t{" + endl + "\t\tp = new Precondition[" + (le.length+1) + "];" + endl;

    //-- For each conjunct,
    for (int i = 1; i <= le.length; i++)
      //-- Set the corresponding element in the array to the code that produces
      //-- that conjunct.
      s += "\t\tp[" + i + "] = " + le[i-1].toCode() + ";" + endl;

    //-- Allocate the array of bindings.
    //-- Set to one more than the number of conjuncts.  The first position
    //-- will be the initial binding.
    s += "\t\tb = new Term[" + (le.length+1) + "][];" + endl;
    s += "\t\tb[0] = unifier;" + endl;
    s += "\t\tb[0] = Term.merge( b, 1 );" + endl + endl;

    //-- A conjunction can be potentially satisfied more than once, so the
    //-- default for the 'isFirstCall' flag is false.
    s += "\t\tsetFirst(false);" + endl + "\t}" + endl + endl;

    //-- Define the 'bind' function.
    s += "\tpublic void bind(Term[] binding)" + endl + "\t{" + endl;

    //-- Implement the 'bind' function by:
    //-- First copy the initial binding into the first spot.
    s += "\t\tb[0] = binding;" + endl;
    s += "\t\tb[0] = Term.merge( b, 1 );" + endl;
    s += "\t\tp[1].bind(binding);" + endl;
    for (int i = 1; i <= le.length; i++) 
      //-- Reset bindings
      s += "\t\tb[" + i + "] = null;" + endl;

    //-- Define the 'nextBindingHelper' function.
    s += "\t}" + endl + endl + "\tprotected Term[] nextBindingHelper()" + endl;
    s += "\t{" + endl;

    //-- Implement the 'nextBindingHelper' function.
    s += getInitCodeNext();

    //-- Define the 'resetHelper' function.
    s += "\t}" + endl + endl + "\tprotected void resetHelper()" + endl + "\t{";
    s += endl;

    //-- Implement the 'resetHelper' function.
    s += getInitCodeReset();

    //-- Close the function definition and the class definition and return the
    //-- resulting string.
    return s + "\t}" + endl + "}" + endl + endl;
  }

  /**
   * This function produces Java code that implements the
   * <code>nextBindingHelper</code> function for the precondition object that
   * represents this conjunction at run time.
   * 
   * @return the produced code as a <code>String</code>.
   */
  private String getInitCodeNext()
  {
    String s = "";
    int i;

    //-- To be used to add appropriate number of tabs to each line of code.
    String tabs;

    //-- Start with the outermost conjunct, and try to find a binding for that
    //-- conjunct. If there is no more binding for that conjunct, try to find
    //-- the next binding for the next outermost conjunct.
    for (i = le.length, tabs = "\t\t"; i >= 1; i--, tabs += "\t")
    {
      if ( i != le.length )
        s += tabs + "boolean b" + i + "changed = false;" + endl;
      s += tabs + "while (b[" + i + "] == null)" + endl + tabs + "{" + endl;
    }

    //-- Try the outer most conjunct.
    s += tabs + "b[1] = p[1].nextBinding();" + endl;
    //-- If there is no more binding for the outermost conjunct, return null.
    s += tabs + "if (b[1] == null)" + endl + tabs + "\treturn null;" + endl;
    s += tabs + "b1changed = true;" + endl;
    
    //-- Going from third outermost conjunct inward, try to apply newly-found
    //-- bindings for outermost conjuncts to each inner conjunct after reseting
    //-- it, and try to find bindings for inner conjuncts.
    tabs = tabs.substring(0, tabs.length() - 1);
    for (i = 2; i <= le.length; i++, tabs = tabs.substring(0, tabs.length() - 1))
    {
      s += tabs + "}" + endl;
      s += tabs + "if ( b" + (i-1) + "changed ) {" + endl;
      s += tabs + "\tp[" + i + "].reset();" + endl;
      s += tabs + "\tp[" + i + "].bind(Term.merge(b, " + i + "));" + endl;
      s += tabs + "}" + endl;
      s += tabs + "b[" + i + "] = p[" + i + "].nextBinding();" + endl;
      //-- If no binding found, null out the next outermost conjunct so we
      //-- try another set of bindings.
      s += tabs + "if (b[" + i + "] == null) b[" + (i-1) + "] = null;" + endl;
      if ( i != le.length )
        s += tabs + "b" + i + "changed = true;" + endl;
      
    }
    s += "\t\t}" + endl + endl;
    //-- Return the result of the merging of the bindings found for each
    //-- conjunct.
    s += "\t\tTerm[] retVal = Term.merge(b, " + (le.length + 1) + ");" + endl;
    s += "\t\tb[" + le.length + "] = null;" + endl;
    s += "\t\treturn retVal;" + endl;
    return s;
  }

  /** This function produces Java code that implements the
   *  <code>resetHelper</code> function for the precondtion object that
   *  represents this conjunction at run time.
   *
   *  @return
   *          the produced code as a <code>String</code>.
  */
  private String getInitCodeReset()
  {
    String s = "";
    int i;

    //-- First, reset all the conjuncts.
    for (i = 1; i <= le.length; i++)
      s += "\t\tp[" + i + "].reset();" + endl;
    //-- null out intermediate bindings.
    for ( i = 1; i <= le.length; i++ )
      s += "\t\tb[" + i + "] = null;" + endl;

    return s;
  }

  /** To propagate the variable count to all the logical expressions the
   *  conjunction of which this object represents.
  */
  protected void propagateVarCount(int varCount)
  {
    for (int i = 0; i < le.length; i++)
      le[i].setVarCount(varCount);
  }

  /** This function produces the Java code to create an object of the class
   *  that was implemented to represent this conjunction at run time.
  */
  public String toCode()
  {
    return "new Precondition" + cnt + "(unifier)";
  }
}
