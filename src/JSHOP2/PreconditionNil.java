package JSHOP2;

/** This class represents an iterator over all the possible bindings that can
 *  satisfy an empty logical expression at run time. Note that in this case
 *  there is only one such binding, and that is the empty binding.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class PreconditionNil extends Precondition
{
  /** The array this object will return as its next binding.
  */
  private Term[] retVal;

  /** To initialize this empty logical expression.
   *
   *  @param vars
   *          the number of variables the current logical expression has. This
   *          is used to return a binding of appropriate size.
  */
  public PreconditionNil(int vars)
  {
    //-- An empty logical expression can be satisfied only once, so pretend
    //-- that this empty logical expression is marked ':first'.
    setFirst(true);

    //-- An empty logical expression never binds any variable to anything.
    //-- Therefore, only an empty binding should be returned by this object.
    retVal = new Term[vars];
    for (int i = 0; i < vars; i++)
      retVal[i] = null;
  }

  /** Since this is an empty logical expression, this function does nothing.
  */
  public void bind(Term[] binding)
  {
  }

  /** To return the next satisfier for this empty logical expression.
  */
  protected Term[] nextBindingHelper()
  {
    //-- Return an empty binding.
    return retVal;
  }

  /** To reset this empty logical expression.
  */
  protected void resetHelper()
  {
  }
}
