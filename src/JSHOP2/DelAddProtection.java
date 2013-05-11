package JSHOP2;

import java.util.Vector;

/** Each <code>Protection</code> element in the delete/add list of an operator
 *  both at compile time and run time is represented as an instance of this
 *  class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class DelAddProtection extends DelAddElement
{
  /** The atom to be protected/unprotected.
  */
  private Predicate atom;

  /** To initialize this <code>Protection</code> delete/add element.
   *
   *  @param atomIn
   *          the atom to be protected/unprotected.
  */
  public DelAddProtection(Predicate atomIn)
  {
    atom = atomIn;
  }

  /** To add this atom to the list of protected atoms.
  */
  public void add(State s, Term[] binding, Vector[] delAddList)
  {
    //-- Apply the binding (and execute the possible code calls) first.
    Predicate p = atom.applySubstitution(binding);

    //-- Protect the resulting atom.
    s.addProtection(p);

    //-- Add the resulting atom to the list of added protections so that in
    //-- case of a backtrack the protection can be retracted.
    delAddList[3].add(p);
  }

  /** To delete this atom from the list of protected atoms.
  */
  public boolean del(State s, Term[] binding, Vector[] delAddList)
  {
    //-- Apply the binding (and execute the possible code calls) first.
    Predicate p = atom.applySubstitution(binding);

    //-- Try to unprotect the resulting atom.
    if (s.delProtection(p))
      //-- If the atom was really unprotected (i.e., it was protected before),
      //-- add it to the list of unprotected atoms so that in case of a
      //-- backtrack it can be reprotected.
      delAddList[2].add(p);

    //-- Trying to unprotect an atom (whether or not it is protected) is never
    //-- going to cause an operator to fail, so always return true.
    return true;
  }

  /** To set the number of variables in this <code>Protection</code> delete/add
   *  element.
  */
  public void setVarCount(int varCount)
  {
    atom.setVarCount(varCount);
  }

  /** This function produces Java code to create this <code>Protection</code>
   *  delete/add element.
  */
  public String toCode()
  {
    return "new DelAddProtection(" + atom.toCode() + ")";
  }
}
