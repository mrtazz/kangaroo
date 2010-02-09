package com.mobiletsm.osm;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;


/**
 * @author andreaswalz
 *
 */
public class CombinedSelector implements Selector {

	public static int FUNCTION_AND = 1;
	
	public static int FUNCTION_OR = 2;
	
	private Selector selector1;
	
	private Selector selector2;
	
	private int function;
	
	
	public CombinedSelector(Selector selector1, Selector selector2, int function) {
		super();
		this.selector1 = selector1;
		this.selector2 = selector2;
		this.function = function;
	}
	
	
	private boolean link(boolean a, boolean b) {
		if (function == FUNCTION_AND)
			return a && b;
		else if (function == FUNCTION_OR)
			return a || b;
		else
			throw new RuntimeException("CombinedSelector: unknown function");
	}
	
	
	public boolean isAllowed(IDataSet arg0, Node arg1) {			
		return link(selector1.isAllowed(arg0, arg1), selector2.isAllowed(arg0, arg1));
	}


	public boolean isAllowed(IDataSet arg0, Way arg1) {
		return link(selector1.isAllowed(arg0, arg1), selector2.isAllowed(arg0, arg1));
	}


	public boolean isAllowed(IDataSet arg0, Relation arg1) {
		return link(selector1.isAllowed(arg0, arg1), selector2.isAllowed(arg0, arg1));
	}

}
