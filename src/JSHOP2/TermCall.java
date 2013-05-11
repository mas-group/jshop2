package JSHOP2;

/** Each call term, both at compile time and at run time, is an instance of
 *  this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TermCall extends Term
{
  /** The list that represents the arguments of the call term. Using LISP
   *  terminology, this is equal to <code>(CDR ct)</code> where <code>ct</code>
   *  is this call term.
  */
  private List args;

  /** The Java implementation of the function that is called in this call term.
  */
  private Calculate calculate;

  /** The name of the function that is called in this call term as a
   *  <code>String</code>.
  */
  private String func;

  /** To initialize this call term. This constructor is used at compile time.
   *
   *  @param argsIn
   *          the argument list.
   *  @param funcIn
   *          the name of the function to be applied.
  */
  public TermCall(List argsIn, String funcIn)
  {
    args = argsIn;
    func = funcIn;
  }

  /** To initialize this call term. This constructor is used at run time.
   *
   *  @param argsIn
   *          the argument list.
   *  @param calculateIn
   *          the Java implementation of the function to be applied.
   *  @param funcIn
   *          the name of the function to be applied.
  */
  public TermCall(List argsIn, Calculate calculateIn, String funcIn)
  {
    args = argsIn;
    calculate = calculateIn;
    func = funcIn;
  }

  /** To apply a given binding to the list of arguments of the function call.
  */
  public Term bind(Term[] binding)
  {
    List boundArgs = args.bindList(binding);

    //-- As soon as all the variables are bound, replace the call term with
    //-- the result of the code call.
    if (boundArgs.isGroundList())
      return calculate.call(boundArgs);

    //-- Not all the variables are bound yet, therefore, the code call can not
    //-- be executed.
    return new TermCall(boundArgs, calculate, func);
  }

  /** Whether or not another term is equivalent to the result of this call
   *  term.
  */
  public boolean equals(Term t)
  {
    return calculate.call(args).equals(t);
  }

  /** Find a unifier between the result of this call term and another given
   *  term.
  */
  public boolean findUnifier(Term t, Term[] binding)
  {
    return calculate.call(args).findUnifier(t, binding);
  }

  /** Check the argument list of this call term for variables.
  */
  public boolean isGround()
  {
    return args.isGroundList();
  }

  /** This function produces Java code to create this call term.
  */
  public String toCode()
  {
    return "new TermCall(" + args.toCode() + ", " + func + ", " + "\"" + func
            + "\"" + ")";
  }

  /** This function is used to print this call term.
  */
  public String toString()
  {
    return "(CALL " + func + " " + args.toString() + ")";
  }
}
