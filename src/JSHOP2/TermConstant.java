package JSHOP2;

/** Each constant symbol, both at compile time and at run time, is an instance
 *  of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TermConstant extends Term
{
  /** Constant symbols are mapped to integers at compile time, and these
   *  integers are used thereafter to represent the constant symbols.
  */
  private int index;

  /** Provides access to the JSHOP2 core algorithm.
  */
  private JSHOP2 jshop2;

  /** To initialize this constant symbol.
   *
   *  @param indexIn
   *          the integer associated with this constant symbol.
   *  @param jshop2In
   *          provides access to the JSHOP2 core algorithm.
  */
  public TermConstant(int indexIn, JSHOP2 jshop2In)
  {
    index = indexIn;
    jshop2 = jshop2In;
  }

  /** Since this term is a constant symbol, binding will not change it,
   *  therefore, simply this constant symbol itself is returned.
  */
  public Term bind(Term[] binding)
  {
    return this;
  }

  /** Whether or not another term is equivalent to this constant symbol.
  */
  public boolean equals(Term t)
  {
    //-- 't' is not a constant symbol.
    if (!(t instanceof TermConstant))
      return false;

    //-- Check if 't' is the same constant symbol.
    return (index == ((TermConstant)t).index);
  }

  /** Find a unifier between this constant symbol and another given term. Since
   *  this term is a constant symbol, this boils down to whether or not the
   *  other given term is equal to this one.
  */
  public boolean findUnifier(Term t, Term[] binding)
  {
    return ((t instanceof TermVariable) || equals(t));
  }

  /** To get the index for this constant symbol.
   *
   *  @return
   *          the integer associated with this constant symobl.
  */
  public int getIndex()
  {
    return index;
  }

  /** This function always returns <code>true</code> because a constant symbol
   *  is always ground by definition.
  */
  public boolean isGround()
  {
    return true;
  }

  /** This function produces Java code to create this constant symbol as a
   *  term.
  */
  public String toCode()
  {
    return "termConstants.getConstant(" + index + ")";
  }

  /** Constant symbols are mapped at compile time to integers, this function,
   *  for printing purposes, maps them back to the strings they were before.
  */
  public String toString()
  {
    return jshop2.getDomain().getConstant(index);
  }
}
