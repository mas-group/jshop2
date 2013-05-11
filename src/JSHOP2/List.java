package JSHOP2;

import java.util.LinkedList;

/** Each list, both at compile time and at run time, is an instance of this
 *  class. Lists are represented the same way they are represented in
 *  <code>LISP</code>, i.e., as a structure consisting of two
 *  <code>CONS</code>, where each <code>CONS</code> can be a list itself.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class List extends CompileTimeObject
{
  /** The head of this list, a term.
  */
  private Term head;

  /** The tail of this list, a term.
  */
  private Term tail;

  /** To initialize this list.
   *
   *  @param headIn
   *          the term to be the head of this list.
   *  @param tailIn
   *          the term to be the tail of this list.
  */
  public List(Term headIn, Term tailIn)
  {
    head = headIn;
    tail = tailIn;
  }

  /** To append another term to the end of this list. Note that this function
   *  is destructive, i.e., it appends the given term to this list, and not a
   *  copy of it.
   *
   *  @param t
   *          the term to be appended to this list.
   *  @return
   *          the result of appending.
  */
  public List append(Term t)
  {
    List l = this;

    while (!l.tail.isNil())
      l = l.getRest();

    //-- Replace the NIL tail with 't'.
    l.tail = t;

    return this;
  }

  /** To apply a given binding to this list. Note that this function does NOT
   *  change this list, rather, it creates a new list which is the result of
   *  binding.
   *
   *  @param binding
   *          an array of terms, indexed by the integers associated with
   *          variable symbols.
   *  @return
   *          the result of binding.
  */
  public List bindList(Term[] binding)
  {
    return new List(head.bind(binding), tail.bind(binding));
  }

  /** Whether or not another list is equivalent to the this list.
   *
   *  @param t
   *          the list being compared to the this list.
   *  @return
   *          <code>true</code> if the two lists are equal, <code>false</code>
   *          otherwise.
  */
  public boolean equals(List t)
  {
    return head.equals(t.head) && tail.equals(t.tail);
  }

  /** Find a unifier that will bind this list to another given list.
   *
   *  @param l
   *          the list with which we are finding a unifier.
   *  @param binding
   *          the unifier to be returned.
   *  @return
   *          <code>false</code> if the two lists can not be unified,
   *          <code>true</code> otherwise.
  */
  public boolean findUnifierList(List l, Term[] binding)
  {
    //-- If 'l' is null, there will be no unifier.
    if (l == null)
      return false;

    return head.findUnifier(l.head, binding) &&
           tail.findUnifier(l.tail, binding);
  }

  /** To get the head of this list, a term.
   *
   *  @return
   *          the head of this list, a term.
  */
  public Term getHead()
  {
    return head;
  }

  /** To get the tail of this list as another list, assuming that the tail is
   *  indeed a list.
   *
   *  @return
   *          the tail of this list as another list.
  */
  public List getRest()
  {
    return ((TermList)tail).getList();
  }

  /** To get the tail of this list, a term.
   *
   *  @return
   *          the tail of this list, a term.
  */
  public Term getTail()
  {
    return tail;
  }

  /** Is this list ground (i.e., has no variables)?
   *
   *  @return
   *          <code>true</code> if this list is ground, <code>false</code>
   *          otherwise.
  */
  public boolean isGroundList()
  {
    return head.isGround() && tail.isGround();
  }

  /** This function gets a <code>LinkedList</code> of terms as input and
   *  returns a list the elements of which are the terms in the input
   *  <code>LinkedList</code> in the reverse order.
   *
   *  @param listIn
   *          the input <code>LinkedList</code>.
   *  @return
   *          the resulting list.
   *
  */
  static public List MakeList(LinkedList<Term> listIn)
  {
    List retVal = null;

    //-- Iterate over the linked list, add each element to the head of the
    //-- return value.
    for (Term t : listIn)
      retVal = new List(t, new TermList(retVal));

    return retVal;
  }

  /** This function is used to print this list.
  */
  public void print()
  {
    System.out.println("(" + this + ")");
  }

  /** This function is used at compile time to produce Java code that when run,
   *  will create this list.
  */
  public String toCode()
  {
    return "new List(" + head.toCode() + ", " + tail.toCode() + ")";
  }

  /** This function is used to print the value of this list.
   *
   *  @return
   *          the printable <code>String</code> that shows the value of this
   *          list.
  */
  public String toString()
  {
    //-- If tail is a list itself,
    if (tail instanceof TermList)
    {
      if (tail.isNil())
        //-- Converting "(a . NIL) to "(a)"
        return head.toString();
      else
      {
        //-- Converting "(a . (b)) to "(a b)"
        String s = tail.toString();

        return head.toString() + " " + s.substring(1, s.length() - 1);
      }
    }
    //-- If tail is not a list,
    else
      return head.toString() + " . " + tail.toString();
  }
}
