package JSHOP2;

/** This class represents an iterator over all the possible bindings that can
 *  satisfy a <code>ForAll</code> logical expression at run time. Note that in
 *  this case there is at most one such binding, and that is the empty binding.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class PreconditionForAll extends Precondition
{
  /** The consequence of the <code>ForAll</code> logical expression this object
   *  represents.
  */
  private Precondition consequence;

  /** To keep track of the bindings seen so far.
  */
  private Term[] currentBinding;

  /** The premise of the <code>ForAll</code> logical expression this object
   *  represents.
  */
  private Precondition premise;

  /** The array this object will return as its next binding.
  */
  private Term[] retVal;

  /** To initialize this <code>ForAll</code> logical expression.
   *
   *  @param premiseIn
   *          the premise of the <code>ForAll</code> logical expression this
   *          object represents.
   *  @param consequenceIn
   *          the consequence of the <code>ForAll</code> logical expression
   *          this object represents.
   *  @param vars
   *          the number of variables this logical expression has. This is used
   *          to return a binding of the appropriate size.
  */
  public PreconditionForAll(Precondition premiseIn,
                            Precondition consequenceIn, int vars)
  {
    //-- A ForAll logical expression can be satisfied only once, so pretend
    //-- that this Forall logical expression is marked ':first'.
    setFirst(true);

    premise = premiseIn;
    consequence = consequenceIn;

    //-- A ForAll logical expression never binds any variable to anything. It
    //-- is merely a check if something is true or not. Therefore, only an
    //-- empty binding should be returned by this object.
    retVal = new Term[vars];
    for (int i = 0; i < vars; i++)
      retVal[i] = null;

    //-- Allocate the memory needed to store the bindings seen so far.
    currentBinding = new Term[vars];
  }

  /** To bind the <code>ForAll</code> logical expression to some binding.
  */
  public void bind(Term[] binding)
  {
    premise.bind(binding);

    //-- Keep track of the bindings seen so far.
    Term.merge(currentBinding, binding);
  }

  /** To return the next satisfier for this <code>ForAll</code> logical
   *  expression, which is either <code>null</code> or an empty binding.
  */
  protected Term[] nextBindingHelper()
  {
    //-- To store the premise satisfier being examined.
    Term[] satisfier;

    //-- For each binding that satisfies the premise,
    while ((satisfier = premise.nextBinding()) != null)
    {
      //-- Merge the satisfier with the bindings seen so far.
      Term.merge(satisfier, currentBinding);

      //-- Reset the consequence
      consequence.reset();

      //-- Apply the result of merging two bindings to the consequence.
      consequence.bind(satisfier);

      //-- See if the consequnce is satisfiable.
      if (consequence.nextBinding() == null)
        //-- If not, return null, meaning that the ForAll logical expression
        //-- does not hold.
        return null;
    }

    //-- Return the empty binding, meaning that the ForAll logical expression
    //-- holds.
    return retVal;
  }

  /** To reset this <code>ForAll</code> logical expression.
  */
  protected void resetHelper()
  {
    //-- Reset the premise of the ForAll logical expression this object
    //-- represents.
    premise.reset();

    //-- Reset the bindings seen so far to an empty binding.
    for (int i = 0; i < currentBinding.length; i++)
      currentBinding[i] = null;
  }
}
