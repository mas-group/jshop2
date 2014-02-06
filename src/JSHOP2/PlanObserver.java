package JSHOP2;

import java.util.ArrayList;

public interface PlanObserver {
  /**
   * This function is used to pass in the list of plan steps that represent the
   * actions taken by JSHOP2 to find plans for the current problem.
   * 
   * @param inputList - an ArrayList of PlanStepInfo objects that are used to
   * reconstruct the plan finding process
   */
  void setPlanStepList(ArrayList<PlanStepInfo> inputList);

  /**
   * This function is used to set the total number of plans found for the
   * current problem by JSHOP2
   * 
   * @param numPlansIn - an integer representing the total number of plans found
   */
  void setNumPlans(int numPlansIn);
}
