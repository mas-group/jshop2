package JSHOP2;

/** This abstract class implements the basic common functionality of the all
 *  possible elements (i.e., methods, operators, and axioms) of a domain at
 *  run time.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract class DomainElement
{
  /** Every element has a head, which is a predicate.
  */
  private Predicate head;

  /** To initialize the domain element.
   *
   *  @param headIn
   *          head of the domain element.
  */
  public DomainElement(Predicate headIn)
  {
    head = headIn;
  }

  /** This function returns the head of this domain element.
   *
   *  @return
   *          the head of this element, which is a predicate.
  */
  public Predicate getHead()
  {
    return head;
  }

  /** This abstract function returns a handle that can be used to calculate,
   *  one by one, the bindings that satisfy the precondition of this domain
   *  element in a given state of the world with respect to a given binding.
   *
   *  @param binding
   *          current binding.
   *  @param which
   *          which precondition to use (ignored if this element is an
   *          operator, since operators have only one precondition).
   *  @return
   *          an object of type Precondition which can be used later on to get
   *          the bindings one by one.
  */
  public abstract Precondition getIterator(Term[] binding, int which);

  /** This function returns the substitution that unifies the head of this
   *  element with a given predicate.
   *
   *  @param p
   *          input predicate
   *  @return
   *          an array that shows with what each variable should be substituted.
  */
  public Term[] unify(Predicate p)
  {
    return head.findUnifier(p.getParam());
  }
}
