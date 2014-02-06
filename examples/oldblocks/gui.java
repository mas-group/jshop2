import JSHOP2.*;
import java.util.*;

public class gui{
	public static void main(String[] args) {
		JSHOP2 jshop2 = new JSHOP2();
		JSHOP2GUI jshop2Gui = new JSHOP2GUI();
		
		jshop2.registerPlanObserver(jshop2Gui);
		
		problem.getPlans(jshop2);
		
		jshop2Gui.run();
	} 
}
