package JSHOP2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/** This abstract class represents an iterator over all the possible bindings
 *  that can satisfy its corresponding logical expression at run time.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract class Precondition
{

  /** The array that stores the sorted satisfiers for this logical precondition
   *  if this is a <code>:sort-by</code> logical precondition.
  */
  Term[][] bindings;

  /** The index pointing to the next satisfier to be returned in case this is a
   *  <code>:sort-by</code> logical precondition.
  */
  int bindingsIdx;

  /** The function to be used to sort the possible bindings if this is a
   *  <code>:sort-by</code> logical precondition.
  */
  private Comparator<Term[]> comp;

  /** Whether or not this logical expression is marked <code>:first</code>.
  */
  private boolean first;

  /** Whether or not this is the first time the <code>nextBinding</code>
   *  function is called for this object after the latest call to its
   *  <code>reset</code> function.
  */
  private boolean firstCall;

  /** This abstract function binds the logical expression to a given binding.
   *
   *  @param binding
   *          the given binding.
  */
  public abstract void bind(Term[] binding);

  /** This function returns the next satisfier for this logical expression.
   *
   *  @return
   *          the next satisfier as an array of terms, each element of which
   *          is either what its corresponding variable should be mapped to,
   *          or <code>null</code> when that variable is not mapped under this
   *          satisfier, <code>null</code> if there is no such satisfier.
  */
  public Term[] nextBinding()
  {
    //-- If the expression is marked ':first' but this is not the first time
    //-- this function is called, return null.
    if (first && !firstCall)
      return null;

    //-- If this is a :sort-by logical precondition,
    if (comp != null)
    {
      //-- If this is the first call to this function, calculate all the
      //-- bindings first, and sort them using the given function.
      if (firstCall)
      {
        //-- A vector to store all the possible bindings that satisfy this
        //-- logical precondition.
        Vector<Term[]> v = new Vector<Term[]>();

        //-- The next possible binding.
        Term[] b;

        //-- Iterate over all the possible bindings.
        while ((b = nextBindingHelper()) != null)
          v.add(b);

        //-- Sort the bindings.
        bindings = new Term[v.size()][];
        bindings = v.toArray(bindings);
        Arrays.sort(bindings, comp);

        //-- Set the index to zero.
        bindingsIdx = 0;
      }

      //-- Make sure next time this function will remember that it has been
      //-- called before.
      firstCall = false;

      //-- If all the possible bindings have been returned, return null.
      if (bindingsIdx == bindings.length)
        return null;

      //-- Return the next binding in the sorted list of satisfiers.
      return (Term[])bindings[bindingsIdx++];
    }

    //-- Make sure next time this function will remember that it has been
    //-- called before.
    firstCall = false;

    //-- Call the helper function to do the actual, subclass-specific work.
    return nextBindingHelper();
  }

  /** This abstract function is called by the <code>nextBinding</code> function
   *  and does the subclass-specific part of the finding the next binding.
  */
  protected abstract Term[] nextBindingHelper();

  /** This function resets this iterator so that it can iterate over again.
   *  Note that this will undo any bindings and/or iterations done before.
  */
  public void reset()
  {
    //-- This is start of a new life span, so this object should forget whether
    //-- or not it has been asked for a satisfier before.
    firstCall = true;

    //-- Call the helper function to do the actual, subclass-specific work.
    resetHelper();
  }

  /** This abstract function is called by the <code>reset</code> function and
   *  does the subclass-specific part of the resetting.
  */
  protected abstract void resetHelper();

  /** To set the function to be used to sort the possible bindings that satisfy
   *  this logical precondition.
   *
   *  @param compIn
   *          the function to be used to sort the possible bindings in case
   *          this is a <code>:sort-by</code> logical precondition,
   *          <code>null</code> otherwise.
   *  @return
   *          this object.
  */
  public Precondition setComparator(Comparator<Term[]> compIn)
  {
    comp = compIn;

    return this;
  }

  /** To set whether or not this function is marked <code>:fist</code>.
   *
   *  @param firstIn
   *          <code>true</code> if this logical expression is marked
   *          <code>:first</code>, <code>false</code> otherwise.
  */
  public void setFirst(boolean firstIn)
  {
    first = firstIn;
  }
}
