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
  /** To represent the constant symbols that we already know exist, so that
   *  there will be no duplicate copies of those symbols. In other words, all
   *  constant symbols that represent the same thing in different places point
   *  to the corresponding element in this array at run time.
  */
  private static TermConstant[] constants;

  /** Constant symbols are mapped to integers at compile time, and these
   *  integers are used thereafter to represent the constant symbols.
  */
  private int index;

  /** To initialize this constant symbol.
   *
   *  @param indexIn
   *          the integer associated with this constant symbol.
  */
  public TermConstant(int indexIn)
  {
    index = indexIn;
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

  /** To return the correponding existing constant symbol.
   *
   *  @param index
   *          the index of the constant symbol to be returned.
   *  @return
   *          the corresponding existing constant symbol.
  */
  public static TermConstant getConstant(int index)
  {
    return constants[index];
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

  /** To initialize an array of constant symbols that we already know exist, so
   *  that there will be no duplicate copies of those symbols. In other words,
   *  all constant symbols that represent the same thing in different places
   *  point to the corresponding element in this array at run time.
   *
   *  @param size
   *          the number of existing constant symbols.
  */
  public static void initialize(int size)
  {
    constants = new TermConstant[size];

    for (int i = 0; i < size; i++)
      constants[i] = new TermConstant(i);
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
    return "TermConstant.getConstant(" + index + ")";
  }

  /** Constant symbols are mapped at compile time to integers, this function,
   *  for printing purposes, maps them back to the strings they were before.
  */
  public String toString()
  {
    return JSHOP2.getDomain().getConstant(index);
  }
}
