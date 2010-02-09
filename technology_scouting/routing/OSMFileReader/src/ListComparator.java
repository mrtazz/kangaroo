import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;


public class ListComparator<T> {
	
	
	public abstract class StepListener {		
		public abstract void step(boolean step1, boolean step2);		
	}
	

	private List<T> list1;

	
	private List<T> list2;
	
	
	private int index1 = 0;
	
	
	private int index2 = 0;
	
	
	private StepListener stepListener;
	
	
	public void setStepListener(StepListener stepListener) {
		this.stepListener = stepListener;
	}
	
	
	public int getIndex1() {
		return index1;
	}
	
	
	public int getIndex2() {
		return index2;
	}
	
	
	public ListComparator(List<T> list1, List<T> list2) {
		super();
		this.list1 = list1;
		this.list2 = list2;
	}

	
	private void doStep(boolean step1, boolean step2) {
		if (stepListener != null) 
			stepListener.step(step1, step2);
		if (step1) index1++;
		if (step2) index2++;
	}
	
	
	public boolean step() {
		
		if (hasNext()) {			
			if (list1.get(index1).equals(list2.get(index2))) {
				doStep(true, true);
			} else {
				boolean found = false;				
				for (int i = index2; i < list2.size() && !found; i++) {
					if (list1.get(index1).equals(list2.get(i))) {
						doStep(false, true);
						found = true;
					}
				}				
				for (int i = index1; i < list1.size() && !found; i++) {
					if (list2.get(index2).equals(list1.get(i))) {
						doStep(true, false);
						found = true;
					}
				}				
				if (!found) {
					doStep(true, false);
				}
			}			
		}		
		
		return hasNext();
	}
	
	
	public boolean hasNext() {
		return (index1 < list1.size() || index2 < list2.size());
	}
	
}
