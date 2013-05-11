package JSHOP2;

/** Each task atom both at compile time and at run time is represented as an
 *  object of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TaskAtom extends CompileTimeObject
{
  /** The task atom itself.
  */
  private Predicate head;

  /** Whether or not this task atom is marked <code>:immediate</code>.
  */
  private boolean immediate;

  /** Whether this task atom is a primitive one.
  */
  private boolean primitive;

  /** To initialize this task atom.
   *
   *  @param headIn
   *          the task atom itself.
   *  @param immediateIn
   *          whether or not this task atom is marked <code>:immediate</code>.
   *  @param primitiveIn
   *          whether or not this task atom is a primitive one.
  */
  public TaskAtom(Predicate headIn, boolean immediateIn, boolean primitiveIn)
  {
    head = headIn;
    immediate = immediateIn;
    primitive = primitiveIn;
  }

  /** To calculate the result of applying a given binding to this task atom.
   *
   *  @param binding
   *          the binding to be applied.
   *  @return
   *          the result of the binding.
  */
  public TaskAtom bind(Term[] binding)
  {
    return new TaskAtom(head.applySubstitution(binding), immediate, primitive);
  }

  /** To get the head of this task atom.
   *
   *  @return
   *          the head of this task atom.
  */
  public Predicate getHead()
  {
    return head;
  }

  /** To check if this task atom is marked <code>:immediate</code>.
   *
   *  @return
   *          <code>true</code> if the task atom is marked
   *          <code>:immediate</code>, <code>false</code> otherwise.
  */
  public boolean isImmediate()
  {
    return immediate;
  }

  /** To check if this task atom is primitive.
   *
   *  @return
   *          <code>true</code> if the task atom is primitive,
   *          <code>false</code> otherwise.
  */
  public boolean isPrimitive()
  {
    return primitive;
  }

  /** This function is used to print this task atom.
  */
  public void print()
  {
    System.out.println(this);
  }

  /** This function produces Java code to create this task atom.
  */
  public String toCode()
  {
    return "new TaskAtom(" + head.toCode() + ", " + immediate + ", " +
           primitive + ")";
  }

  /** This function is used to produce a printable <code>String</code> showing
   *  the value of this task atom.
   *
   *  @return
   *          the printable <code>String</code> that shows the value of this
   *          task atom.
  */
  public String toString()
  {
    String s;
    if (primitive)
      s = head.toString(JSHOP2.getDomain().primitiveTasks);
    else
      s = head.toString(JSHOP2.getDomain().compoundTasks);

    if (immediate)
      return "(:immediate " + s.substring(1);
    else
      return s;
  }
}
