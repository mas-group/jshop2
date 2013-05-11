package JSHOP2;

/** Each atomic term in a logical expression at compile time is represented as
 *  an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class LogicalExpressionAtomic extends LogicalExpression
{
  /** The logical atom.
  */
  private Predicate logicalAtom;

  /** To initialize this atomic logical expression.
   *
   *  @param logicalAtomIn
   *          the logical atom this class represents.
  */
  public LogicalExpressionAtomic(Predicate logicalAtomIn)
  {
    logicalAtom = logicalAtomIn;
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

  /** To propagate the variable count to the logical atom represented by this
   *  object.
  */
  protected void propagateVarCount(int varCount)
  {
    logicalAtom.setVarCount(varCount);
  }

  /** This function produces the Java code to create a
   *  <code>PreconditionAtomic</code> object that represents this atomic
   *  logical expression at run time.
  */
  public String toCode()
  {
    return "new PreconditionAtomic(" + logicalAtom.toCode() + ", unifier)";
  }
}
