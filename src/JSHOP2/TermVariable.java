package JSHOP2;

/** Each variable symbol both at compile time and at run time, is an instance
 *  of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TermVariable extends Term
{
  /** Variable symbols are mapped to integers at compile time, and these
   *  integers are used thereafter to represent the variable symbols.
  */
  private int index;

  /** To represent the variable symbols that we know occur in the domain
   *  description, so that there will be no duplicate copies of those symbols.
   *  In other words, all variable symbols that represent the same thing in
   *  different places point to the corresponding element in this array at run
   *  time.
  */
  private static TermVariable[] variables;

  /** To initialize this variable symbol.
   *
   *  @param indexIn
   *          the integer associated with this variable symbol.
  */
  public TermVariable(int indexIn)
  {
    index = indexIn;
  }

  /** Since this term is a variable symbol, binding it basically means finding
   *  out whether or not this variable is already mapped to something in the
   *  input, and if so, returning the value this variable is mapped to.
  */
  public Term bind(Term[] binding)
  {
    //-- If this variable is already mapped to something in the input:
    if (binding[index] != null)
      return binding[index];

    //-- If this variable is not mapped to anything in the input, just return
    //-- the variable symbol itself.
    return this;
  }

  /** This function returns <code>false</code>.
  */
  public boolean equals(Term t)
  {
    return false;
  }

  /** Find a unifier between this variable symbol and another given term.
  */
  public boolean findUnifier(Term t, Term[] binding)
  {
    //-- If 't' is a variable symbol, skip it.
    if (t instanceof TermVariable)
      return true;

    //-- If the variable has not already been mapped to something, map it:
    if (binding[index] == null)
    {
      binding[index] = t;
      return true;
    }

    //-- If the variable has already been mapped to something, check if it is
    //-- unified with the same thing again.
    return t.equals(binding[index]);
  }

  /** To get the index for this variable symbol.
   *
   *  @return
   *          the integer associated with this variable symobl.
  */
  public int getIndex()
  {
    return index;
  }

  /** To return the correponding existing variable symbol.
   *
   *  @param index
   *          the index of the variable symbol to be returned.
   *  @return
   *          the corresponding existing variable symbol.
  */
  public static TermVariable getVariable(int index)
  {
    return variables[index];
  }

  /** To initialize an array of variable symbols that we know occur in the
   *  domain description, so that there will be no duplicate copies of those
   *  symbols. In other words, all variable symbols that represent the same
   *  thing in different places point to the corresponding element in this
   *  array at run time.
   *
   *  @param size
   *          the number of existing variable symbols.
  */
  public static void initialize(int size)
  {
    variables = new TermVariable[size];

    for (int i = 0; i < size; i++)
      variables[i] = new TermVariable(i);
  }

  /** This function always returns <code>false</code> because a variable symbol
   *  is never ground by definition.
  */
  public boolean isGround()
  {
    return false;
  }

  /** This function produces Java code to create this variable symbol as a
   *  term.
  */
  public String toCode()
  {
    return "TermVariable.getVariable(" + index + ")";
  }

  /** This function is used to print this variable symbol.
  */
  public String toString()
  {
    return "VAR" + index;
  }
}
