package JSHOP2;

import java.util.Vector;

/** This class implements an iterator with data members that can keep track of
 *  where the algorithm is in terms of bindings found so far so that when the
 *  next binding is needed it can be calculated correctly. This class is needed
 *  because an atom can be satisfied either by looking for bindings at the
 *  current state of the world, or by using an axiom.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class MyIterator
{
  /** The axiom being used right now. If none is used (i.e., we are still
   *  looking for the atom in the current state of the world) the value of
   *  this variable is <code>null</code>.
  */
  Axiom ax;

  /** When an axiom is being used, this variable holds the binding that unifies
   *  the head of the axiom and the atom being proved.
  */
  Term[] binding;

  /** Whether or not at least one satisfier has been found for the current
   *  branch of the current axiom. As soon as it becomes <code>true</code>,
   *  further branches of the axiom will not be considered.
  */
  boolean found;

  /** When looking at the current state of the world, this variable represents
   *  the index of the corresponding <code>Vector</code>, when using an axiom
   *  to prove an atom, this variable represents which branch of that axiom is
   *  being used.
  */
  int index;

  /** When an axiom is being used, this variable acts as an iterator over all
   *  the possible satisfiers of the precondition of the current branch of the
   *  current axiom.
  */
  Precondition pre;

  /** The <code>Vector</code> in the current state of the world that represents
   *  the atoms for which we are trying to find satisfiers.
  */
  Vector<Term> vec;

  /** Which of the (possibly several) axioms that can be used to prove a
   *  certain atom is being used right now. If none is being used (i.e., we are
   *  still looking for the atom in the current state of the world), it is set
   *  to -1.
  */
  int whichAxiom;

  /** To initialize this iterator.
   *
   *  @param vecIn
   *          The <code>Vector</code> in the current state of the world that
   *          represents the atoms for which we are trying to find satisfiers.
  */
  public MyIterator(Vector<Term> vecIn)
  {
    //-- Initially, no axiom is being considered.
    ax = null;

    //-- Reset the index over the Vector of atoms to be considered.
    index = 0;

    //-- Initially, no axiom precondition is being considered.
    pre = null;

    vec = vecIn;

    //-- Initially, no axiom is being considered.
    whichAxiom = -1;
  }
}
