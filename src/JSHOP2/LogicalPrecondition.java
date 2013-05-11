package JSHOP2;

/** Each logical precondition at compile time is represented as an instance of
 *  this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class LogicalPrecondition extends CompileTimeObject
{
  /** 
   * Whether or not this logical precondition is marked <code>:first</code>.
  */
  private boolean first;

  /** The name of the function used in a <code>:sort-by</code> logical
   *  precondition.
  */
  private String func;

  /** The logical expression this class represents.
  */
  private LogicalExpression le;

  /** To initialize this logical precondition.
   *
   *  @param leIn
   *          the logical expression this class represents.
   *  @param firstIn
   *          whether or not this logical precondition is marked
   *          <code>:first</code>.
  */
  public LogicalPrecondition(LogicalExpression leIn, boolean firstIn)
  {
    le = leIn;
    first = firstIn;

    //-- This is not a :sort-by logical precondition, so the function name is
    //-- set null.
    func = null;
  }

  /** To initialize this logical precondition.
   *
   *  @param leIn
   *          the logical expression this class represents.
   *  @param funcIn
   *          the name of the function used in a <code>:sort-by</code> logical
   *          precondition.
  */
  public LogicalPrecondition(LogicalExpression leIn, String funcIn)
  {
    le = leIn;
    func = funcIn;

    //-- A :sort-by logical precondition can not be marked :first.
    first = false;
  }

  /** To check whether or not this logical precondition is marked
   *  <code>:first</code>.
   *
   *  @return
   *          <code>true</code> if this logical precondition is marked
   *          <code>:first</code>, <code>false</code> otherwise.
  */
  public boolean getFirst()
  {
    return first;
  }

  /** This function produces Java code that implements the class any object of
   *  which can be used at run time to represent this logical precondition.
  */
  public String getInitCode()
  {
    return le.getInitCode();
  }

  /** This function is used to set the number of variables for this logical
   *  precondition.
   *
   *  @param varCount
   *          the number of variables for this logical precondition.
  */
  public void setVarCount(int varCount)
  {
    le.setVarCount(varCount);
  }

  /** This function produces the Java code to create an object that represents
   *  this logical precondition at run time.
  */
  public String toCode()
  {
    return "(" + le.toCode() + ").setComparator(" + func + ")";
  }
}
