package JSHOP2;

import java.util.Vector;

/** Each atomic element in the delete/add list of an operator both at compile
 *  time and run time is represented as an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class DelAddAtomic extends DelAddElement
{
  /** The atom to be deleted/added.
  */
  private Predicate atom;

  /** To initialize this atomic delete/add element.
   *
   *  @param atomIn
   *          the atom to be deleted/added.
  */
  public DelAddAtomic(Predicate atomIn)
  {
    atom = atomIn;
  }

  /** To add this atomic delete/add element to the current state of the world.
  */
  public void add(State s, Term[] binding, Vector[] delAddList)
  {
    //-- Apply the binding (and execute the possible code calls) first.
    Predicate p = atom.applySubstitution(binding);

    //-- Try to add the atom to the current state of the world.
    if (s.add(p))
      //-- If the atom was really added to the current state of the world
      //-- (i.e., it wasn't there before), add it to the list of added atoms
      //-- so that in case of a backtrack it can be retracted.
      delAddList[1].add(p);
  }

  /** To delete this atomic delete/add element from the current state of the
   *  world.
  */
  public boolean del(State s, Term[] binding, Vector[] delAddList)
  {
    //-- Apply the binding (and execute the possible code calls) first.
    Predicate p = atom.applySubstitution(binding);

    //-- If the resulting atom is protected in the current state of the world,
    //-- it can not be deleted, and therefore the operator associated with this
    //-- atomic delete/add element can not be applied. Therefore, return false.
    if (s.isProtected(p))
      return false;

    //-- To store the index of the deleted atom.
    int index;

    //-- Try to delete the atom from the current state of the world.
    if ((index = s.del(p)) != -1)
      //-- If the atom was really deleted from the current state of the world
      //-- (i.e., it was there before), add it to the list of deleted atoms
      //-- so that in case of a backtrack it can be added back. Also keep track
      //-- of where the atom was, so that it can be added back exactly where it
      //-- was. This is important because new bindings are calculated as they
      //-- are needed (as opposed to calculating all of them in advance and
      //-- returning them one-by-one), and therefore if a backtrack happens,
      //-- the data structures should look exactly as they were before the
      //-- backtracked decision (to apply the operator this atomic delete/add
      //-- element is associated with) was made.
      delAddList[0].add(new NumberedPredicate(p, index));

    return true;
  }

  /** To set the number of variables in this atomic delete/add element.
  */
  public void setVarCount(int varCount)
  {
    atom.setVarCount(varCount);
  }

  /** This function produces Java code to create this atomic delete/add
   *  element.
  */
  public String toCode()
  {
    return "new DelAddAtomic(" + atom.toCode() + ")";
  }
}
