package JSHOP2;

import java.util.Vector;

/** Each <code>ForAll</code> element in the delete/add list of an operator both
 *  at compile time and run time is represented as an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class DelAddForAll extends DelAddElement
{
  /** The atoms to be deleted/added.
  */
  private Predicate[] atoms;

  /** The number of objects already instantiated from this class.
  */
  private static int classCnt = 0;

  /** The number of objects already instantiated from this class before this
   *  object was instantiated. This is used as a unique identifier for this
   *  object to distinguish it from the other objects of this class.
  */
  private int cnt;

  /** The precondtion of the <code>ForAll</code> statement at compile time is
   *  represented by this variable.
  */
  private LogicalExpression exp;

  /** The precondtion of the <code>ForAll</code> statement at run time is
   *  represented by this variable.
  */
  private Precondition pre;

  /** To initialize this <code>ForAll</code> delete/add element at compile
   *  time.
   *
   *  @param expIn
   *          the logical expression to be the precondition of this
   *          <code>ForAll</code> delete/add element.
   *  @param atomsIn
   *          the atoms to be added/deleted as a <code>Vector</code>. Note that
   *          we use a <code>Vector</code> rather than an array at compile time
   *          because at compile time we do not know how many atoms there will
   *          be.
  */
  public DelAddForAll(LogicalExpression expIn, Vector<Predicate> atomsIn)
  {
    exp = expIn;

    //-- Storing the atoms in an array, now that we know how many atoms there
    //-- are.
    atoms = new Predicate[atomsIn.size()];
    atoms = atomsIn.toArray(atoms);

    cnt = classCnt++;
  }

  /** To initialize this <code>ForAll</code> delete/add element at run time.
   *
   *  @param preIn
   *          the logical expression to be the precondition of this
   *          <code>ForAll</code> delete/add element.
   *  @param atomsIn
   *          the atoms to be added/deleted as an array. Note that we use an
   *          array rather than a <code>Vector</code> at run time because at
   *          run time we know how many atoms there will be.
  */
  public DelAddForAll(Precondition preIn, Predicate[] atomsIn)
  {
    pre = preIn;
    atoms = atomsIn;
  }

  /** To add the atoms of this <code>ForAll</code> delete/add element to the
   *  current state of the world.
  */
  public void add(State s, Term[] binding, Vector[] delAddList)
  {
    //-- To store the next binding that satisfies the precondition of this
    //-- ForAll delete/add element.
    Term[] nextB;

    //-- Reset the precondition and apply the binding (and execute the possible
    //-- code calls) first.
    pre.reset();
    pre.bind(binding);

    //-- For each possible binding that satisfies the precondition of this
    //-- ForAll delete/add element,
    while ((nextB = pre.nextBinding()) != null)
    {
      Term.merge(nextB, binding);
      //-- For each atom to be added,
      for (int i = 0; i < atoms.length; i++)
      {
        //-- Apply the substitution (and execute the possible code calls) first.
        Predicate p = atoms[i].applySubstitution(nextB);

        //-- Try to add the resulting (presumably ground) atom to the current
        //-- state of the world.
        if (s.add(p))
          //-- If the atom was really added to the current state of the world
          //-- (i.e., it wasn't there before), add it to the list of the added
          //-- atoms so that in case of a backtrack it can be retracted.
          delAddList[1].add(p);
      }
    }
  }

  /** To delete the atoms of this <code>ForAll</code> delete/add element from
   *  the current state of the world.
  */
  public boolean del(State s, Term[] binding, Vector[] delAddList)
  {
    //-- To store the next binding that satisfies the precondition of this
    //-- ForAll delete/add element.
    Term[] nextB;

    //-- Reset the precondition and apply the binding (and execute the possible
    //-- code calls) first.
    pre.reset();
    pre.bind(binding);

    //-- For each possible binding that satisfies the precondition of this
    //-- ForAll delete/add element,
    while ((nextB = pre.nextBinding()) != null)
    {
      Term.merge(nextB, binding);
      //-- For each atom to be deleted,
      for (Predicate atom : atoms)
      {
        //-- Apply the substitution (and execute the possible code calls)
        //-- first.
        Predicate p = atom.applySubstitution(nextB);

        //-- If the resulting atom is protected in the current state of the
        //-- world, it can not be deleted, and therefore the operator
        //-- associated with this ForAll delete/add element can not be
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
          //-- backtrack happens, the data structures should look exactly as
          //-- they were before the backtracked decision (to apply the operator
          //-- this ForAll delete/add element is associated with) was made.
          delAddList[0].add(new NumberedPredicate(p, index));
      }
    }

    return true;
  }

  /** This function produces Java code that initializes some data structures
   *  that will be needed to create the precondition object that implements
   *  the precondition of this <code>ForAll</code> delete/add element at run
   *  time.
   *
   *  @return
   *          the produced code as a <code>String</code>.
  */
  public String getExpCode()
  {
    return exp.getInitCode();
  }

  /** This function produces Java code used to initialize an array of type
   *  predicate this <code>ForAll</code> delete/add element will use at run
   *  time to represent the atoms that will be deleted/added by this element.
   *  It also produces the code to set the current unifier for the precondition
   *  of this element to an empty one (i.e., an array of <code>null</code>
   *  elements, because when this precondition is created there is still no
   *  binding to be applied to it.
   *
   *  @return
   *          the Java code as a <code>String</code>.
  */
  public String getInitCode()
  {
    String retVal;

    //-- Allocate an array of the right size for the empty binding.
    retVal = "\t\tunifier = new Term[" + exp.getVarCount() + "];" + endl;

    //-- Set the elements of the binding to null (meaning that none of the
    //-- variables are bound yet.
    for (int i = 0; i < exp.getVarCount(); i++)
      retVal += endl + "\t\tunifier[" + i + "] = null;";

    //-- Define the array of predicates. Note the use of variable 'cnt' to
    //-- make the name of this array unique.
    retVal += endl + endl + "\t\tPredicate[] atoms" + cnt + " = {" + endl;

    //-- For each atom in this ForAll delete/add element,
    for (int i = 0; i < atoms.length; i++)
    {
      //-- Add the Java code that produces that atom as a member of the array.
      retVal += "\t\t\t" + atoms[i].toCode();

      //-- If this is not the last element in the array, add a comma to the
      //-- code.
      if (i != atoms.length - 1)
        retVal += "," + endl;
    }

    //-- Close the array definition, return the resulting String.
    return retVal + " };" + endl;
  }

  /** To set the number of variables in this <code>ForAll</code> delete/add
   *  element.
  */
  public void setVarCount(int varCount)
  {
    exp.setVarCount(varCount);

    for (int i = 0; i < atoms.length; i++)
      atoms[i].setVarCount(varCount);
  }

  /** This function produces Java code to create this <code>ForAll</code>
   *  delete/add element.
  */
  public String toCode()
  {
    return "new DelAddForAll(" + exp.toCode() + ", atoms" + cnt + ")";
  }
}
