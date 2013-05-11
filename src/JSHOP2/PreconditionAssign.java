package JSHOP2;

/** This class represents an iterator over all the possible bindings that can
 *  satisfy an assignment logical expression at run time. Note that in this
 *  there is only one such binding.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class PreconditionAssign extends Precondition
{
  /** The term this assignment logical expression represents, after all the
   *  bindings are applied.
  */
  private Term boundT;

  /** The array this object will return as its next binding.
  */
  private Term[] retVal;

  /** The term this assignment logical expression represents, without any
   *  subsequent bindings applied to it.
  */
  private Term term;

  /** The index of the variable to be given a value under this assignment
   *  logical expression.
  */
  private int whichVar;

  /** To initialize this assigment logical expression.
   *
   *  @param termIn
   *          the term this assigment logical expression represents.
   *  @param unifier
   *          the current unifier.
   *  @param whichVarIn
   *          the index of the variable to be given a value under this
   *          assignment logical expression.
  */
  public PreconditionAssign(Term termIn, Term[] unifier, int whichVarIn)
  {
    //-- An assignment logical expression can be satisfied only once, so
    //-- pretend that this assignment logical expression is marked ':first'.
    setFirst(true);

    term = termIn.bind(unifier);

    //-- Initially, this object is not binding any variable, so set all the
    //-- elements of 'retVal' to null.
    retVal = new Term[unifier.length];
    for (int i = 0; i < unifier.length; i++)
      retVal[i] = null;

    whichVar = whichVarIn;
  }

  /** To bind the assignment logical expression to some binding.
  */
  public void bind(Term[] binding)
  {
    boundT = boundT.bind(binding);
  }

  /** To return the next satisfier for this assignment logical expression.
  */
  protected Term[] nextBindingHelper()
  {
    //-- Assign the variable to what it is supposed to be assigned to.
    retVal[whichVar] = boundT;

    return retVal;
  }

  /** To reset this assignment logical expression.
  */
  protected void resetHelper()
  {
    //-- Undo the bindings.
    boundT = term;
  }
}
