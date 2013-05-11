package JSHOP2;

/** This class represents an iterator over all the possible bindings that can
 *  satisfy a call logical expression at run time. Note that in this case there
 *  is at most one such binding.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class PreconditionCall extends Precondition
{
  /** The term this call logical expression represents, after all the bindings
   *  are applied.
  */
  private Term boundT;

  /** The array this object will return as its next binding.
  */
  private Term[] retVal;

  /** The term this call logical expression represents, without any subsequent
   *  bindings applied to it.
  */
  private Term term;

  /** To initialize this call logical expression.
   *
   *  @param termIn
   *          the term this call logical expression represents.
   *  @param unifier
   *          the current unifier.
  */
  public PreconditionCall(TermCall termIn, Term[] unifier)
  {
    //-- A call term can be satisfied only once, so pretend that this call term
    //-- is marked ':first'.
    setFirst(true);

    term = termIn.bind(unifier);

    //-- Initially, this object is not binding any variable, so set all the
    //-- elements of 'retVal' to null.
    retVal = new Term[unifier.length];
    for (int i = 0; i < unifier.length; i++)
      retVal[i] = null;
  }

  /** To bind the call logical expression to some binding.
  */
  public void bind(Term[] binding)
  {
    boundT = boundT.bind(binding);
  }

  /** To return the next satisfier for this call logical expression.
  */
  protected Term[] nextBindingHelper()
  {
    //-- This behavior is inherited from LISP. A NIL object represents a
    //-- logical falsehood, while everything else represnts true.
    if (boundT.isNil())
      return null;

    return retVal;
  }

  /** To reset this call logical expression.
  */
  protected void resetHelper()
  {
    //-- Undo the bindings.
    boundT = term;
  }
}
