package JSHOP2;

/** Each domain element (i.e., method, operator, or axiom) at compile time is
 *  represented as an instance of a class derived from this abstract class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract class InternalElement extends CompileTimeObject
{
  /** The number of objects already instantiated from this class before this
   *  object was instantiated. This is used as a unique identifier for this
   *  object to distinguish it from the other objects of this class.
  */
  private int cnt;

  /** Every element has a head, which is a predicate.
  */
  private Predicate head;

  /** To initialize this internal domain element.
   *
   *  @param headIn
   *          head of this element.
   *  @param cntIn
   *          index of this element in the domain description.
  */
  public InternalElement(Predicate headIn, int cntIn)
  {
    head = headIn;
    cnt = cntIn;
  }

  /** To get the number of objects already instantiated from this class before
   *  this object was instantiated. This is used as a unique identifier for
   *  this object to distinguish it from the other objects of this class.
   *
   *  @return
   *          the number of objects already instantiated from this class before
   *          this object was instantiated.
  */
  public int getCnt()
  {
    return cnt;
  }

  /** To get the head of this internal domain element.
   *
   *  @return
   *          the head of this internal domain element.
  */
  public Predicate getHead()
  {
    return head;
  }
}
