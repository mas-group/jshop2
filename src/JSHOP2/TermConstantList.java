package JSHOP2;

/** Each constant symbol, both at compile time and at run time, is an instance
 *  of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TermConstantList
{
  /** To represent the constant symbols that we already know exist, so that
   *  there will be no duplicate copies of those symbols. In other words, all
   *  constant symbols that represent the same thing in different places point
   *  to the corresponding element in this array at run time.
  */
  private TermConstant[] constants;


  /** To initialize an array of constant symbols that we already know exist, so
   *  that there will be no duplicate copies of those symbols. In other words,
   *  all constant symbols that represent the same thing in different places
   *  point to the corresponding element in this array at run time.
   *
   *  @param size
   *          the number of existing constant symbols.
  */
  public TermConstantList(int size, JSHOP2 jshop2In)
  {
    constants = new TermConstant[size];

    for (int i = 0; i < size; i++)
      constants[i] = new TermConstant(i, jshop2In);
  }

  /** To return the correponding existing constant symbol.
   *
   *  @param index
   *          the index of the constant symbol to be returned.
   *  @return
   *          the corresponding existing constant symbol.
  */
  public TermConstant getConstant(int index)
  {
    return constants[index];
  }
}
