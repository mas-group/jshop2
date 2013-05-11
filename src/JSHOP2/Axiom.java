package JSHOP2;

/** Each axiom at run time is represented as a class derived from this abstract
 *  class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract class Axiom extends DomainElement
{
  /** Number of branches of this axiom. Each branch represents one different
   *  way to prove this axiom, in case all the previous branches have already
   *  been tried and failed.
  */
  private int branchSize;

  /** To initialize the axiom.
   *
   *  @param head
   *          head of the axiom.
   *  @param branchSizeIn
   *          number of branches in the axiom.
  */
  public Axiom(Predicate head, int branchSizeIn)
  {
    super(head);
    branchSize = branchSizeIn;
  }

  /** To get the number of branches in this axiom.
   *
   *  @return
   *          number of branches in this axiom.
  */
  public int getBranchSize()
  {
    return branchSize;
  }

  /** To get the label of a given branch of this axiom.
   *
   *  @param which
   *          the branch the label of which is to be returned.
   *  @return
   *          the label for that branch.
  */
  public abstract String getLabel(int which);
}
