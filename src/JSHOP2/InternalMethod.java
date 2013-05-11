package JSHOP2;

import java.util.Vector;

/** Each method at compile time is represented as an instance of this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class InternalMethod extends InternalElement
{
  /** The number of objects already instantiated from this class.
  */
  private static int classCnt = 0;

  /** A <code>Vector</code> of <code>String</code>s each of which represents
   *  the label of a branch of this method.
  */
  private Vector<String> labels;

  /** A <code>Vector</code> of logical preconditions each of which represents
   *  the precondition of a branch of this method. Each branch is an
   *  alternative on how to decompose the task associated with this method.
  */
  private Vector<LogicalPrecondition> pres;

  /** A <code>Vector</code> of task lists each of which represents a possible
   *  way to decompose the task associated with this method if the
   *  corresponding precondition is satisfied in the current state of the
   *  world.
  */
  private Vector<TaskList> subs;

  /** To initialize an <code>InternalMethod</code> object.
   *
   *  @param head
   *          head of the method (i.e., the compound task which can be
   *          decomposed by using this method).
   *  @param labelsIn
   *          a <code>Vector</code> of <code>String</code> labels.
   *  @param presIn
   *          a <code>Vector</code> of logical preconditions.
   *  @param subsIn
   *          a <code>Vector</code> of task lists.
  */
  public InternalMethod(Predicate head, Vector<String> labelsIn, Vector<LogicalPrecondition> presIn,
                        Vector<TaskList> subsIn)
  {
    //-- Set the head of this InternalMethod. Note the use of 'classCnt' to
    //-- make this object distinguishable from other objects instantiated from
    //-- this same class.
    super(head, classCnt++);

    //-- Set the labels, preconditions and task decompositions of
    //-- branches in this method.
    labels = labelsIn;
    pres = presIn;
    subs = subsIn;

    //-- To iterate over branch preconditions.
    //-- For each branch, set the number of variables in the precondition for
    //-- that branch. This will be used to produce the code that will be used
    //-- to find bindings, since a binding is an array of this size.
    for (LogicalPrecondition pre : pres)
      pre.setVarCount(getHead().getVarCount());

    //-- To iterate over task decompositions.
    //-- For each task decomposition, set the number of variables in the task
    //-- list for that decomposition.
    for (TaskList tl : subs)
      tl.setVarCount(getHead().getVarCount());
  }

  /** This function produces the Java code needed to implement this method.
  */
  public String toCode()
  {
    String s = "";

    //-- First produce the initial code for the preconditions of each branch.
    for (int i = 0; i < pres.size(); i++)
      s += pres.get(i).getInitCode();

    //-- The header of the class for this method at run time. Note the use of
    //-- 'getCnt()' to make the name of this class unique.
    s += "class Method" + getCnt() + " extends Method" + endl + "{" + endl;

    //-- The constructor of the class.
    s += "\tpublic Method" + getCnt() + "()" + endl + "\t{" + endl;

    //-- Call the constructor of the base class (class 'Method') with the code
    //-- that produces the head of this method.
    s += "\t\tsuper(" + getHead().toCode() + ");" + endl;

    //-- Allocate the array to keep the possible task lists that represent
    //-- possible decompositions of this method.
    s += "\t\tTaskList[] subsIn = new TaskList[" + subs.size() + "];" + endl;
    s += endl;

    //-- For each possible decomposition,
    for (int i = 0; i < subs.size(); i++)
    {
      if ((subs.get(i)).isEmpty())
        //-- This decomposition is an empty task list.
        s += "\t\tsubsIn[" + i + "] = TaskList.empty;" + endl;
      else
        //-- This decomposition is not an empty task list, so call the function
        //-- that will produce the task list for this decomposition. This
        //-- function will be implemented later on. Note the use of variable
        //-- 'i' to make the header of the function being called unique.
        s += "\t\tsubsIn[" + i + "] = createTaskList" + i + "();" + endl;
    }

    //-- Call the function that sets the method's task list to the array that
    //-- was created and initialized.
    s += endl + "\t\tsetSubs(subsIn);" + endl + "\t}" + endl + endl;

    //-- For each possible decomposition,
    for (int i = 0; i < subs.size(); i++)
    {
      //-- If the decomposition is not an empty list, we need to implement the
      //-- function that returns this decomposition.
      if (!(subs.get(i)).isEmpty())
      {
        //-- The function header.
        s += "\tTaskList createTaskList" + i + "()" + endl + "\t{" + endl;

        //-- The code that will produce this task list.
        s += (subs.get(i)).toCode() + "\t}" + endl + endl;
      }
    }

    //-- The function that returns an iterator that can be used to find all the
    //-- bindings that satisfy a given precondition of this method and return
    //-- them one-by-one.
    s += "\tpublic Precondition getIterator(Term[] unifier, int which)" + endl;
    s += "\t{" + endl + "\t\tPrecondition p;" + endl + endl;

    //-- The switch statement to choose the appropriate precondition.
    s += "\t\tswitch (which)" + endl + "\t\t{";

    //-- For each possible decomposition,
    for (int i = 0; i < pres.size(); i++)
    {
      //-- Retrieve the logical precondition.
      LogicalPrecondition pre = pres.get(i);

      //-- Produce the code that will return the appropriate iterator.
      s += endl + "\t\t\tcase " + i + ":" + endl + "\t\t\t\tp = ";
      s += pre.toCode() + ";" + endl;

      //-- If the logical precondition is marker ':first', set the appropriate
      //-- flag.
      if (pre.getFirst())
        s += "\t\t\t\tp.setFirst(true);" + endl;

      s += "\t\t\tbreak;";
    }

    //-- Close the switch statement.
    s += endl + "\t\t\tdefault:" + endl + "\t\t\t\treturn null;" + endl;
    s += "\t\t}" + endl;

    //-- Reset the precondition and return it.
    s += endl + "\t\tp.reset();" + endl + endl + "\t\treturn p;" + endl;

    //-- This function returns the label of a given branch of this method.
    s += "\t}" + endl + endl + "\tpublic String getLabel(int which)" + endl;

    //-- The switch statement to choose the appropriate label.
    s += "\t{" + endl + "\t\tswitch (which)" + endl + "\t\t{";

    //-- For each branch;
    for (int i = 0; i < labels.size(); i++)
      //-- Return its associated label.
      s += endl + "\t\t\tcase " + i + ": return \"" + labels.get(i) + "\";";

    //-- Close the switch statement.
    s += endl + "\t\t\tdefault: return null;" + endl + "\t\t}" + endl;

    //-- Close the function definition and the class definition and return the
    //-- resulting string.
    return s + "\t}" + endl + "}" + endl + endl;
  }
}
