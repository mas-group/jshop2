package JSHOP2;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * This is the graphical interface for JSHOP2
 * 
 * @author John Shin
 *
 */
public class JSHOP2GUI extends JFrame {
	private static final long serialVersionUID = 112832006;
	
	// The list of plan steps dynamically set by JSHOP2
	protected static ArrayList<PlanStepInfo> planStepList;
	
	// The number of plans found in the current problem.  This is
	// dynamically set by JSHOP2
	protected static int numPlans;
	
//-----------------------------------------------------------------------------
	
    // Used to iterate through 'planStepList'
    protected int iterator;
    
    // Used in various places when a global counter is convenient    
    protected int count;
    
    // Tracks which plan is currently being worked on    
    protected int planNumber;
    
    // Holds the data that make up a command from the input    
    protected PlanStepInfo newCommand;
    
    // A string that temporarily holds the method info that will be
    // transferred to the "reduced" command that will follow
    protected String newMethod;
    
    // A vector to store the leaf nodes that make up the plan.  The elements are
    // DefaultMutableTreeNode's, and their indices represent the order in which they were
    // visited.
    protected ArrayList<DefaultMutableTreeNode> leafNodes;
    
    // The tree object that is displayed on-screen    
    protected JTree tree;
    
    // The tree model that contains all the data within the tree structure    
    protected DefaultTreeModel treeModel;
    
    // The invisible root node that is the parent for the first node displayed in the tree    
    protected DefaultMutableTreeNode rootNode;
    
    // The label that displays the action taken at every step of the plan    
    protected Label messageLabel;
    
    protected Label stateLabel;    
    
    // The text area where information about the current state and preconditions is displayed.
    protected TextArea stateTextArea;
    
    protected TextArea stepInfoTextArea;
    
    // The text input box used to specifiy the step interval length
    protected JTextField multiStepField;
    
    // A hashtable of DefaultMutableTreeNode references used to keep track of the nodes in the tree.
    // Currently, nodes that are deleted in the tree when backtracking aren't deleted in this
    // hashtable.  This doesn't seem to cause a problem except that it may become inefficient for 
    // large domains that backtrack a lot.  In those cases, a search for a particular node within
    // treeNodeReferences may waste a lot of time looking over nodes that have been deleted.
    protected Hashtable<Integer, DefaultMutableTreeNode> treeNodeReferences;
    
    // The progress bar object    
    protected JProgressBar progressBar;
    
    // The label used to show the number of steps in the plan
    protected Label progressLabel;  
    
    protected LeafTrackerDialog leafTracker;
    
    // The name of the currently selected node
    protected String selectedNodeName; 
    
    // Holds the dimensions of the screen.  Used to center all frames and dialog boxes.
    protected Dimension screenSize;    

//-----------------------------------------------------------------------------
    /**
     * The default constructor.  Call this constructor only after the plan step
     * list has been initialized by <code>setPlanStepList</code> and the the 
     * number of plans has been set by <code>setNumPlans</code>.  When called
     * successfully, this constructor will launch the GUI. 
     * 
     */
    public JSHOP2GUI() {
    	// Calculate values to center all frames and dialog boxes
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
    	screenSize = toolkit.getScreenSize();    	
    	initFieldsAndCreateInterface();    	
    	int x = (screenSize.width - getWidth()) / 2;
    	int y = (screenSize.height - getHeight()) / 2;
    	
    	// Center this frame's location
    	setLocation(x, y);
    	
    	progressBar.setMaximum( planStepList.size() );
        progressBar.setValue( 0 );
        String msg = "Progress:  ";
        msg += String.valueOf( iterator );
        msg += " / ";
        msg += String.valueOf( planStepList.size() );
        progressLabel.setText( msg );
        setVisible( true );
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * This function is used to pass in the list of plan steps that represent the 
     * actions taken by JSHOP2 to find plans for the current problem.
     * 
     * @param inputList - an ArrayList of PlanStepInfo objects that are used to
     * reconstruct the plan finding process
     */
    public static void setPlanStepList(ArrayList<PlanStepInfo> inputList) {
    	planStepList = inputList;
    }
    
    /**
     * This function is used to set the total number of plans found for the current
     * problem by JSHOP2
     * 
     * @param numPlansIn - an integer representing the total number of plans found
     */
    public static void setNumPlans(int numPlansIn) {
    	numPlans = numPlansIn;
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * Initializes all fields and constructs the graphical interface 
     */
    private void initFieldsAndCreateInterface() {
    	iterator = 0;
        count = 0;
        planNumber = 0;
        treeNodeReferences = new Hashtable<Integer, DefaultMutableTreeNode>();
        getContentPane().setLayout( new BorderLayout() );
        
        newMethod = "";
        selectedNodeName = "";
        
       
       // createPlanList();
        leafNodes = new ArrayList<DefaultMutableTreeNode>();
        
        // Creating the leafTracker object
        leafTracker = new LeafTrackerDialog( this );
        
        // Creating the menu bar and menus
        MenuBar mbar = new MenuBar();
        setMenuBar( mbar );
        SHOP2GUIMenuHandler menuHandler = new SHOP2GUIMenuHandler();
        
        MenuItem item1, item2;
        Menu file = new Menu( "File" );        
        file.add( item1 = new MenuItem("Exit") );
        mbar.add( file ); 
        
        
        // Registering listeners for File menu items        
        item1.addActionListener( menuHandler );
        
        Menu view = new Menu( "View" );        
        view.add( item1 = new MenuItem("Leaf Node Tracker...") );
        view.add( item2 = new MenuItem("Show State..."));
        mbar.add( view );
        
        // Registering listeners for View menu items
        item1.addActionListener( menuHandler );
        item2.addActionListener( menuHandler );
     
        
        
        // ********************************************************* //
        // ------------------ Creating the Center ------------------ //
        // ********************************************************* //        
        
        // Creating tree and its scroll pane
        rootNode = new DefaultMutableTreeNode( "ROOTNODE" );
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree( treeModel );
        tree.setRootVisible(false);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setCellRenderer( new NodeRenderer() ); // setting cell rendered to paint nodes
        ToolTipManager.sharedInstance().registerComponent(tree); //enable tool tips

        
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        
        // Adding a treeSelectionListner to the tree
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {                
                DefaultMutableTreeNode treeNode = 
                            (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
                Node node = (Node)treeNode.getUserObject();
                
                // setting the name for the currently selected node
                selectedNodeName = node.name;
                
                // setting the text for stateTextArea
                String msg = node.getState(); 
                stateTextArea.replaceRange( msg, 0, stateTextArea.getText().length() );
                stateTextArea.setCaretPosition(0);
                
                // setting the text for Plan Step Info
                msg = "";
                if (!node.method.equals("")) {
                	msg += "METHOD USED:\n";
                	msg += node.method;
                	
                } else if (node.delAdd != null) {                	
                	if (node.delAdd[0] != null) {
                		Vector v = node.delAdd[0];
                		if (v.size() > 0) {
                			msg += "DELETED ATOMS:\n";                			
                			for (int i = 0; i < v.size(); i++) {
                				msg += v.get(i).toString();
                				msg += "\n";
                			} msg += "\n";  
                		}
                	} if (node.delAdd[1] != null) {
                		Vector v = node.delAdd[1];
                		if (v.size() > 0) {                			
                			msg += "ADDED ATOMS:\n";                		
                			for (int i = 0; i < v.size(); i++) {
                				msg += v.get(i).toString();
                				msg += "\n";
                			} msg += "\n";
                		}
                	} if (node.delAdd[2] != null) {
                		Vector v = node.delAdd[2];
                		if (v.size() > 0) {
                			msg += "DELETED PROTECTIONS:\n";                		
                			for (int i = 0; i < v.size(); i++) {
                				msg += v.get(i).toString();     
                				msg += "\n";
                			} msg += "\n";
                		}
                	} if (node.delAdd[3] != null) {
                		Vector v = node.delAdd[3];
                		if (v.size() > 0) {
                			msg += "ADDED PROTECTIONS:\n";                		
                			for (int i = 0; i < v.size(); i++) {
                				msg += v.get(i).toString();
                				msg += "\n";
                			} msg += "\n";
                		}
                	}
                }
                
                stepInfoTextArea.replaceRange( msg, 0, stepInfoTextArea.getText().length() );
                stepInfoTextArea.setCaretPosition(0);
                
                msg = "Current State          ( Total: ";
                msg += node.getStateSize();
                msg += " )";
                
                stateLabel.setText( msg );
            }
        });        
        
        JScrollPane jpane = new JScrollPane(tree);
        
        getContentPane().add( jpane, BorderLayout.CENTER );        
        
        
        // ****************************************************** //
        // --------------- Creating the East Side --------------- //
        // ****************************************************** //        
        
        // Creating buttons
        JButton singleStepButton = new JButton( "Single Step" );
        singleStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runOneStep();
            }
        });
        
        
        JButton multiStepButton = new JButton( "Multi-Step" );
        multiStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int numSteps = ( Integer.valueOf(multiStepField.getText()) ).intValue();                
                for ( int i = 0; i < numSteps; i++ ) {
                    if ( runOneStep() == false )
                        break;
                }
                /*
                for (int i = 0; i < commandList.size(); i++)
                    ((Command)commandList.elementAt(i)).print();
                */
            }
        });
        
        JButton runButton = new JButton( "Run" );
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                while ( runOneStep() ) {}
            }
        });        
 
        
        JButton restartButton = new JButton( "Restart" );
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iterator = 0;
                planNumber = 0;
                treeNodeReferences.clear();
                
                // clearing the tree model and refreshing the display
                rootNode.removeAllChildren();
                treeModel.reload();
                
                // clearing the text in the top status label
                messageLabel.setText("");
                
                // clearing text in the bottom message boxes
                stateTextArea.replaceRange( "", 0, stateTextArea.getText().length() );
                stepInfoTextArea.replaceRange( "", 0, stepInfoTextArea.getText().length() );
                
                // clearing the list of leaf nodes
                leafNodes.clear();
                
                // resetting progress bar and label
                String msg = "Progress:  0 / ";
                msg += String.valueOf( planStepList.size() );
                progressLabel.setText( msg );
                progressBar.setValue( 0 );
                
                // resetting stateLabel
                stateLabel.setText( "Current State          ( Total: -- )" );
                
                // updating leaf node tracker
                leafTracker.updateNodeCount();
                
            }
        });
        
        // Creating the multi-step size input field
        multiStepField = new JTextField("1");
        
        // Adding components        
        JPanel innerPanel_1 = new JPanel();
        innerPanel_1.setLayout( new GridLayout(0, 1, 5, 5) );
        innerPanel_1.add( multiStepField );
        innerPanel_1.add( multiStepButton );
        innerPanel_1.add( singleStepButton );
        innerPanel_1.add( runButton );
        innerPanel_1.add( restartButton );
        
        JPanel innerPanel_2 = new JPanel();
        innerPanel_2.setLayout( new GridLayout(0, 1) );
        innerPanel_2.add( new Label( "Multi-step size:", Label.CENTER ) );
        innerPanel_2.add( multiStepField );
        
        
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout( new BorderLayout() );        
        outerPanel.add( innerPanel_1, BorderLayout.CENTER );
        outerPanel.add( innerPanel_2, BorderLayout.NORTH );
        
        getContentPane().add( outerPanel, BorderLayout.EAST );
        
        
        
        // ******************************************************* //
        // --------------- Creating the South Side --------------- //
        // ******************************************************* //        
        
        // Creating message area  
        outerPanel = new JPanel();
        getContentPane().add( outerPanel, BorderLayout.SOUTH ); 
        outerPanel.setLayout( new BorderLayout() );
        
        JPanel messagePanel = new JPanel();
        outerPanel.add( messagePanel, BorderLayout.CENTER );
        messagePanel.setLayout( new GridLayout(1, 0) );
        
        JPanel leftMessagePanel = new JPanel();
        messagePanel.add( leftMessagePanel );
        leftMessagePanel.setLayout( new BorderLayout() );
        stateTextArea = new TextArea();
        stateTextArea.setEditable( false );       
        leftMessagePanel.add( stateTextArea, BorderLayout.CENTER );
        stateLabel = new Label( "Current State          ( Total: -- )" );
        leftMessagePanel.add( stateLabel, BorderLayout.NORTH );        
        
        JPanel rightMessagePanel = new JPanel();
        messagePanel.add( rightMessagePanel );
        rightMessagePanel.setLayout( new BorderLayout() );
        stepInfoTextArea = new TextArea();
        stepInfoTextArea.setEditable( false );       
        rightMessagePanel.add( stepInfoTextArea, BorderLayout.CENTER );  
        rightMessagePanel.add( new Label("Step Info"), BorderLayout.NORTH );
        
        
        // Creating progress bar
        progressBar = new JProgressBar();
        progressLabel = new Label( "Progress:  0 / 0" );
        
        innerPanel_1 = new JPanel();
        outerPanel.add( innerPanel_1, BorderLayout.EAST );        
        innerPanel_1.setLayout( new BorderLayout() );        
        
        innerPanel_2 = new JPanel();
        innerPanel_1.add( innerPanel_2, BorderLayout.NORTH );
        innerPanel_2.setLayout( new GridLayout(0,1) );
        innerPanel_2.add( progressLabel );
        innerPanel_2.add( progressBar );
        
        
        
        // ******************************************************* //
        // --------------- Creating the North Side --------------- //
        // ******************************************************* //        
        
        // Creating message Label        
        messageLabel = new Label(""); 
        getContentPane().add( messageLabel, BorderLayout.NORTH );
        
        
        
        // ********************************************************* //
        // --------------- Making the window visible --------------- //
        // ********************************************************* //        
        
        // Create main window
        setSize( new Dimension(800, 700) );
        setTitle( "Graphical Interface for JSHOP2" );
       
        
        // Register listeners
        addWindowListener( new SHOP2GUIWindowAdapter() );
        tree.addKeyListener( new SHOP2GUIKeyAdapter() );
        
        
    }    
    
//-----------------------------------------------------------------------------    
    
    /**
     * Executes a single step in the plan step list
     */    
    private boolean runOneStep() {
        boolean retval = true;
        ArrayList<Node> toAdd = new ArrayList<Node>();
        DefaultMutableTreeNode parent = null;
        
        if ( iterator < planStepList.size() ) {
            PlanStepInfo step = planStepList.get(iterator++);
            
            // setting progress bar
            String msg = "Progress:  ";
            msg += String.valueOf( iterator );
            msg += " / ";
            msg += String.valueOf( planStepList.size() );
            progressLabel.setText( msg );
            progressBar.setValue( iterator );
            
            
            // if a plan has been found            
            if ( step.planFound == true ) {
                processPlanFound();
                
                // retval is set to false here so that the "run" feature will stop every time
                // a plan is found.  
                retval = false;
            }
            
            // trying a task
            else if ( step.action.equals("TRYING") ) {
                // If the last step was a "plan found" step, then the leafNodes
                // vector has to be cleared now that it is working on a new plan.  Here,
                // iterator is subtracted by 2 due to the fact that it's been post-incremented
                // above.  This step assumes that a "plan found" step will always be followed
                // by a "trying" step, and it's implemented to enable dynamic leaf node tracking.
                if ( iterator - 2 >= 0 )
                    if ( planStepList.get(iterator - 2).planFound == true )
                        leafNodes.clear();
                parent = processTrying( step, toAdd );
            }
                
            // reducing a task
            else if ( step.action.equals("REDUCED") )
                parent = processReduced( step, toAdd );
            
            else if ( step.action.equals("STATECHANGED") )
            	parent = processStateChanged( step );
            
            else if ( step.action.equals("SETGOALTASKS")) 
            	parent = processSetGoalTasks(step, toAdd);            
                
            // backtracking   
            else if ( step.action.equals("BACKTRACKING") )
                processBacktracking( step );
                
            // updating leaf node tracker
            leafTracker.updateNodeCount();
            
            // adding nodes to treeModel and treeNodeReferences    
            for (int i = 0; i < toAdd.size(); i++) {
                Node add = toAdd.get(i);               
                DefaultMutableTreeNode child = new DefaultMutableTreeNode( add );
                treeNodeReferences.put( add.ID, child );
                treeModel.insertNodeInto( child, parent, parent.getChildCount() );
                tree.scrollPathToVisible(new TreePath(child.getPath())); // makes the node visible
                if (iterator == 1) // special case when displaying the goal task
                    tree.setSelectionPath(new TreePath(child.getPath())); 
            }
            
            // Ensures that after a REDUCED and STATECHANGED step, a 'valueChanged' event will be generated
            // for the parent so the updated info will be displayed in the GUI.
            if (step.action.equals("REDUCED") || step.action.equals("STATECHANGED")) {
            	TreePath parentPath = new TreePath(parent.getPath());
            	TreeSelectionEvent e = new TreeSelectionEvent(parent, parentPath, false,
            			parentPath, parentPath);            	
            	TreeSelectionListener listener = tree.getTreeSelectionListeners()[0];
            	listener.valueChanged(e);
            }
            
        } else
            retval = false;
        
        return retval;
    }    
    
//-----------------------------------------------------------------------------  
    
    /**
     * Helper function to runOneStep().
     * This function executes the steps required to display the plan on-screen
     */
    private void processPlanFound() {
        planNumber++;
        ArrayList<String> plan = new ArrayList<String>(); // vector of strings containing the leaves' numbered names
        
        // setting messageLabel
        messageLabel.setText( "Plan found" );
        
        // Labeling leaf nodes with appropriate numbers.
        // Although renumberLeaves() does the same thing, it isn't called here
        // so that planList can be created in the process of renumbering the leaves.
        for (int i = 0; i < leafNodes.size(); i++) {                    
            DefaultMutableTreeNode leaf = leafNodes.get(i);
            Node node = (Node)leaf.getUserObject();
            node.tag = i+1;            
            plan.add( node.toString() );
            
            treeModel.nodeChanged( leaf );
        }        
        
        // Creating found plan dialog box
        String title = "Plan ";
        title += String.valueOf( planNumber );
        title += " of ";
        title += String.valueOf( numPlans );
        new PlanDialog( title, plan );
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * Helper function to runOneStep().
     * This function determines the current state of the world for every node
     * and inserts the goal task into the tree.
     */    
    private DefaultMutableTreeNode processTrying( PlanStepInfo step, ArrayList<Node> toAdd ) {        
        DefaultMutableTreeNode parent = null;
        
        // set messageLabel
        String msg = "Trying ";
        msg += step.taskAtom;
        messageLabel.setText( msg );                
                    
        // if adding the root node
        if ( iterator == 1 ) {
            parent = rootNode;
            Node temp = new Node( step.taskAtom.toString(), step.taskAtom.getHead().getID() );
            temp.state = step.state;
            toAdd.add( temp );
            
            // I should add temp to the leafNodes list here, but it's problematic
            // because I don't have access to its DefaultMutableTreeNode wrapper object
            // due to it not being created yet.  This is only a problem in the trivial case
            // where the plan consists of only the root node, so I deemed it unnecessary to
            // deal with...for now.
            
        } else { // setting node's state to current state
            DefaultMutableTreeNode treeNode = treeNodeReferences.get( step.taskAtom.getHead().getID() );
            Node temp = (Node)treeNode.getUserObject();
            temp.state = step.state;  
            treeModel.nodeChanged( treeNode ); 
            
            // adding the node as a leaf, as long as it's not (!!INOP)
            if ( !step.taskAtom.equals("(!!INOP)") ) {
                leafNodes.add( treeNode );
                renumberLeaves();
            }
            
            // selecting the node onscreen                       
            tree.setSelectionPath(new TreePath(treeNode.getPath())); 
            tree.scrollPathToVisible(new TreePath(treeNode.getPath())); // makes the node visible
        }
        
        return parent;
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * Helper function to runOneStep().
     * This function adds children to existing nodes and marks them if they are ordered.
     */  
    private DefaultMutableTreeNode processReduced( PlanStepInfo step, ArrayList<Node> toAdd ) {
        DefaultMutableTreeNode parent = null;        
        
        // set messageLabel
        String msg = "Reduced ";
        msg += step.taskAtom;
        msg += " into the following: ";
        
        // The term "parent" here is referring to the current node (parent as in parent of
        // the children that are about to be added).
        parent = treeNodeReferences.get( step.taskAtom.getHead().getID() );
        
        // Setting the method name for this node       
        Node node = (Node)parent.getUserObject();
        node.method = step.method;
        
        // Removing this node from the leaf nodes list, since it now has children.
        // This step assumes that a REDUCED statement will always follow a TRYING
        // statement pertaining to the same task atom, resulting in the deletion of the 
        // last element in leafNodes.
        if ( !leafNodes.isEmpty() ) {
            // removing numbering from this node
            node.tag = null;
            treeModel.nodeChanged( parent );
            
            // removing this node from leafNodes list
            leafNodes.remove( leafNodes.size()-1 );
        }
        
        // backtrack if this node has already been reduced and is being reduced again  
        if ( !parent.isLeaf() )
            backtrack( parent );
        
        // selecting the node onscreen         
        tree.setSelectionPath(new TreePath(parent.getPath()));
        tree.scrollPathToVisible(new TreePath(parent.getPath())); // makes the node visible
    
        // creating the children to be added
        for ( int i = 0; i < step.children.length; i++ ) {
            String childName = step.children[i].getTask().toString();
            Node newNode = new Node( childName, step.children[i].getTask().getHead().getID() );
            msg += childName;
            msg += " ";
            if ( !step.ordered )
                newNode.ordered = false;
            toAdd.add(newNode);
        } 
     
        messageLabel.setText( msg ); 
        
    	return parent;
    }
 
//-----------------------------------------------------------------------------
    
    private DefaultMutableTreeNode processStateChanged(PlanStepInfo step) {
    	DefaultMutableTreeNode retval = null;
    	
    	// set messageLabel
        String msg = "State changed by ";
        msg += step.operatorInstance;
        messageLabel.setText( msg );
        
        retval = treeNodeReferences.get( step.taskAtom.getHead().getID() );
        
        // Setting delAdd for this node       
        Node node = (Node)retval.getUserObject();
        node.delAdd = step.delAdd;
        
        // Changing name to intantiated version       
        node.name = step.operatorInstance;     
        
        // selecting the node onscreen         
        tree.setSelectionPath(new TreePath(retval.getPath()));
        tree.scrollPathToVisible(new TreePath(retval.getPath())); // makes the node visible
    	
    	return retval;
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * Sets the goal tasks for the problem.  The goal tasks are stored in the 'children' field
     * of the 'step' parameter.  Each element of 'children' represents a TaskList that encapsulates
     * a goal task.  Calling getTask() on each TaskList gives access to the TaskAtom that represents
     * the goal task.  If getTask() returns null, then that means the goal task is encapsulated
     * one or more levels down and getSubtasks() must be called.  This is done recursively by
     * 'setGoalTasksHelper'.  
     */
    private DefaultMutableTreeNode processSetGoalTasks(PlanStepInfo step, ArrayList<Node> toAdd) {
    	DefaultMutableTreeNode retval = null;    	
    	retval = rootNode;    	
    	setGoalTasksHelper(step.children, step, toAdd);    	
    	messageLabel.setText("Goal tasks added");    	
    	return retval;
    }
    
    private void setGoalTasksHelper(TaskList[] children, PlanStepInfo step, ArrayList<Node> toAdd) {
    	for (int i = 0; i < children.length; i++) {
    		TaskList child = children[i];
    		if (child.getTask() != null) {
    			Node temp = new Node( child.getTask().toString(), child.getTask().getHead().getID());
    			temp.state = step.state;
    			temp.ordered = step.ordered;    			
    			toAdd.add( temp );
    		} else if (child.getSubtasks() != null)
    			setGoalTasksHelper(child.getSubtasks(), step, toAdd);    		
    	}    	
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * Helper function to runOneStep().
     * This function takes care of backtracking procedures.
     */
    private void processBacktracking( PlanStepInfo step ) {
        String msg = "Backtracking from ";
        msg += step.taskAtom;
        messageLabel.setText( msg );
        
        DefaultMutableTreeNode treeNode = treeNodeReferences.get( step.taskAtom.getHead().getID() );
                    
        if ( !treeNode.isLeaf() )        
            backtrack( treeNode );
        
        // If backtracking from a leaf node, check to see if this leaf node was the most recent
        // leaf node to be added to 'leafNodes'.  If true, then delete this leaf node's tag and
        // remove it from 'leafNodes'.  
        // <Note:> I'm assuming that whenever you backtrack from a leaf node, that leaf node will
        // always have the highest tag value in 'leafNodes'.  I'm not certain if this is always the case.
        else {
        	Node treeNodeUserObj = (Node)treeNode.getUserObject();
        	Node leafNodeUserObj = (Node)leafNodes.get(leafNodes.size()-1).getUserObject();
        	
        	if (treeNodeUserObj.ID == leafNodeUserObj.ID) {
        		// removing numbering from this node
                treeNodeUserObj.tag = null;
                treeModel.nodeChanged( treeNode );
                
                // removing this node from leafNodes list
                leafNodes.remove( leafNodes.size()-1 );
        	}
        }
        
        // selecting the node onscreen 
        tree.setSelectionPath(new TreePath(treeNode.getPath())); // selects the node
        tree.scrollPathToVisible(new TreePath(treeNode.getPath())); // makes the node visible
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * This function handles the procedures that take place when backtracking.
     * First, it modifies the leafNodes list and then deletes any children under
     * treeNode.
     */
    private void backtrack( DefaultMutableTreeNode treeNode ) {
        // removing any leaves in leafNodes that are descendants of treeNode
        for (int i = 0; i < leafNodes.size(); i++) {
            if ( treeNode.isNodeDescendant(leafNodes.get(i)) ) {
                leafNodes.remove(i);
                i--;
            }
        }        
        
        // reunumbering leaves on-screen
        renumberLeaves();
            
        // deleting children from this node            
        treeNode.removeAllChildren();        
        
        treeModel.reload( treeNode );
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * This function renumbers all current leaf nodes in the order they were visited
     * relative to one another.
     */
    private void renumberLeaves() {
        for (int i = 0; i < leafNodes.size(); i++) {
            DefaultMutableTreeNode leaf = leafNodes.get(i);
            Node node = (Node)leaf.getUserObject();
            node.tag = i+1;
            
            treeModel.nodeChanged( leaf );
        }
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * Listener Classes    
     */    
    private class SHOP2GUIWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }
    }
    
//-----------------------------------------------------------------------------
    
    private class SHOP2GUIKeyAdapter extends KeyAdapter {
        public void keyTyped(KeyEvent ke) {
            if ( ke.getKeyChar() == ' ' ) {
                runOneStep();
            }
        }
    }
    
//-----------------------------------------------------------------------------
    
    private class SHOP2GUIMenuHandler implements ActionListener {
        public void actionPerformed( ActionEvent ae ) {
            String arg = (String)ae.getActionCommand();
            if ( arg.equals("Exit") )
                System.exit(0);
            else if ( arg.equals("Leaf Node Tracker...") )            	
            	leafTracker.visible( true );           
            else if ( arg.equals("Show State...")) {            	
            	String title = "State for ";
            	title += selectedNodeName;
                new StateWindowDialog( title );
            }
        } 
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * User object utilized by the tree model    
     */
    private class Node {
        public String name;
        public String method;
        public boolean ordered;
        public ArrayList<String> state;
        public Integer tag;
        public int ID;
        public Vector[] delAdd;
        
        public Node( String nameIn, int IDin ) {
            name = nameIn;
            method = "";
            ordered = true;
            state = null;
            tag = null;
            ID = IDin;
            delAdd = null;
        }
        
        public String toString() {
            if ( tag == null )
                return name;
            else {
                String newName = "[ ";
                newName += String.valueOf( tag.intValue() );
                newName += " ]    ";
                newName += name;
                return newName;
            }
        }
        
        public boolean hasState() {
            return state != null;
        }
        
        public String getState() {
            String retval = null;
            
            // the state is unknown
            if ( state == null)
                retval = "Unknown";
            // the state is empty
            else if ( state.size() == 0 ) {
                retval = "";
            } else {                
                retval = state.get(0);          
                for ( int i=1; i < state.size(); i++ ) {
                    retval += "\n";
                    retval += state.get(i);                
                }            
            }
            return retval;
        }
        
        public String getStateSize() {
            String retval = null;
            
            // if the state is unknown
            if ( state == null )
                retval = "--";
            
            // if the state is known
            else 
                retval = String.valueOf( state.size() );            
            
            return retval;
        }
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * Object used to render the nodes in the tree
     */    
    private class NodeRenderer extends DefaultTreeCellRenderer {
    	private static final long serialVersionUID = 112832007;
        ImageIcon yellowBall, blueBall, smYellowBall, smBlueBall;        
        
        public NodeRenderer() {
            yellowBall = new ImageIcon( "images/yellow.gif" );
            blueBall = new ImageIcon( "images/blue.gif" );
            smYellowBall = new ImageIcon( "images/small-yellow.gif" );
            smBlueBall = new ImageIcon( "images/small-blue.gif" );
        }
        
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
                            
            if ( value instanceof DefaultMutableTreeNode ) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
                if ( isOrdered(treeNode) ) {                    
                    if ( isVisited(treeNode) ) {
                        setIcon( yellowBall );
                        setToolTipText( "Ordered, Visited" );
                    } else {
                        setIcon( smYellowBall );
                        setToolTipText( "Ordered, Unvisited" );
                    }
                    
                } else {                  
                    if ( isVisited(treeNode) ) {                        
                        setIcon( blueBall );
                        setToolTipText( "Unordered, Visited" );
                    } else {
                        setIcon( smBlueBall );
                        setToolTipText( "Unordered, Unvisited" );
                    }
                } 
            }

            return this;
        }   
        
        private boolean isOrdered( DefaultMutableTreeNode treeNode ) {
            if ( treeNode.getUserObject() instanceof Node ) {
                Node node = (Node)treeNode.getUserObject();            
                return node.ordered;
            } else
                return false;
        }
        
        
        private boolean isVisited( DefaultMutableTreeNode treeNode ) {
            if ( treeNode.getUserObject() instanceof Node ) {
                Node node = (Node)treeNode.getUserObject();            
                return node.hasState();
            } else
                return false;
        }

    }
        
//-----------------------------------------------------------------------------
    
    /**
     * The object that creates the dialog box showing plan results whenever
     * a plan is found.
     */    
    private class PlanDialog extends JDialog {
    	private static final long serialVersionUID = 112832008;
        public PlanDialog(  String title, ArrayList<String> plan ) {
            setTitle( title );
            
            getContentPane().setLayout( new FlowLayout(FlowLayout.CENTER, 5, 20) );
            
            // Creating message string to display in text area            
            String msg = "";
            for ( int i = 0; i < plan.size(); i++ ) {
                msg += plan.get(i);
                msg += "\n";
            }
            
            // Creating the text area that will display the plan            
            TextArea textBox = new TextArea( msg, 24, 63 );
            textBox.setEditable( false );
            
            // Creating the "Close" button
            JButton closeButton = new JButton( "  Close  " );
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            
            // Adding components to the dialog box          
            getContentPane().add( textBox );
            getContentPane().add( closeButton );
           
            
            setSize( new Dimension(500,500) );
            
            // Center dialog box
            int x = (screenSize.width - getWidth()) / 2;
            int y = (screenSize.height - getHeight()) / 2;
            setLocation(x, y);
            
            setVisible(true);
        }
    }
   
//-----------------------------------------------------------------------------
    
    private class StateWindowDialog extends JDialog {
    	private static final long serialVersionUID = 112832009;
    	
    	public StateWindowDialog( String title ) {
    		setTitle( title );
    		getContentPane().setLayout( new FlowLayout(FlowLayout.CENTER, 5, 20) );
    		
    		 // Creating the text area that will display the plan            
            TextArea textBox = new TextArea( stateTextArea.getText(), 24, 63 );
            textBox.setEditable( false );
            
            // Creating the "Close" button
            JButton closeButton = new JButton( "  Close  " );
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            
            // Adding components to the dialog box          
            getContentPane().add( textBox );
            getContentPane().add( closeButton );               
            
            setSize( new Dimension(500,500) );    
            
            // Center dialog box
            int x = (screenSize.width - getWidth()) / 2;
            int y = (screenSize.height - getHeight()) / 2;
            setLocation(x, y);
            
            setVisible(true);
    	}
    	
    }
    
//-----------------------------------------------------------------------------
    
    /**
     * The object that creates the leaf node tracker dialog box.
     */
    private class LeafTrackerDialog extends JDialog {
    	private static final long serialVersionUID = 112832010;
        private Label leafTotalLabel;
        private TextField leafNumberField;
        
        public LeafTrackerDialog( Frame parent ) {
            super( parent, "Leaf Node Tracker" );
            getContentPane().setLayout( new BorderLayout() );
            
                // adding leafTotalLabel
            leafTotalLabel = new Label("Leaf Nodes:    0");
            getContentPane().add( leafTotalLabel, BorderLayout.NORTH );
        
                // creating a new border layout container for the input text field
            JPanel innerPanel_1 = new JPanel();
            getContentPane().add( innerPanel_1, BorderLayout.CENTER );        
            innerPanel_1.setLayout( new BorderLayout() );
            JPanel innerPanel_2 = new JPanel();
            innerPanel_1.add( innerPanel_2, BorderLayout.NORTH );
            innerPanel_2.setLayout( new GridLayout(1,0) );
            innerPanel_2.add( new Label("Leaf number:") );
            leafNumberField = new TextField("1");
            innerPanel_2.add( leafNumberField );
        
        
                // creating a new border layout container for the buttons
            innerPanel_2 = new JPanel();
            innerPanel_1.add( innerPanel_2, BorderLayout.CENTER );        
            innerPanel_2.setLayout( new BorderLayout() );
            JPanel innerPanel_3 = new JPanel();
            innerPanel_2.add( innerPanel_3, BorderLayout.NORTH );
            innerPanel_3.setLayout( new GridLayout(1,0) );
        
                // adding buttons
            JButton prevButton = new JButton("Prev");
            prevButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int leafNum = ( Integer.valueOf(leafNumberField.getText()) ).intValue();
                    leafNum--;
                    if ( leafNum > 0 && leafNum <= leafNodes.size() ) {
                        leafNumberField.setText( String.valueOf(leafNum) );
                        DefaultMutableTreeNode treeNode = leafNodes.get(leafNum - 1);
                        tree.setSelectionPath(new TreePath(treeNode.getPath())); // selects the node
                        tree.scrollPathToVisible(new TreePath(treeNode.getPath())); // makes the node visible
                    }
                
                }
            });
        
            JButton findButton = new JButton("Find");
            findButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int leafNum = ( Integer.valueOf(leafNumberField.getText()) ).intValue();
                    if ( leafNum > 0 && leafNum <= leafNodes.size() ) {
                        DefaultMutableTreeNode treeNode = leafNodes.get(leafNum - 1);
                        tree.setSelectionPath(new TreePath(treeNode.getPath())); // selects the node
                        tree.scrollPathToVisible(new TreePath(treeNode.getPath())); // makes the node visible
                    }
                }
            });
        
            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int leafNum = ( Integer.valueOf(leafNumberField.getText()) ).intValue();
                    leafNum++;
                    if ( leafNum > 0 && leafNum <= leafNodes.size() ) {
                        leafNumberField.setText( String.valueOf(leafNum) );
                        DefaultMutableTreeNode treeNode = leafNodes.get(leafNum - 1);
                        tree.setSelectionPath(new TreePath(treeNode.getPath())); // selects the node
                        tree.scrollPathToVisible(new TreePath(treeNode.getPath())); // makes the node visible
                    }
                
                }
            });
            innerPanel_3.add( prevButton );            
            innerPanel_3.add( nextButton );
            innerPanel_3.add( findButton );
            
            setSize( new Dimension(200,110) );
            
            // Center dialog box
            int x = (screenSize.width - getWidth()) / 2;
            int y = (screenSize.height - getHeight()) / 2;
            setLocation(x, y);
        }
        
        public void visible( boolean in ) {
            setVisible( in );
        }
        
        public void updateNodeCount() {
            String msg = "Leaf Nodes Total:    ";
            msg += String.valueOf(leafNodes.size());
            leafTotalLabel.setText( msg );
        }
    }    
}
