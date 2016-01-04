package warehouse.controller;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public abstract class TableBaseMouseListener implements TableModelListener{
	private static boolean active = true;
	
	public static void setActive(boolean active){
		TableBaseMouseListener.active = active;
	}
	
	protected abstract void doTableChanged(TableModelEvent e);
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if(active){
			doTableChanged(e);
		}
		
	}

}
