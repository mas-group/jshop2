package JSHOP2;

import java.util.Vector;

/** Each operator at compile time is represented as an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class InternalOperator extends InternalElement
{
  /** This operator's add list, a <code>Vector</code> of objects of type
   *  <code>DelAddElement</code>. Note that a <code>Vector</code>, rather than
   *  an array, is used, since at compile time we do not know how many
   *  delete/add elements there will be.
  */
  private Vector add;

  /** The number of objects already instantiated from this class.
  */
  private static int classCnt = 0;

  /** The cost of this operator.
  */
  private Term cost;

  /** This operator's delete list, a <code>Vector</code> of objects of type
   *  <code>DelAddElement</code>. Note that a <code>Vector</code>, rather than
   *  an array, is used, since at compile time we do not know how many
   *  delete/add elements there will be.
  */
  private Vector del;

  /** The logical precondition of this operator.
  */
  private LogicalPrecondition pre;

  /** To initialize an <code>InternalOperator</code> object.
   *
   *  @param head
   *          head of the operator (i.e., the primitive task that can be
   *          achieved by applying this operator).
   *  @param preIn
   *          the logical precondition for this operator.
   *  @param delIn
   *          the delete list of the operator. The first element of the
   *          <code>Vector</code> is of type <code>Integer</code>. If it is not
   *          <code>null</code>, it means that the delete list of this operator
   *          is a variable, and the value of the <code>Integer</code> shows
   *          that variable's index. Otherwise, the rest of the
   *          <code>Vector</code> is of type <code>DelAddElement</code>,
   *          representing the atoms and protections that will be deleted from
   *          the state of the world when this operator is applied.
   *  @param addIn
   *          the add list of the operator. The first element of the
   *          <code>Vector</code> is of type <code>Integer</code>. If it is not
   *          <code>null</code>, it means that the add list of this operator
   *          is a variable, and the value of the <code>Integer</code> shows
   *          that variable's index. Otherwise, the rest of the
   *          <code>Vector</code> is of type <code>DelAddElement</code>,
   *          representing the atoms and protections that will be added to the
   *          state of the world when this operator is applied.
   *  @param costIn
   *          the cost of the operator.
  */
  public InternalOperator(Predicate head, LogicalPrecondition preIn,
                          Vector delIn, Vector addIn, Term costIn)
  {
    //-- Set the head of this InternalOperator. Note the use of 'classCnt' to
    //-- make this object distinguishable from other objects instantiated from
    //-- the same class.
    super(head, classCnt++);

    //-- Set the precondition of this operator.
    pre = preIn;

    //-- Set the delete list of this operator.
    del = delIn;

    //-- Set the add list of this operator.
    add = addIn;

    //-- Set the cost of the operator.
    cost = costIn;

    //-- Set the number of variables in the precondition of the operator.
    //-- This will be used to produce the code that will be used to find
    //-- bindings, since a binding is an array of this size.
    pre.setVarCount(getHead().getVarCount());

    //-- If the delete list of the operator is not a variable,
    if ((Integer)del.get(0) == null)
      //-- For each delete/add element there,
      for (int i = 1; i < del.size(); i++)
        //-- Set the number of variables.
        ((DelAddElement)del.get(i)).setVarCount(getHead().getVarCount());

    //-- If the add list of the operator is not a variable,
    if ((Integer)add.get(0) == null)
      //-- For each delete/add element there,
      for (int i = 1; i < add.size(); i++)
        //-- Set the number of variables.
        ((DelAddElement)add.get(i)).setVarCount(getHead().getVarCount());
  }

  /** This function produces the Java code needed to implement this operator.
  */
  public String toCode()
  {
    String s;

    //-- The index of the variable that represents the delete/add list of the
    //-- operator, -1 otherwise (i.e., when the delete/add list of the operator
    //-- is a real list). To be used when the constructor of the operator is
    //-- called at run time.
    int delIdx, addIdx;

    //-- Is there a ForAll Element in this operator's delete/add list.
    boolean hasForAll = false;

    //-- First produce the initial code for the precondition of the operator.
    s = pre.getInitCode();

    Integer varIdx = (Integer)del.get(0);
    //-- If the first element of the delete list is null, it means the delete
    //-- list is a real list, so process the list.
    if (varIdx == null)
    {
      delIdx = -1;

      //-- For each element in the delete list, except the first one of course,
      for (int i = 1; i < del.size(); i++)
      {
        //-- If the delete/add element is a ForAll element,
        if ((DelAddElement)del.get(i) instanceof DelAddForAll)
        {
          //-- Produce the code that will calculate the bindings that will
          //-- satisfy its preconditions.
          s += ((DelAddForAll)del.get(i)).getExpCode();

          hasForAll = true;
        }
      }
    }
    else
      //-- If the first element of the delete list is not null, it means the
      //-- delete list is a variable, and this element represents its index.
      delIdx = varIdx.intValue();

    varIdx = (Integer)add.get(0);
    //-- If the first element of the add list is null, it means the add list is
    //-- a real list, so process the list.
    if (varIdx == null)
    {
      addIdx = -1;

      //-- For each element in the add list, except the first one of course,
      for (int i = 1; i < add.size(); i++)
      {
        //-- If the delete/add element is a ForAll element,
        if ((DelAddElement)add.get(i) instanceof DelAddForAll)
        {
          //-- Produce the code that will calculate the bindings that will
          //-- satisfy its preconditions.
          s += ((DelAddForAll)add.get(i)).getExpCode();

          hasForAll = true;
        }
      }
    }
    else
      //-- If the first element of the add list is not null, it means the add
      //-- list is a variable, and this element represents its index.
      addIdx = varIdx.intValue();

    //-- The header of the class for this operator at run time. Note the use of
    //-- 'getCnt()' to make the name of this class unique.
    s += "class Operator" + getCnt() + " extends Operator" + endl + "{" + endl;

    //-- The constructor of the class.
    s += "\tpublic Operator" + getCnt() + "()" + endl + "\t{" + endl;

    //-- Call the constructor of the base class (class 'Operator') with the
    //-- code that produces the head of this method.
    s += "\t\tsuper(" + getHead().toCode() + ", " + delIdx + ", " + addIdx;
    s += ", " + cost.toCode() + ");" + endl + endl;

    //-- Define a variable that will be used in the constructors of the
    //-- 'LogicalExpression's if there are any ForAll elements.
    if (hasForAll)
      s += "\t\tTerm[] unifier;" + endl + endl;

    //-- If the delete list is a real list, produce the code that will
    //-- initialize the array that represents the delete list.
    if (delIdx == -1)
    {
      //-- Allocate the array.
      s += endl + "\t\tDelAddElement[] delIn = new DelAddElement[";
      s += (del.size() - 1) + "];" + endl;

      //-- For each element in the delete list, except for the first one of
      //-- course,
      for (int i = 1; i < del.size(); i++)
      {
        //-- If it is a ForAll element, produce the code that will initialize
        //-- the list of atoms to be deleted by this element.
        if ((DelAddElement)del.get(i) instanceof DelAddForAll)
          s += ((DelAddForAll)del.get(i)).getInitCode();

        //-- Set the corresponding element in the array to the code that
        //-- produces this delete/add element.
        s += "\t\tdelIn[" + (i - 1) + "] = ";
        s += ((DelAddElement)del.get(i)).toCode() + ";" + endl;
      }

      //-- Set the delete list of the operator to the array just created.
      s += endl + "\t\tsetDel(delIn);" + endl;
    }

    //-- If the add list is a real list, produce the code that will initialize
    //-- the array that represents the add list.
    if (addIdx == -1)
    {
      s += endl + "\t\tDelAddElement[] addIn = new DelAddElement[";
      s += (add.size()  - 1) + "];" + endl;

      //-- For each element in the add list, except for the first one of
      //-- course,
      for (int i = 1; i < add.size(); i++)
      {
        //-- If it is a ForAll element, produce the code that will initialize
        //-- the list of atoms to be added by this element.
        if ((DelAddElement)add.get(i) instanceof DelAddForAll)
          s += ((DelAddForAll)add.get(i)).getInitCode();

        //-- Set the corresponding element in the array to the code that
        //-- produces this delete/add element.
        s += "\t\taddIn[" + (i - 1) + "] = ";
        s += ((DelAddElement)add.get(i)).toCode() + ";" + endl;
      }

      //-- Set the add list of the operator to the array just created.
      s += endl + "\t\tsetAdd(addIn);" + endl;
    }

    //-- Close the constructor.
    s += "\t}" + endl + endl;

    //-- The function that returns an iterator that can be used to find all the
    //-- bindings that satisfy this operator's precondition and return them
    //-- one-by-one.
    s += "\tpublic Precondition getIterator(Term[] unifier, int which)";
    s += endl + "\t{" + endl + "\t\tPrecondition p;" + endl + endl;

    //-- Produce the code that will return the appropriate iterator.
    s += "\t\tp = " + pre.toCode() + ";" + endl;

    //-- If the logical precondition is marker ':first', set the appropriate
    //-- flag.
    if (pre.getFirst())
      s += "\t\tp.setFirst(true);" + endl;

    //-- Reset the precondition and return it, and close the function.
    s += "\t\tp.reset();" + endl + endl + "\t\treturn p;" + endl + "\t}";

    //-- Close the class definition and return the resulting string.
    return s + endl + "}" + endl + endl;
  }
}
