package JSHOP2;

/** Each logical expression at compile time is represented as a class derived
 *  from this abstract class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract class LogicalExpression extends CompileTimeObject
{
  /** The number of times function <code>getClassCnt()</code> is called before.
  */
  private static int classCnt = 0;

  /** The number of variables in this logical expression, used to determine
   *  the size of bindings when unifiers are calculated.
  */
  private int varCount;

  /** This function returns a unique integer every time called. This unique
   *  integer is used at compile time to make names of classes that implement
   *  preconditions at run time unique.
   *
   *  @return
   *          the unique integer.
  */
  public int getClassCnt()
  {
    return classCnt++;
  }

  /** This function produces Java code that initializes some data structures
   *  that will be needed to create the precondition object that implements
   *  this logical expression at run time.
   *
   *  @return
   *          the produced code as a <code>String</code>.
  */
  public abstract String getInitCode();

  /** This function returns the number of variables in this logical expression.
   *
   *  @return
   *          the number of variables in this logical expression.
  */
  public int getVarCount()
  {
    return varCount;
  }

  /** This abstract function is called whenever the number of variables for an
   *  object of this class is set. Classes that extend this class should
   *  implement this function accordingly in order to update their own data
   *  structures where they hold this information.
   *
   *  @param varCountIn
   *          the number of variables for this logical expression.
  */
  protected abstract void propagateVarCount(int varCountIn);

  /** This function is used to set the number of variables for this logical
   *  expression.
   *
   *  @param varCountIn
   *          the number of variables for this logical expression.
  */
  public void setVarCount(int varCountIn)
  {
    varCount = varCountIn;

    propagateVarCount(varCountIn);
  }
}
