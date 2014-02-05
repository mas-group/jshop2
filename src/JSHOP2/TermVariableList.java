package JSHOP2;

/** Each variable symbol both at compile time and at run time, is an instance
 *  of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TermVariableList
{
  /** To represent the variable symbols that we know occur in the domain
   *  description, so that there will be no duplicate copies of those symbols.
   *  In other words, all variable symbols that represent the same thing in
   *  different places point to the corresponding element in this array at run
   *  time.
  */
  private TermVariable[] variables;

  /** To initialize an array of variable symbols that we know occur in the
   *  domain description, so that there will be no duplicate copies of those
   *  symbols. In other words, all variable symbols that represent the same
   *  thing in different places point to the corresponding element in this
   *  array at run time.
   *
   *  @param size
   *          the number of existing variable symbols.
  */
  public TermVariableList(int size)
  {
    variables = new TermVariable[size];

    for (int i = 0; i < size; i++)
      variables[i] = new TermVariable(i);
  }

  /** To return the correponding existing variable symbol.
   *
   *  @param index
   *          the index of the variable symbol to be returned.
   *  @return
   *          the corresponding existing variable symbol.
  */
  public TermVariable getVariable(int index)
  {
    return variables[index];
  }
}
