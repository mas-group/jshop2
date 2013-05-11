package JSHOP2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/** This class represents all the variables that JSHOP2 needs every time it
 *  calls itself recursively. The reason all these variables are bundled
 *  together in one class rather than having them locally defined is to save
 *  stack space. Right now, the only thing that is stored in the stack is a
 *  pointer to this class in each recursion, and the actual data is stored in
 *  heap memory, while if these variables were just locally defined, all of
 *  them would be stored in the stack, resulting in very fast stack overflow
 *  errors.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
class InternalVars
{
  /** The binding that unifies the head of a method or an operator with the
   *  task being achieved.
  */
  Term[] binding;

  /** An array of size 4 to store the atoms and protections that are being
   *  deleted or added to the current state of the world as a result of
   *  application of an operator, to be used in case of a backtrack over that
   *  operator.
  */
  Vector[] delAdd;

  /** The iterator iterating over the <code>LinkedList</code> of the tasks
   *  that we have the option to achieve right now.
  */
  Iterator<TaskList> e;

  /** Whether or not at least one satisfier has been found for the current
   *  branch of the current method. As soon as it becomes <code>true</code>,
   *  further branches of the method will not be considered.
  */
  boolean found;

  /** The index of the method or operator being considered.
  */
  int j;

  /** The index of the branch of the current method being considered.
  */
  int k;

  /** An array of methods that can achieve the compound task being
   *  considered.
  */
  Method[] m;

  /** Next binding that satisfies the precondition of the current method or
   *  operator.
  */
  Term[] nextB;

  /** An array of operators that can achieve the primitive task being
   *  considered.
  */
  Operator[] o;

  /** An iterator over the bindings that can satisfy the precondition of the
   *  current method or operator.
  */
  Precondition p;

  /** The task atom chosen to be achieved next.
  */
  TaskAtom t;

  /** A <code>LinkedList</code> of the task atoms we have the option to
   *  achieve right now.
  */
  LinkedList<TaskList> t0;

  /** The atomic task list that represents, in the task network, the task
   *  atom that has been chosen to be achieved next.
  */
  TaskList tl;
}

/** This class is the implementation of the JSHOP2 algorithm.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class JSHOP2
{
  /** The plan currently being constructed.
  */
  private static Plan currentPlan;

  /** The domain description for the planning problem.
  */
  private static Domain domain;

  /** The maximum number of plans to be returned.
  */
  private static int planNo;

  /** The plans are stored in this variable as a list of type
   *  <code>Plan</code>.
  */
  private static LinkedList<Plan> plans;

  /** The current state of the world.
  */
  private static State state;

  /** The task list to be achieved.
  */
  private static TaskList tasks;

  /** An <code>ArrayList</code> that represents the steps taken to find every
   *  plan.
  */
  private static ArrayList<PlanStepInfo> planStepList;

  /** Incremented whenever a plan is found. Passed to JSHOP2GUI.
  */
  private static int numPlans;

  /** This function finds plan(s) for a given initial task list.
   *
   *  @param tasksIn
   *          the initial task list to be achieved.
   *  @param planNoIn
   *          the maximum number of plans to be returned.
   *  @return
   *          0 or more plans that achieve the given task list.
  */
  public static LinkedList<Plan> findPlans(TaskList tasksIn, int planNoIn)
  {
    //-- Initialize the plan list to an empty one.
    plans = new LinkedList<Plan>();

    //-- Initialize the current plan to an empty one.
    currentPlan = new Plan();

    //-- Initialize the current task list to be achieved.
    tasks = tasksIn;

    //-- Initialize the list of steps taken to find all plans
    planStepList = new ArrayList<PlanStepInfo>();

    //-- Initialize the number of plans found to 0
    numPlans = 0;

    planNo = planNoIn;

    PlanStepInfo newStep = new PlanStepInfo();
    newStep.action = "SETGOALTASKS";
    newStep.children = tasks.subtasks;
    newStep.ordered = tasks.isOrdered();
    planStepList.add(newStep);

    //-- Call the helper function.
    findPlanHelper(tasks);

    //-- Initialize planStepList within JSHOP2GUI
    JSHOP2GUI.setPlanStepList(planStepList);

    //-- Initialize numPlans within JSHOP2GUI
    JSHOP2GUI.setNumPlans(numPlans);

    //-- Return the found plan(s).
    return plans;
  }

  /** This is the helper function that finds a plan.
   *
   *  @param chosenTask
   *          the task list chosen to look for the next task atom to achieve.
   *          This variable is usually set to the whole task network unless
   *          there is a method that is chosen to decomopose a task, and the
   *          decomposition of that task has not gone all the way down to an
   *          operator. In that case, this variable will be set to the task
   *          decomposed by that method.
   *  @return
   *          <code>true</code> if a plan is found, <code>false</code>
   *          otherwise.
  */
  private static boolean findPlanHelper(TaskList chosenTask)
  {
    //-- The local variables we need every time this function is called.
    InternalVars v = new InternalVars();
    
    //-- Find all the tasks that we have the option to achieve right now. This
    //-- equals to the first task in the current task list if it is ordered, or
    //-- the first task in all the subtasks of the current task list if it is
    //-- unordered. In the latter case, if there is an immediate task as the
    //-- first task of any of the subtasks, that immediate task and ONLY that
    //-- immediate task is returned.
    v.t0 = chosenTask.getFirst();

    //-- If there are no tasks left,
    if (v.t0.size() == 0)
    {
      //-- If the chosen task is not the whole task network the algorithm is
      //-- initially set to achieve, it means we have just achieved that task,
      //-- and not the whole task network. Therefore, try to achieve the rest
      //-- of the task network.
      if (chosenTask != tasks)
        return findPlanHelper(tasks);
      //-- Otherwise, add the current plan to the list of the plans for the
      //-- given task network. Note that in the case where we are looking for
      //-- more than one plan, we add a clone of the current plan to the list
      //-- rather than the current plan itself since the current plan will be
      //-- changed during the look for other plans.
      else {
        if (planNo != 1) {
          plans.addLast((Plan)currentPlan.clone());
        } else {
          plans.addLast(currentPlan);
        }

        PlanStepInfo newStep = new PlanStepInfo();
        newStep.planFound = true;
        planStepList.add(newStep);
        numPlans++;

        return true;
      }
    }

    //-- This array of size 4 will store the atoms and protections that are
    //-- deleted from and added to the current state of the world as a result
    //-- of an operator being applied. This information is used in case a
    //-- backtrack happens over that operator to store the state of the world
    //-- to what it was before the backtracked operator was applied.
    v.delAdd = new Vector[4];

    //-- To iterate over the tasks we have the option to achieve right now.
    v.e = v.t0.iterator();

    //-- For each of the tasks that we have the option to achieve right now,
    while (v.e.hasNext())
    {
      //-- Find the next option.
      v.tl = (TaskList)v.e.next();
      v.t = v.tl.getTask();

      //-- Create a TRYING step for the list of plan steps
      PlanStepInfo newStep = new PlanStepInfo();
      newStep.action = "TRYING";
      newStep.state = state.getState();
      newStep.taskAtom = v.t;
      planStepList.add(newStep);

      //-- If that task is primitive,
      if (v.t.isPrimitive())
      {
        //-- Remove the task from the task list, by replacing it with an empty
        //-- task list.
        v.tl.replace(TaskList.empty);

        //-- Find all the operators that achieve this primitive task.
        v.o = domain.ops[v.t.getHead().getHead()];

        //-- For each of these operators,
        for (v.j = 0; v.j < v.o.length; v.j++)
        {
          //-- Find the binding that unifies the head of the operator with the
          //-- task.
          v.binding = v.o[v.j].unify(v.t.getHead());

          //-- If there is such bindings,
          if (v.binding != null)
          {
            //-- Get the iterator that iterates over all the bindings that can
            //-- satisfy the precondition for this operator.
            v.p = v.o[v.j].getIterator(v.binding, 0);

            //-- For each such binding,
            while ((v.nextB = v.p.nextBinding()) != null)
            {
              //-- Merge the two bindings.
              Term.merge(v.nextB, v.binding);

              //-- If the operator is applicable, apply it, and,
              if (v.o[v.j].apply(v.nextB, state, v.delAdd))
              {
                //-- Add the instance of the operator that achieved this task
                //-- to the beginning of the plan, remembering how much it
                //-- cost.
                double cost = currentPlan.addOperator(v.o[v.j], v.nextB);

                //-- Create a STATECHANGED step for the list of plan steps
                newStep = new PlanStepInfo();
                newStep.action = "STATECHANGED";
                newStep.taskAtom = v.t;
                newStep.delAdd = v.delAdd;
                newStep.operatorInstance = v.o[v.j].getHead().applySubstitution(v.nextB).toString(JSHOP2.getDomain().getPrimitiveTasks());
                planStepList.add(newStep);

                //-- Recursively call the same function to achieve the
                //-- remaining tasks. If a plan is found for the remaining
                //-- tasks and we have found the maximum number of plans we are
                //-- allowed, return true.
                if (findPlanHelper(tasks) && plans.size() >= planNo)
                  return true;

                //-- Remove the operator from the current plan.
                currentPlan.removeOperator(cost);
              }

              //-- Undo the changes that were the result of applying this
              //-- operator, because we are backtracking here.
              state.undo(v.delAdd);
            }
          }
        }

        //-- Insert the task we chose to achieve first back where it was,
        //-- because we couldn't achieve it.
        v.tl.undo();
      }
      //-- If that task is compound,
      else
      {
        //-- Find all the methods that decompose this compound task.
        v.m = domain.methods[v.t.getHead().getHead()];

        //-- For each of these methods,
        for (v.j = 0; v.j < v.m.length; v.j++)
        {
          //-- Find the binding that unifies the head of the method with the
          //-- task.
          v.binding = v.m[v.j].unify(v.t.getHead());

          //-- If there is such binding,
          if (v.binding != null)
          {
            //-- Initially, precondition of no branch of this method has
            //-- already been satisfied, so set this variable to false.
            v.found = false;

            //-- Iterate on all the branches of this method. note the use of
            //-- 'v.found' in the condition for the 'for' loop. It is there
            //-- because of the semantics of the method branches in JSHOP2:
            //-- Second branch is considered only when there is no binding for
            //-- the first branch, the third branch is considered only when
            //-- there is no binding for the first and second branches, etc.
            for (v.k = 0; (v.k < v.m[v.j].getSubs().length) && !v.found; v.k++)
            {
              //-- Get the iterator that iterates over all the bindings that
              //-- can satisfy the precondition for this branch of this method.
              v.p = v.m[v.j].getIterator(v.binding, v.k);

              //-- For each such binding,
              while ((v.nextB = v.p.nextBinding()) != null)
              {
                //-- Merge the two bindings.
                Term.merge(v.nextB, v.binding);

                //-- Replace the decomposed task in task list with its
                //-- decomposition according to this branch of this method.
                v.tl.replace(v.m[v.j].getSubs()[v.k].bind(v.nextB));

                //-- Create a REDUCED step for the list of plan steps
                newStep = new PlanStepInfo();
                newStep.action = "REDUCED";
                newStep.taskAtom = v.t;
                newStep.children = v.tl.subtasks;
                newStep.ordered = v.m[v.j].getSubs()[v.k].isOrdered();
                newStep.method = v.m[v.j].getLabel(v.k);
                planStepList.add(newStep);

                //-- Recursively call the same function to achieve the
                //-- remaining tasks, but make the function choose its next
                //-- tasks to achieve to be the substasks of the task we just
                //-- decomposed, till an operator is seen and applied, or this
                //-- whole task is achieved without seeing an operator (i.e.,
                //-- this task was decomposed to an empty task list).
                if (findPlanHelper(v.tl) && plans.size() >= planNo)
                  //-- A full plan is found, return true.
                  return true;

                //-- The further branches of this method must NOT be considered
                //-- even if this branch fails because there has been at least
                //-- one satisfier for this branch of the method. Set this
                //-- variable to true to prevent the further branches of this
                //-- method from being considered.
                v.found = true;

                //-- Undo the changes in the task list, because this particular
                //-- decomposition failed.
                v.tl.undo();
              }
            }
          }
        }
      }

      //-- Create a BACKTRACKING step for the list of plan steps
      newStep = new PlanStepInfo();
      newStep.action = "BACKTRACKING";
      newStep.taskAtom = v.t;
      planStepList.add(newStep);
    }

    //-- Return false, because all the options were tried and none worked.
    return false;
  }

  /** This function returns the planning domain.
   *
   *  @return
   *          the current planning domain.
  */
  public static Domain getDomain()
  {
    return domain;
  }

  /** This function returns the current state of the world.
   *
   *  @return
   *          the current state of the world.
  */
  public static State getState()
  {
    return state;
  }

  /** This function is used to initialize the planning algorithm.
   *
   *  @param domainIn
   *          the planning domain.
   *  @param stateIn
   *          the initial state of the world.
  */
  public static void initialize(Domain domainIn, State stateIn)
  {
    domain = domainIn;
    state = stateIn;
  }
}
