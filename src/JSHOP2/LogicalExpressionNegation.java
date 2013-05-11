package JSHOP2;

/** Each negative term in a logical expression at compile time is represented
 *  as an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class LogicalExpressionNegation extends LogicalExpression
{
  /** The logical expression to be negated.
  */
  private LogicalExpression le;

  /** To initialize this negative logical expression.
   *
   *  @param leIn
   *          the logical expression to be negated.
  */
  public LogicalExpressionNegation(LogicalExpression leIn)
  {
    le = leIn;
  }

  /** This function produces Java code that implements the class any object of
   *  which can be used at run time to represent the logical expression this
   *  object is negating.
  */
  public String getInitCode()
  {
    return le.getInitCode();
  }

  /** To propagate the variable count to the logical expression the negative of
   *  which this object represents.
  */
  protected void propagateVarCount(int varCount)
  {
    le.setVarCount(varCount);
  }

  /** This function produces the Java code to create a
   *  <code>PreconditionNegation</code> object that represents this negative
   *  logical expression at run time.
  */
  public String toCode()
  {
    return "new PreconditionNegation(" + le.toCode() + ", " + getVarCount() + ")";
  }
}
