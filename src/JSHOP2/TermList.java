package JSHOP2;

/** Each list both at compile time and at run time, is an instance of this
 *  class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TermList extends Term
{
  /** The list this term is representing.
  */
  private List list;

  /** The <code>NIL</code> term.
  */
  public static TermList NIL = new TermList(null, null);

  /** Provides access to the JSHOP2 core algorithm.
  */
  private JSHOP2 jshop2;

  /** To initialize this list term.
   *
   *  @param listIn
   *          the list this term represents.
   *  @param jshop2In
   *          provides access to the JSHOP2 core algorithm.
  */
  public TermList(List listIn, JSHOP2 jshop2In)
  {
    list = listIn;
    jshop2 = jshop2In;
  }

  /** To initialize this list term.
   *
   *  @param headIn
   *          the head of the list this term represents.
   *  @param tailIn
   *          the tail of the list this term represents.
   *  @param jshop2In
   *          provides access to the JSHOP2 core algorithm.
  */
  public TermList(Term headIn, Term tailIn, JSHOP2 jshop2In)
  {
    list = new List(headIn, tailIn);
    jshop2 = jshop2In;
  }

  /** To apply a given binding to the list this term represents.
  */
  public Term bind(Term[] binding)
  {
    if (list != null)
      return new TermList(list.bindList(binding), jshop2);
    else
      return NIL;
  }

  /** Whether or not another term is equivalent to this list term.
  */
  public boolean equals(Term t)
  {
    //-- 't' is not a list.
    if (!(t instanceof TermList))
      return false;

    //-- Both terms are representing NIL, so they are equal.
    if (list == null && ((TermList)t).list == null)
      return true;

    //-- Only one of the terms represents NIL, so they are not equal.
    if (list == null || ((TermList)t).list == null)
      return false;

    //-- Compare the lists.
    return (list.equals(((TermList)t).list));
  }

  /** Find a unifier between this list and another given term.
  */
  public boolean findUnifier(Term t, Term[] binding)
  {
    //-- If 't' is a variable symbol, ignore it.
    if (t instanceof TermVariable)
      return true;

    //-- If 't' is not a list, it can not be unified with this term.
    if (!(t instanceof TermList))
      return false;

    //-- If this list represents NIL, the only way to unify 't' with it would
    //-- be for 't' to represent NIL too.
    if (list == null)
      return (((TermList)t).list == null);

    //-- Unify the lists.
    return list.findUnifierList(((TermList)t).list, binding);
  }

  /** To get the list this term represents.
   *
   *  @return
   *          the list this term represents.
  */
  public List getList()
  {
    return list;
  }

  /** Whether or not there are any variables in the list this term represents.
  */
  public boolean isGround()
  {
    return (list == null || list.isGroundList());
  }

  /** This function produces Java code to create this list term.
  */
  public String toCode()
  {
    if (list == null)
      return "TermList.NIL";

    return "new TermList(" + list.getHead().toCode() + ", " +
           list.getTail().toCode() + ", jshop2)";
  }

  /** This function converts this list term to a predicate.
   *
   *  @param varCount
   *          number of variables in the resulting predicate.
   *  @return
   *          the resulting predicate.
  */
  public Predicate toPredicate(int varCount)
  {
    return new Predicate(((TermConstant)list.getHead()).getIndex(), varCount,
                         list.getTail(), jshop2);
  }

  /** This function is used to print this list term.
  */
  public String toString()
  {
    if (list == null)
      return "NIL";

    return "(" + list.toString() + ")";
  }
}
