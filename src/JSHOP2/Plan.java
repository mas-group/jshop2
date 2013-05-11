package JSHOP2;

import java.util.LinkedList;

/** This class represent a plan as a <code>LinkedList</code> of ground
 *  instances of operators.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class Plan
{
  /** The cost of the plan. */
  private double cost;

  /** The new line character in the platform JSHOP2 is running on.
  */
  final static String endl = System.getProperty("line.separator");

  /** The plan as a <code>LinkedList</code> of ground instances of operators.
  */
  private LinkedList<Predicate> ops;

  /** To initialize the plan to an empty list.
  */
  public Plan()
  {
    ops = new LinkedList<Predicate>();
    cost = 0;
  }

  /** This function is used by objects of this class to clone themselves.
   *
   *  @param opsIn
   *          the operators in the plan.
   *  @param costIn
   *          the cost of the plan.
  */
  private Plan(LinkedList<Predicate> opsIn, double costIn)
  {
    ops = opsIn;
    cost = costIn;
  }

  /** To add an operator instance to the end of the plan.
   *
   *  @param op
   *          the operator the instance of which is being added.
   *  @param binding
   *          the binding to instantiate the operator.
   *  @return
   *          the cost of the operator instance being added.
  */
  public double addOperator(Operator op, Term[] binding)
  {
    ops.addLast(op.getHead().applySubstitution(binding));
    cost += op.getCost(binding);

    return op.getCost(binding);
  }

  /** To clone an object of this class.
  */
  public Object clone()
  {
    return new Plan(new LinkedList<Predicate>(ops), cost);
  }

  /** To get the sequence of operators represented by this object.
   *
   *  @return
   *          A <code>LinkedList</code> of operator instances in this plan.
  */
  public LinkedList<Predicate> getOps()
  {
    return ops;
  }

  /** To remove the operator instance at the end of the plan.
   *
   *  @param opCost
   *          the cost of the operator instance to be removed.
  */
  public void removeOperator(double opCost)
  {
    ops.removeLast();
    cost -= opCost;
  }

  /** This function returns a printable <code>String</code> representation of
   *  this plan.
   *
   *  @return
   *          the <code>String</code> representation of this plan.
  */
  public String toString()
  {
    //-- The value to be returned.
    String retVal = "Plan cost: " + cost + endl + endl;

    //-- Get the names of the operators in this domain.
    String[] primitiveTasks = JSHOP2.getDomain().getPrimitiveTasks();

    //-- Iterate over the operator instances in the plan and print them.
    for (Predicate p : ops)
      retVal += p.toString(primitiveTasks) + endl;

    return retVal + "--------------------" + endl;
  }
}
