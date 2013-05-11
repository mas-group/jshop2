package JSHOP2;

import java.util.Vector;

/** Each operator at run time is represented as a class derived from this
 *  abstract class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public abstract class Operator extends DomainElement
{
  /** Represents the add list in case it is a real list and not a variable.
  */
  private DelAddElement[] add;

  /** Represents the add list in case it is a variable (The integer value
   *  represents variable's index), otherwise it is -1.
  */
  private int addVarIdx;

  /** Cost of this operator.
  */
  private Term cost;

  /** Represents the delete list in case it is a real list and not a variable.
  */
  private DelAddElement[] del;

  /** Represents the delete list in case it is a variable (The integer value
   *  represents variable's index), otherwise it is -1.
  */
  private int delVarIdx;

  /** To initialize the operator.
   *
   *  @param head
   *          head of the operator.
   *  @param delVarIdxIn
   *          the index of the delete list as a variable, -1 if the delete list
   *          is not a variable.
   *  @param addVarIdxIn
   *          the index of the add list as a variable, -1 if the add list is
   *          not a variable.
   *  @param costIn
   *          the cost of the operator.
  */
  public Operator(Predicate head, int delVarIdxIn, int addVarIdxIn,
                  Term costIn)
  {
    super(head);

    delVarIdx = delVarIdxIn;
    addVarIdx = addVarIdxIn;
    cost = costIn;
  }

  /** This function is used to apply this operator to a given state.
   *
   *  @param binding
   *          the current binding.
   *  @param s
   *          current state of the world.
   *  @param delAdd
   *          a 4-member array of type <code>Vector</code> used to keep track
   *          of the atoms and protections deleted from and added to the
   *          current state of the world as the result of applying this
   *          operator. This data can be used later in case of a backtrack to
   *          undo these changes.
   *  @return
   *          <code>true</code> if the operator was applicable,
   *          <code>false</code> otherwise. An operator is not applicable when
   *          at least one of the atoms it tries to delete is protected.
  */
  public boolean apply(Term[] binding, State s, Vector[] delAdd)
  {
    //-- Initialze the 'delAdd' array.
    delAdd[0] = new Vector();
    delAdd[1] = new Vector();
    delAdd[2] = new Vector();
    delAdd[3] = new Vector();

    //-- If the delete list is a variable,
    if (delVarIdx != -1)
    {
      //-- Find what that variable is bound to.
      List l = ((TermList)binding[delVarIdx]).getList();

      //-- Iterate over the elements of the delete list.
      while (l != null)
      {
        //-- Each element of the list should be a Predicate. Make that
        //-- Predicate.
        Predicate p = ((TermList)l.getHead()).toPredicate(0);

        //-- If this atom is protected in the current state of the world, it
        //-- can not be deleted, and therefore this operator can not be
        //-- applied. Therefore, return false.
        if (s.isProtected(p))
          return false;

        //-- To store the index of the deleted atom.
        int index;

        //-- Try to delete the atom from the current state of the world.
        if ((index = s.del(p)) != -1)
          //-- If the atom was really deleted from the current state of the
          //-- world (i.e., it was there before), add it to the list of deleted
          //-- atoms so that in case of a backtrack it can be added back. Also
          //-- keep track of where the atom was, so that it can be added back
          //-- exactly where it was. This is important because new bindings are
          //-- calculated as they are needed (as opposed to calculating all of
          //-- them in advance and returning them one-by-one), and therefore if a
          //-- backtrack happens, the data strucutures should look exactly as
          //-- they were before the backtracked decision to apply this operator
          //-- was made.
          delAdd[0].add(new NumberedPredicate(p, index));

        l = l.getRest();
      }
    }
    //-- If the delete list is a real list,
    else
    {
      //-- For each delete/add element in the delete list,
      for (int i = 0; i < del.length; i++)
        //-- Try to delete the atom from the current state of the world.
        if (!del[i].del(s, binding, delAdd))
          //-- If the atom can not be deleted (i.e., it is protected), return
          //-- false because this operator can not be applied.
          return false;
    }

    //-- If the add list is a variable,
    if (addVarIdx != -1)
    {
      //-- Find what that variable is bound to.
      List l = ((TermList)binding[addVarIdx]).getList();

      //-- Iterate over the elements of the add list.
      while (l != null)
      {
        //-- Each element of the list should be a Predicate. Make that
        //-- Predicate.
        Predicate p = ((TermList)l.getHead()).toPredicate(0);

        //-- Try to add the resulting (presumably ground) atom to the current
        //-- state of the world.
        if (s.add(p))
          //-- If the atom was really added to the current state of the world
          //-- (i.e., it wasn't there before), add it to the list of the added
          //-- atoms so that in case of a backtrack it can be retracted.
          delAdd[1].add(p);

        l = l.getRest();
      }
    }
    //-- If the add list is a real list,
    else
    {
      //-- For each delete/add element in the add list,
      for (int i = 0; i < add.length; i++)
        //-- Add it to the current state of the world.
        add[i].add(s, binding, delAdd);
    }

    return true;
  }

  /** To get the cost of this operator.
   *
   *  @param binding
   *          the binding to be applied to the cost term.
   *  @return
   *          the cost of applying this operator.
  */
  public double getCost(Term[] binding)
  {
    return ((TermNumber)cost.bind(binding)).getNumber();
  }

  /** To set the add list.
   *
   *  @param addIn
   *          the add list.
  */
  public void setAdd(DelAddElement[] addIn)
  {
    add = addIn;
  }

  /** To set the delete list.
   *
   *  @param delIn
   *          the delete list.
  */
  public void setDel(DelAddElement[] delIn)
  {
    del = delIn;
  }
}
