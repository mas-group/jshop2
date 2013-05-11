package JSHOP2;

import java.util.ArrayList;
import java.util.Vector;


/**
 * This object encapsulates the information pertaining to a single step in the planning
 * process.  A sequence of these objects stored in a list represents the
 * steps taken during the process of finding a plan.  The step represented by this 
 * object is determined by the type of action that is being performed.  There 
 * are 6 types of actions: SETGOALTASKS, TRYING, REDUCED, STATECHANGED, BACKTRACKING,
 * and PLANFOUND. Depending on which action is being performed, a different subset of 
 * the fields in PlanStepInfo must be set as follows:
 * 
 * During a "SETGOALTASKS" step:
 *  - children: set to represent the goal task list
 *  - ordered: set to true if goal task list is ordered; false otherwise
 * 
 * During a "TRYING" step:
 *  - taskAtom: set to the current task atom (and its bindings) being tried (or !!INOP)
 *  - action: set to "TRYING"
 *  - state: set to a list of strings representing the current state of the world
 *  
 * During a "REDUCED" step:
 *  - taskAtom: set to the current task atom (and its bindings) being reduced 
 *  - action: set to "REDUCED"
 *  - method: set to method being used to reduce current task atom
 *  - preconditions: set to the preconditions that need to be satisfied to use method
 *  - children: set to a list of strings representing the task atoms that are the children
 *  - ordered: set to false if the current task atom is unordered.  True otherwise.
 *  
 * During a "STATECHANGED" step:
 *  - taskAtom: set to the top task that is changing the state
 *  - delAdd: set to a vector of 4 vectors that represent added & deleted atoms & protections
 *  - operatorInstance: set to the string representing 'taskAtom' after it's variables are instantiated
 *  
 * During a "BACKTRACKING" step:
 *  - taskAtom: set to the task atom that you are currently bactracking from
 *  
 * During a "PLANFOUND" step:
 *  - planFound: set to true  
 * 
 *  
 * @author John Shin
 *
 */
public class PlanStepInfo {
	
	// The current task atom object.
	public TaskAtom taskAtom;
	
	// Used during a STATECHANGED step.  Represents the task atom after all
	// bindings have been applied.  Use this field when displaying name of the operator,
	// but use taskAtom above to locate the right node in the decomposition tree.
	public String operatorInstance;
	
	// Equals "TRYING" if attempting to perform a task.  Equals "REDUCED" if
	// task is non-primitive and is being decomposed by a method.  
	// Equals "BACKTRACKING" if currently backtracking from the task atom
	// represented by "taskAtom"
	public String action;
	
	// The method being used to reduce the current task atom represented
	// by "taskAtom". Only set during a "REDUCED" command.
	public String method;
	
	// The preconditions that were satisfied in order to use the above method.
	// Only set during a "REDUCED" command.
    public String preconditions;
    
    // A list of strings representing the current state of the world.  Only
    // set during a "TRYING" command.
    public ArrayList<String> state;    
    
    // An array of TaskLists representing the task atoms that the current task atom
    // will be reduced into.  Only set during "REDUCED" and "SETGOALTASKS" commands.
    public TaskList[] children;
    
    public Vector[] delAdd;
    
    // Set to true if current task atom is ordered.  False otherwise
    public boolean ordered;
    
    // Set to true if the plan is found at this point
    public boolean planFound;
    
    
    public PlanStepInfo() {
    	taskAtom = null;
    	action = "";
    	method = "";
    	preconditions = "";
    	state = null;
    	children = null;
    	ordered = true;
    	planFound = false;
    }
    
    public void print() {
        System.out.print( "Task Atom: " + taskAtom + "\n" );
        System.out.print( "Action: " + action + "\n" );
        System.out.print( "Method: " + method + "\n" );
        System.out.print( "Preconditions: " + preconditions + "\n" );
        System.out.print( "Children: \n");            
        /*if ( children != null ) {
            for (int i = 0; i < children.size(); i++)
                System.out.print(children.get(i));
            System.out.print("\n");
        } else
            System.out.print("none\n");*/
        if (children != null)
        	for (int i = 0; i < children.length; i++)
        		children[i].print();
            
        System.out.print( "State: ");            
        if ( state != null ) {
            for (int i = 0; i < state.size(); i++)
                System.out.print(state.get(i));
            System.out.print("\n");
        } else
            System.out.print("none\n");
        
            
        System.out.print( "Ordered: ");
        if (ordered)
            System.out.print( "true\n");
        else
            System.out.print( "false\n");
            
        System.out.print("\n\n");
    }
}