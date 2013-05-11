package JSHOP2;

import java.util.LinkedList;

/** The thread that invokes JSHOP2 to solve a planning problem. The only reason
 *  to have another thread to solve problems rather than using the main thread
 *  to do so is that, in some platforms, the command line parameters that are
 *  supposed to change the stack size work only for the threads other than the
 *  main thread.
 *
 *  @author Okhtay Ilghami
 *  @author <a href="http://www.cs.umd.edu/~okhtay">http://www.cs.umd.edu/~okhtay</a>
 *  @version 1.0.3
*/
public class SolverThread extends Thread
{
  /** The maximum number of plans allowed.
  */
  private int planNo;

  /** The list of plans to be returned.
  */
  private LinkedList<Plan> plans;

  /** The task list to be achieved.
  */
  private TaskList tl;

  /** To initialize this thread.
   *
   *  @param tlIn
   *          the task list to be achieved by this thread.
   *  @param planNoIn
   *          the maximum number of plans allowed.
  */
  public SolverThread(TaskList tlIn, int planNoIn)
  {
    tl = tlIn;
    planNo = planNoIn;
  }

  /** To return the plans found by this thread. This function should be called
   *  only after the thread has died.
   *
   *  @return
   *          A <code>LinkedList</code> of plans found by this thread.
  */
  public LinkedList<Plan> getPlans()
  {
    return plans;
  }

  /** The function that is called when this thread is invoked.
  */
  public void run()
  {
    //-- Solve the planning problem.
    plans = JSHOP2.findPlans(tl, planNo);
  }
}

