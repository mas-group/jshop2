package JSHOP2;

/** Each assign term in a logical expression at compile time is represented as
 *  an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class LogicalExpressionAssignment extends LogicalExpression
{
  /** The term to which the variable will be assigned.
  */
  private Term t;

  /** The index of the variable to be assigned.
  */
  private int whichVar;

  /** To initialize this assignment logical expression.
   *
   *  @param whichVarIn
   *          the index of the variable to be assigned.
   *  @param tIn
   *          the term to which the variable will be assigned.
  */
  public LogicalExpressionAssignment(int whichVarIn, Term tIn)
  {
    whichVar = whichVarIn;
    t = tIn;
  }

  /** This class does not need any initialization code, therefore, this
   *  function simply returns an empty <code>String</code>.
  */
  public String getInitCode()
  {
    return "";
  }
  
  protected LogicalExpression getNNF(boolean negated) {
	  if (negated) {
		  return new LogicalExpressionNegation(this);
	  } else {
		  return this;
	  }
  }

  /** This class does not need to propagate the variable count, therefore, this
   *  function does nothing.
  */
  protected void propagateVarCount(int varCount)
  {
  }

  /** This function produces the Java code to create a
   *  <code>PreconditionAssign</code> object that represents this assignment
   *  logical expression at run time.
  */
  public String toCode()
  {
    return "new PreconditionAssign(" + t.toCode() + ", unifier, " + whichVar +
           ")";
  }
}
