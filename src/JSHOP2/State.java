package JSHOP2;

import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;

/** This class is used to represent the current state of the world.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class State
{
  /** The atoms in the current state of the world as an array of
   *  <code>Vector</code>s. The array is indexed by the possible heads (i.e.,
   *  the constant symbol that comes first) of the possible predicates.
  */
  private Vector<Term>[] atoms;

  /** The axioms in the domain description as a two-dimensional array. The
   *  array is indexed first by the head of the predicates each axiom can prove
   *  and second by the axioms themselves.
  */
  private Axiom[][] axioms;

  /** The protections in the current state of the world as an array of
   *  <code>Vector</code>s. The array is indexed by the heads of protected
   *  predicates.
  */
  private Vector<NumberedPredicate>[] protections;

  /** To initialize the state of the world.
   *
   *  @param size
   *          the number of possible heads of predicates (i.e., the number of
   *          constant symbols that can come first in a predicate).
   *  @param axiomsIn
   *          the axioms in the domain description as a two-dimensional array.
   *          The array is indexed first by the head of the predicates each
   *          axiom can prove and second by the axioms themselves.
  */
  public State(int size, Axiom[][] axiomsIn)
  {
    //-- Initialize the arrays that represent the atoms and protections in the
    //-- current state of the world.
    atoms = new Vector[size];

    protections = new Vector[size];

    for (int i = 0; i < size; i++)
    {
      atoms[i] = new Vector<Term>();
      protections[i] = new Vector<NumberedPredicate>();
    }

    axioms = axiomsIn;
  }

  /** To add a predicate to the current state of the world.
   *
   *  @param p
   *          the predicate to be added.
   *  @return
   *          <code>true</code> if the predicate was added (i.e., it was not
   *          already in the current state of the world), <code>false</code>
   *          otherwise.
  */
  public boolean add(Predicate p)
  {
        //-- Find the right Vector to add this predicate to.
    
    //-- First look for the predicate in the Vector. If it is already there,
    //-- do nothing and return false.
    for (Term t : atoms[p.getHead()]) {
      if (p.equals(t))
        return false;
    }

    //-- Add the predicate and return true.
    atoms[p.getHead()].add(p.getParam());

    return true;
  }

  /** To protect a given predicate in the current state of the world.
   *
   *  @param p
   *          the predicate to be protected.
   *  @return
   *          this function always returns <code>true</code>.
  */
  public boolean addProtection(Predicate p)
  {
    // -- First, find the appropriate Vector to add the protection to.

    // -- If the predicate is already protected, just increase the protection
    // -- counter.
    for (NumberedPredicate np : protections[p.getHead()]) {
      if (p.equals(np.getParam())) {
        np.inc();
        return true;
      }
    }

    // -- If this is the first time this predicate is being protected, add it
    //-- to the Vector.
    protections[p.getHead()].add(new NumberedPredicate(p));
    return true;
  }

  /** To empty the world state.
  */
  public void clear()
  {
    for (int i = 0; i < atoms.length; i++)
    {
      atoms[i].clear();
      protections[i].clear();
    }
  }

  /** To delete a predicate from the current state of the world.
   *
   *  @param p
   *          the predicate to be deleted.
   *  @return
   *          the index of the predicate that was deleted in the
   *          <code>Vector</code> if the predicate was deleted (i.e., it
   *          existed in the current state of the world), -1 otherwise. This
   *          index is used in case of a backtrack to undo this deletion by
   *          inserting the deleted predicate right back where it used to be.
  */
  public int del(Predicate p)
  {
    Term t;

    //-- Find the right Vector to delete this predicate from.
    Vector<Term> vec = atoms[p.getHead()];

    //-- If predicate is found, delete it and return its index.
    for (int i = 0; i < vec.size(); i++)
    {
      t = (Term)vec.get(i);

      if (p.equals(t))
      {
        vec.remove(i);
        return i;
      }
    }

    //-- There was nothing to delete, so return -1.
    return -1;
  }

  /** To unprotect a given predicate.
   *
   *  @param p
   *          the predicate to be unprotected.
   *  @return
   *          <code>true</code> if the protected is unprotected successfully,
   *          <code>false</code> otherwise (i.e., when the predicate was not
   *          protected before).
  */
  public boolean delProtection(Predicate p)
  {
    NumberedPredicate np;

    //-- First, find the appropriate Vector to delete the protection from.
    Iterator<NumberedPredicate> e = protections[p.getHead()].iterator();

    //-- Look for the protection.
    while (e.hasNext())
    {
      np = e.next();

      //-- If it is found,
      if (p.equals(np.getParam()))
      {
        //-- Decrease the protection counter for this predicate.
        if (!np.dec())
          //-- If the counter drops to zero, remove the protection completely.
          e.remove();

        return true;
      }
    }

    //-- Nothing was there to delete, so return false.
    return false;
  }

  /** To check if a predicate is protected.
   *
   *  @param p
   *          the predicate to be checked.
   *  @return
   *          <code>true</code> if the predicate is protected,
   *          <code>false</code> otherwise.
  */
  public boolean isProtected(Predicate p)
  {
    //-- First, find the appropriate Vector to look for the protection.
    //-- Iterate over the Vector to find the protection.
    for (NumberedPredicate np : protections[p.getHead()])
    {
      if (p.equals(np.getParam()))
        return true;
    }

    return false;
  }

  /** To initialize and return the appropriate iterator when looking
   *  for ways to satisfy a given predicate.
   *
   *  @param head
   *          the index of the constant symbol that is the head of the
   *          predicate (i.e., that comes first in the predicate).
   *  @return
   *          the iterator to be used to find the satisfiers for this
   *          predicate.
  */
  public MyIterator iterator(int head)
  {
    return new MyIterator(atoms[head]);
  }

  /** This function returns the bindings that can satisfy a given precondition
   *  one-by-one.
   *
   *  @param p
   *          the predicate to be satisfied.
   *  @param me
   *          the iterator that keeps track of where we are with the satisfiers
   *          so that the next time this function is called, we can take off
   *          where we stopped last time.
   *  @return
   *          the next binding as an array of terms indexed by the indeices of
   *          the variable symbols in the given predicate.
  */
  public Term[] nextBinding(Predicate p, MyIterator me)
  {
    Term[] nextB;

    Term[] retVal;

    Term t;

    //-- If we are still looking into the atoms to prove the predicate (i.e.,
    //-- we have not started looking into the axioms),
    if (me.whichAxiom == -1)
    {
      //-- Iterate over the appropriate Vector to find atoms that can satisfy
      //-- the given predicate.
      while (me.index < me.vec.size())
      {
        t = (Term)me.vec.get(me.index++);
        retVal = p.findUnifier(t);

        //-- If this atom can satisfy the given predicate, return the binding
        //-- that unifies the two.
        if (retVal != null)
          return retVal;
      }

      //-- We have already looked at all the atoms that could possibly satisfy
      //-- the predicate. From now on, we will look at the axioms only.
      me.whichAxiom = 0;
    }

    while (true)
    {
      //-- If we need to look at a new axiom,
      while (me.ax == null)
      {
        //-- If there are no more axioms to be looked at, return null.
        if (me.whichAxiom == axioms[p.getHead()].length)
          return null;

        //-- Try the next axiom whose head matches the head of the given
        //-- predicate.
        me.ax = axioms[p.getHead()][me.whichAxiom++];

        //-- Try to unify the axiom's head with the predicate.
        me.binding = me.ax.unify(p);

        //-- If the two can not be unified,
        if (me.binding == null)
          //-- Try to look for the next axiom.
          me.ax = null;
        else
        {
          //-- Start with the first branch of this axiom.
          me.index = 0;
          //-- No branch has been satisfied yet, so set this variable to false.
          me.found = false;
        }
      }

      //-- Iterate on all the branches of this axiom.
      for (; me.index < me.ax.getBranchSize(); me.index++)
      {
        //-- If this is the first time this branch is considered, get the
        //-- iterator for the precondition of this branch.
        if (me.pre == null)
          me.pre = me.ax.getIterator(me.binding, me.index);

        //-- Try the next satisfier for the precondition of this branch of this
        //-- axiom. If there is a next satisfier,
        while ((nextB = me.pre.nextBinding()) != null)
        {
          //-- Merge the two bindings.
          Term.merge(nextB, me.binding);

          //-- Calculate the instance of the axiom we are using.
          Predicate groundAxiomHead = me.ax.getHead().applySubstitution(nextB);

          //-- Try to unify the axiom and the predicate.
          retVal = p.findUnifier(groundAxiomHead.getParam());

          //-- If there is such unifier, return it.
          if (retVal != null)
          {
            //-- The further branches of this axiom must NOT be considered even
            //-- if this branch fails because there has been at least one
            //-- satisfier for this branch of the axiom. Set this variable to
            //-- true to prevent the further branches of this axiom from being
            //-- considered.
            me.found = true;

            return retVal;
          }
        }

        //-- Try the next branch of this axiom.
        me.pre = null;

        //-- According to the semantics of the axiom branches in JSHOP2, second
        //-- branch is considered only when there is no binding for the first
        //-- branch, the third branch is considered only when there is no
        //-- binding for the first and second branches, etc. Therefore, if one
        //-- of the branches of this axiom has already returned a satisfier,
        //-- the other branches should be ignored.
        if (me.found)
          break;
      }

      //-- Try the next axiom.
      me.ax = null;
    }
  }

  /** This function is used to print the current state of the world.
  */
  public void print()
  {
    for (int i = 0; i < atoms.length; i++)
    {
      for (Term t : atoms[i]) 
      {
        (new Predicate(i, 0, t)).print();
      }

      System.out.println();
    }

    System.out.println("------");
  }


  /**
   * Returns an ArrayList of strings that represents the state.  Used
   * in conjunction with JSHOP2GUI
   * (Added 5/28/06)
   * @return
   *    - An ArrayList<String> representing the state
   */
  public ArrayList<String> getState() {
    ArrayList<String> retval = new ArrayList<String>();
    for (int i = 0; i < atoms.length; i++) 
    {
      for (Term t : atoms[i]) 
      {
        retval.add((new Predicate(i, 0, t)).toString());
      }
    }
    return retval;
  }


  /**
   * This function is used, in case of a backtrack, to undo the changes that
   * were made to the current state of the world because of the backtracked
   * decision.
   * 
   * @param delAdd
   *          a 4-member array of type <code>Vector</code>. These four
   *          members are the deleted atoms, the added atoms, the deleted
   *          protections and the added protections respectively.
   */
  public void undo(Vector[] delAdd)
  {
    Iterator e;

    NumberedPredicate np;

    //-- Since when an operator is applied, first the predicates in its delete
    //-- list are deleted and then the predicates in its add list are added,
    //-- when that application is undone, first the added predicates should be
    //-- deleted and then the deleted predicates should be added.

    //-- Deleting the added predicates.
    e = delAdd[1].iterator();
    while (e.hasNext())
      del((Predicate)e.next());

    //-- Adding the deleted predicates, exactly where they were deleted from.
    for (int i = delAdd[0].size() - 1; i >= 0; i--)
    {
      np = (NumberedPredicate)delAdd[0].get(i);
      atoms[np.getHead()].add(np.getNumber(), np.getParam());
    }

    //-- Deleting the added protections.
    e = delAdd[3].iterator();
    while (e.hasNext())
      delProtection((Predicate)e.next());

    //-- Adding the deleted protections.
    e = delAdd[2].iterator();
    while (e.hasNext())
      addProtection((Predicate)e.next());
  }
}
