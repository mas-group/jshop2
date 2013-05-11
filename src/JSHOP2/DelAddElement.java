package JSHOP2;

import java.util.Vector;

/** Each element in the delete/add list of an operator both at compile time and
 *  run time is represented as an instance of a class derived from this
 *  abstract class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract class DelAddElement extends CompileTimeObject
{
  /** What to do when an operator has this element in its add list and it is
   *  being applied.
   *
   *  @param s
   *          the current state of the world.
   *  @param binding
   *          the binding to be applied before adding the element.
   *  @param delAddList
   *          an array of size 4 of atoms and protections deleted and added.
   *          This is useful when a backtrack happens: Added atoms and
   *          protections are retracted, and deleted atoms and protections are
   *          added back to change the state of the world to what it was before
   *          backtracked decision was made. The 4 elements of the array are
   *          the deleted atoms, the added atoms, the deleted protections, and
   *          the added protections repectively.
  */
  public abstract void add(State s, Term[] binding, Vector[] delAddList);

  /** What to do when an operator has this element in its delete list and it is
   *  being applied.
   *
   *  @param s
   *          the current state of the world.
   *  @param binding
   *          the binding to be applied before deleting the element.
   *  @param delAddList
   *          an array of size 4 of atoms and protections deleted and added.
   *          This is useful when a backtrack happens: Added atoms and
   *          protections are retracted, and deleted atoms and protections are
   *          added back to change the state of the world to what it was before
   *          backtracked decision was made. The 4 elements of array are the
   *          the deleted atoms, added atoms, deleted protections and added
   *          protections in that order.
   *  @return
   *          <code>true</code> if the atom(s) associated with this
   *          delete/add element were deleted, <code>false</code> otherwise,
   *          i.e., when at least one of the atoms to be deleted was protected.
   *          If this function returns <code>false</code>, it means the
   *          operator has failed and should be backtracked.
  */
  public abstract boolean del(State s, Term[]binding, Vector[] delAddList);

  /** To set the number of variables in this delete/add element. It is used
   *  when returning a binding since a binding is assumed to be an array of
   *  this size. This function must be called at compile time, since at run
   *  time the code to calculate the bindings has already been produced.
   *
   *  @param varCount
   *          the number of variables.
  */
  public abstract void setVarCount(int varCount);
}
