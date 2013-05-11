package JSHOP2;

import java.util.LinkedList;
import java.util.Vector;
import java.util.ArrayList;

/** Each task list, both at compile time and at run time, is an instance of
 *  this class.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class TaskList extends CompileTimeObject
{
  /** This variable represents an empty task list. All over the code, whenever
   *  any object needs an empty task list, it will use this variable rather
   *  than create a new empty task list to save memory because functionally
   *  all the empty task lists are the same.
  */
  public static TaskList empty = new TaskList();

  /** Whether or not this is an ordered task list.
  */
  private boolean ordered;

  /** In case this task list is not-atomic, this array represents it.
   *  Otherwise, this variable will be <code>null</code>.
  */
  public TaskList[] subtasks;

  /** In case this task list is atomic, this variable represents it. Otherwise,
   *  this variable will be <code>null</code>.
  */
  private TaskAtom task;

  /** To initialize this task list as an empty one (i.e., an ordered non-atomic
   *  task list of length 0). Note that this is a private function in order to
   *  minimize its use. In case an empty task list is needed,
   *  <code>TaskList.empty</code> should be used to save memory.
  */
  private TaskList()
  {
    ordered = true;

    subtasks = new TaskList[0];

    task = null;
  }

  /** To initialize this task list as an atomic one.
   *
   *  @param taskIn
   *          the task atom this task list will represent.
  */
  public TaskList(TaskAtom taskIn)
  {
    //-- A task atom is ordered by definition.
    ordered = true;

    subtasks = null;

    task = taskIn;
  }

  /** To initialize this task list as a non-atomic one.
   *
   *  @param size
   *          the number of task lists in the task list this object represents.
   *  @param orderedIn
   *          whether or not this task list is ordered.
  */
  public TaskList(int size, boolean orderedIn)
  {
    ordered = orderedIn;

    subtasks = new TaskList[size];

    task = null;
  }

  /** To bind a task list to a given binding.
   *
   *  @param binding
   *          the given binding
   *  @return
   *          the resulting task list.
  */
  public TaskList bind(Term[] binding)
  {
    //-- If this is an atomic task list,
    if (subtasks == null)
      //-- Just bind the task atom.
      return new TaskList(task.bind(binding));

    //-- If this is not an atomic task list, bind each of the tasks in the task
    //-- list.
    TaskList retVal = new TaskList(subtasks.length, ordered);

    for (int i = 0; i < subtasks.length; i++)
      retVal.subtasks[i] = subtasks[i].bind(binding);

    return retVal;
  }

  /** To create a non-atomic task list. This is defined as a static function
   *  rather than a constructor because it returns <code>TaskList.empty</code>
   *  if the required task list is of length 0 rather than creating a new empty
   *  task list. Other than that, this function acts like a constructor.
   *
   *  @param subtasksIn
   *          a <code>Vector</code> of subtasks the output task list will
   *          represent.
   *  @param orderedIn
   *          whether or not the output task list should be ordered.
   *  @return
   *          the resulting task list.
  */
  public static TaskList createTaskList(Vector<TaskList> subtasksIn, boolean orderedIn)
  {
    //-- If the size of the task list is zero, just return the empty task list
    //-- to save memory since we need only one object to represent all the
    //-- functionally-equivalent empty task lists.
    if (subtasksIn.size() == 0)
      return empty;

    TaskList retVal = new TaskList(subtasksIn.size(), orderedIn);

    for (int i = 0; i < subtasksIn.size(); i++)
      retVal.subtasks[i] = subtasksIn.get(i);

    return retVal;
  }

  /** To return a <code>LinkedList</code> of the task atoms we have the option
   *  to achieve right now. This list might have more than one member because
   *  of the existence of unordered task lists.
   *
   *  @return
   *          a <code>LinkedList</code> of the task atoms we have the option to
   *          achieve right now.
  */
  public LinkedList<TaskList> getFirst()
  {
    //-- Make an empty list.
    LinkedList<TaskList> retVal = new LinkedList<TaskList>();

    //-- Call the helper function.
    getFirstHelper(retVal);

    //-- Return the result.
    return retVal;
  }

  /** The helper function to calculate a <code>LinkedList</code> of the task
   *  atoms we have the option to achieve right now.
   *
   *  @param res
   *          this <code>LinkedList</code> stores the task atoms we have
   *          incrementally calculated so far.
   *  @return
   *          <code>true</code> if there can be no more task atoms we have the
   *          option to achieve next because we have encountered an immediate
   *          task atom, <code>false</code> otherwise. This return value is a
   *          sign for this function to stop calling itself recursively for
   *          more options in case an immediate task atom has been encountered.
  */
  private boolean getFirstHelper(LinkedList<TaskList> res)
  {
    //-- If this is an atomic task:
    if (subtasks == null)
    {
      //-- If this is an immediate task atom, it has to be decomposed next.
      //-- Therefore, just empty the list, add this task atom to it, and return
      //-- true.
      if (task.isImmediate())
      {
        res.clear();
        res.add(this);
        return true;
      }

      //-- Add the task atom to the list.
      res.add(this);

      //-- Since this is not an immediate task, return false, meaning the
      //-- function should still continue looking for other possible task
      //-- atoms.
      return false;
    }
    //-- If this is a non-atomic but ordered task list:
    else if (ordered)
    {
      //-- Just store the length of the list to check later if it has
      //-- changed.
      int listSize = res.size();

      //-- For each task in the task list, Call the function recursively.
      for (int i = 0; i < subtasks.length; i++)
      {
        if (subtasks[i].getFirstHelper(res))
          //-- If an immediate task atom was found, just return true.
          return true;
        else if (res.size() != listSize)
          //-- Otherwise, if the list has changed (since this is an ordered
          //-- task, it can change the list only once, therefore there is no
          //-- need to look any further in this task list), just return false.
          return false;
      }

      return false;
    }
    //-- If this is a non-atomic unordered task list:
    else
    {
      //-- For each task in the task list, Call the function recursively.
      for (int i = 0; i < subtasks.length; i++)
        if (subtasks[i].getFirstHelper(res))
          //-- If an immediate task atom was found, just return true
          return true;

      return false;
    }
  }

  /** This function produces Java code used to create this task list either
   *  as an atomic task list or recursively, as a list of other task lists.
   *
   *  @param what
   *          the <code>String</code> name of the task list created by this
   *          piece of code.
   *  @return
   *          the Java code as a <code>String</code>.
  */
  public String getInitCode(String what)
  {
    //-- Empty task list.
    if (isEmpty())
      return "\t\t" + what + " = TaskList.empty;" + endl;

    //-- Atomic task list.
    if (subtasks == null)
      return "\t\t" + what + " = new TaskList(" + task.toCode() + ");" + endl;

    //-- Non-atomic task list.
    String s;

    s = "\t\t" + what + " = new TaskList(" + subtasks.length + ", " + ordered
        + ");" + endl;

    //-- Recursively create subtasks.
    for (int i = 0; i < subtasks.length; i++)
      s += subtasks[i].getInitCode(what + ".subtasks[" + i + "]");

    return s;
  }

  /** To return the task atom associated with this task list.
   *
   *  @return
   *          the task atom associated with this task list in case this is an
   *          atomic task list, <code>null</code> otherwise.
  */
  public TaskAtom getTask()
  {
    return task;
  }

  /** Whether or not this task list is, or has become as result of task
   *  decomposition, an empty one.
   *
   *  @return
   *          <code>true</code> if this task list is equivalent to an empty
   *          task list, <code>false</code> otherwise.
  */
  public boolean isEmpty()
  {
    //-- Atomic task lists can not be empty.
    if (subtasks == null)
      return false;

    //-- If any of the subtasks is not empty, then the task list is not empty.
    for (int i = 0; i < subtasks.length; i++)
      if (!subtasks[i].isEmpty())
        return false;

    //-- If this is a non-atomic task list and all its subtasks are empty, then
    //-- it is an empty task list.
    return true;
  }

  /** This function is used to print this task list.
  */
  public void print()
  {
    System.out.println(this);
  }

  /** This function replaces (i.e., decomposes) a given task atom with a task
   *  list.
   *
   *  @param tasksIn
   *          the decomposition to replace the task atom.
  */
  public void replace(TaskList tasksIn)
  {
    subtasks = tasksIn.subtasks;
  }

  /** This function sets the number of variables for this task list. This
   *  number is used to return bindings of the appropriate size.
   *
   *  @param varCountIn
   *          the number of variables for this task list.
  */
  public void setVarCount(int varCountIn)
  {
    //-- If this is an atomic task list,
    if (subtasks == null)
      task.getHead().setVarCount(varCountIn);
    //-- If this is a non-atomic task list,
    else
      //-- Do it for each of the subtasks.
      for (int i = 0; i < subtasks.length; i++)
        subtasks[i].setVarCount(varCountIn);
  }

  /** This function produces Java code to create this task list.
  */
  public String toCode()
  {
    return "\t\tTaskList retVal;" + endl + endl + getInitCode("retVal") +
           endl + "\t\treturn retVal;" + endl;
  }

  /** This function returns a printable <code>String</code> representation of
   *  this task list.
   *
   *  @return
   *          the <code>String</code> representation.
  */
  public String toString()
  {
    //-- If this is an empty task list,
    if (isEmpty())
      return "()";
    //-- If this is an atomic task list,
    else if (subtasks == null)
      return task.toString();
    //-- If this is a non-atomic task list,
    else
    {
      String s = "(";

      //-- Add the ':unordered' keyword if it is needed.
      if (!ordered)
        s += ":unordered";

      //-- For each subtask,
      for (int i = 0; i < subtasks.length; i++)
        //-- If it is not empty,
        if (!subtasks[i].isEmpty())
        {
          if (!s.equals("("))
            s += " ";
          //-- Add the subtask to the result.
          s += subtasks[i];
        }

      //-- Close the paranthesis and return the result.
      return s + ")";
    }
  }

  /** This function undoes the decomposition of a task atom to a task list
   *  in case of a backtrack over the decision to decompose the task.
  */
  public void undo()
  {
    //-- Get rid of the subtasks which are the result of the decomposition.
    subtasks = null;
  }

  /**
   * This clone function was added so that TaskLists can be copied and passed to
   * JSHOP2GUI.
   * &lt;Added 5/12/06&gt;
   */
  public TaskList clone() {
    TaskList retval = new TaskList();

    retval.ordered = ordered;
    retval.task = task;
    if (subtasks != null) {
      retval.subtasks = new TaskList[subtasks.length];
      for (int i = 0; i < subtasks.length; i++)
        retval.subtasks[i] = subtasks[i].clone();
    } else
      retval.subtasks = null;

    return retval;
  }

  /**
   * Returns whether or not this is an ordered task list
   * &lt;Added 5/14/06&gt;
   */
  public boolean isOrdered() {
    return ordered;
  }

  /**
   * Returns the task list's subtasks
   * @return A TaskList[] that represents the subtasks of this task list
   * &lt;Added 5/14/06&gt;
   */
  public TaskList[] getSubtasks() {
    return subtasks;
  }

  /**
   * Returns an ArrayList of strings that represent the children of this
   * compound task.  Used in conjunction with with JSHOP2GUI.
   * &lt;Added 5/26/06&gt;
   */
  public void getChildren(ArrayList<String> childrenList) {
    // Only retrieve children if this is not an empty task list
    if ( !isEmpty() ) {
      //-- If this is an atomic task list,
      if (subtasks == null)
        childrenList.add(task.toString());
      //-- If this is a non-atomic task list,
      else {
        //-- For each subtask,
        for (int i = 0; i < subtasks.length; i++) {
          if (!subtasks[i].isEmpty()) {
            //-- Add the subtask to the result.
            subtasks[i].getChildren(childrenList);
          }
        }
      }
    }
  }
}
