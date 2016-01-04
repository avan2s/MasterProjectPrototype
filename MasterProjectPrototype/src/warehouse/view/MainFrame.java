package warehouse.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import warehouse.controller.TableBaseMouseListener;
import warehouse.controller.TransportOptimization;
import warehouse.model.Customer;
import warehouse.model.Transport;
import warehouse.model.Warehouse;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel model;
	private List<Warehouse> warehouses;
	private List<Customer> customers;
	private List<Transport> possibleTransports;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 764, 180);
		contentPane.add(scrollPane);
		
		table = new JTable();
		model = new DefaultTableModel();
		this.initializeModel();
		table.setModel(model);
		scrollPane.setViewportView(table);
		
		// Table Changed Listener
		model.addTableModelListener(new TableBaseMouseListener(){

			@Override
			protected void doTableChanged(TableModelEvent e) {
				// Bei Updates
				if(e.getType() == TableModelEvent.UPDATE && e.getFirstRow()!= TableModelEvent.HEADER_ROW){
					String newValue = (String)model.getValueAt(e.getFirstRow(), e.getColumn());
					String objectId = (String)model.getValueAt(e.getFirstRow(),0);
					Transport t = getTransportByObjectId(objectId);
					String columnName = model.getColumnName(e.getColumn());
					switch(columnName){
					case "Cost":
						t.setCost(Integer.parseInt(newValue));
						break;
					case "Max Transports":
						TableBaseMouseListener.setActive(false);
						Warehouse w = t.getFrom();
						w.setMaxSupplies(Integer.parseInt(newValue));
						for(int row = 0;row < model.getRowCount();row++){
							String wName = (String)model.getValueAt(row, 1);
							if(wName.contentEquals(w.getName())){
								model.setValueAt(Integer.parseInt(newValue), row, e.getColumn());
							}
						}
						TableBaseMouseListener.setActive(true);
						break;
					case "Demand":
						TableBaseMouseListener.setActive(false);
						Customer c = t.getTo();
						c.setDemand(Integer.parseInt(newValue));
						for(int row = 0;row < model.getRowCount();row++){
							String cName = (String)model.getValueAt(row, 3);
							if(cName.contentEquals(c.getName())){
								model.setValueAt(Integer.parseInt(newValue), row, e.getColumn());
							}
						}
						TableBaseMouseListener.setActive(true);
						break;
					default:
						throw new IllegalArgumentException("Changes in this column are not implemented!");
					}
				}				
			}
			
		});
		/*model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				// Bei Updates
				if(e.getType() == TableModelEvent.UPDATE && e.getFirstRow()!= TableModelEvent.HEADER_ROW){
					String newValue = (String)model.getValueAt(e.getFirstRow(), e.getColumn());
					String objectId = (String)model.getValueAt(e.getFirstRow(),0);
					Transport t = getTransportByObjectId(objectId);
					String columnName = model.getColumnName(e.getColumn());
					switch(columnName){
					case "Cost":
						t.setCost(Integer.parseInt(newValue));
						break;
					case "Max Transports":
						t.getFrom().setMaxSupplies(Integer.parseInt(newValue));
						break;
					case "Demand":
						t.getTo().setDemand(Integer.parseInt(newValue));
						break;
					default:
						throw new IllegalArgumentException("Changes in this column are not implemented!");
					}
				}
			}
		});*/
		
		// BUTTON: Add Transport
		JButton btnAddTransport = new JButton("Add Transport");
		btnAddTransport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String wName = JOptionPane.showInputDialog("Enter Warehouse Name:");
				boolean transportExist = true;
				
				// Gibt es das Warehouse schon?
				Warehouse warehouse = null;
				for(Warehouse w: warehouses){
					if(w.getName().contentEquals(wName)){
						warehouse = w;
						break;
					}
				}
				// Wenn es ein neues Warehouse ist...
				if(warehouse==null){
					String sMaxTransports = JOptionPane.showInputDialog("Enter Max Transports");
					warehouse = new Warehouse(wName, Integer.parseInt(sMaxTransports));
					warehouses.add(warehouse);
					transportExist = false;
				}
				
				String cName = JOptionPane.showInputDialog("Enter Customer Name:");
				Customer customer = null;
				for(Customer c: customers){
					if(c.getName().contentEquals(cName)){
						customer = c;
						break;
					}
				}
				
				// Wenn es ein neuer Kunde ist
				if(customer==null){
					String sDemand = JOptionPane.showInputDialog("Enter demand:");
					customer = new Customer(cName, Integer.parseInt(sDemand));
					customers.add(customer); 
					transportExist = false;
				}
				
				// Wenn Transport bereits existiert, Transport aktualisieren
				if(transportExist){
					JOptionPane.showMessageDialog(null, "Transport already exist!");
				}
				// wenn Transport ein neuer Transport ist (Warehouse -> Kunde)-Strecke gibt es schon
				else{
					String sCost = JOptionPane.showInputDialog("Enter cost:");
					int cost = Integer.parseInt(sCost);
					Transport t = new Transport(warehouse, customer, cost);
					addTransportToTable(t);
				}
				
			}
		});
		btnAddTransport.setBounds(10, 201, 137, 23);
		contentPane.add(btnAddTransport);
		
		// BUTTON: Delete Transport
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int userSelection = JOptionPane.showConfirmDialog(null,"Delete?", "Delete?", JOptionPane.YES_NO_OPTION);
				if(userSelection == JOptionPane.YES_OPTION){
					String objectString = (String)model.getValueAt(table.getSelectedRow(),0);
					removeTransportByObjectId(objectString);
					model.removeRow(table.getSelectedRow());
				}
				
			}
		});
		btnDelete.setBounds(161, 202, 89, 23);
		contentPane.add(btnDelete);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 235, 764, 118);
		contentPane.add(scrollPane_2);
		
		JTextArea textAreaOptimizationModel = new JTextArea();
		scrollPane_2.setViewportView(textAreaOptimizationModel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 364, 764, 175);
		contentPane.add(scrollPane_1);
		
		JTextArea textAreaSolution = new JTextArea();
		scrollPane_1.setViewportView(textAreaSolution);
		
		// BUTTON: SOLVE
		JButton btnSolve = new JButton("Solve");
		btnSolve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransportOptimization optimizer = new TransportOptimization(possibleTransports);
				optimizer.solve();
				textAreaOptimizationModel.setText(optimizer.getProblem());
				textAreaSolution.setText(optimizer.getSolution());
			}
		});
		btnSolve.setBounds(341, 202, 89, 23);
		contentPane.add(btnSolve);
		

	}
	
	
	private void initializeModel(){
		warehouses = new ArrayList<>();
		customers = new ArrayList<>();
		possibleTransports = new ArrayList<Transport>();
		
		Warehouse w1 = new Warehouse("W1",70);
		Warehouse w2 = new Warehouse("W2",50);
		Warehouse w3 = new Warehouse("W3",50);
		Warehouse w4 = new Warehouse("W4",50);
		warehouses.add(w1);
		warehouses.add(w2);
		warehouses.add(w3);
		warehouses.add(w4);
		
		Customer c1 = new Customer("C1",30);
		Customer c2 = new Customer("C2",30);
		Customer c3 = new Customer("C3",30);
		Customer c4 = new Customer("C4",30);
		Customer c5 = new Customer("C5",30);
		Customer c6 = new Customer("C6",30);
		customers.add(c1);
		customers.add(c2);
		customers.add(c3);
		customers.add(c4);
		customers.add(c5);
		customers.add(c6);
		
		int costs[][] = {{15,35,25,20,30,40},{10,50,35,30,25,45},{20,55,40,20,25,35},{25,40,30,35,20,25}};
		System.out.println(costs[3][5]);
		int w_index = 0;
		int c_index = 0;
		
		for(Warehouse w : warehouses){
			c_index = 0;
			for(Customer c: customers){
				Transport t = new Transport(w, c, costs[w_index][c_index]);
				possibleTransports.add(t);
				c_index++;
			}
			w_index++;
		}
		model.addColumn("Object");
		model.addColumn("Warehouse");
		model.addColumn("Max Transports");
		model.addColumn("Customer");
		model.addColumn("Demand");
		model.addColumn("Cost");
		for(Transport t : possibleTransports){
			this.addTransportToTable(t);
		}
		
	}
	
	private Transport getTransportByObjectId(String id){
		for(Transport t : possibleTransports){
			if(t.toString().contentEquals(id))
				return t;
		}
		return null;
	}
	
	private void addTransportToTable(Transport t){
		Warehouse w = t.getFrom();
		Customer c = t.getTo();
		List<Object> row = new ArrayList<>();
		row.add(t.toString());
		row.add(w.getName());
		row.add(w.getMaxSupplies());
		row.add(c.getName());
		row.add(c.getDemand());
		row.add(t.getCost());
		model.addRow(row.toArray());
		if(!possibleTransports.contains(t))
			this.possibleTransports.add(t);
		if(!warehouses.contains(w))
			this.warehouses.add(w);
		if(!customers.contains(c))
			this.customers.add(c);
	}
	
	private void removeTransportByObjectId(String objectId){
		// Transport aus der Liste löschen
		Customer cOfDeletedTransport = null;
		Warehouse wOfDeletedTransport = null;
		boolean deleteCustomer = true;
		boolean deleteWarehouse = true;
		for(Transport t : possibleTransports){
			if(t.toString().contentEquals(objectId)){
				wOfDeletedTransport = t.getFrom();
				wOfDeletedTransport.removeTransport(t);
				cOfDeletedTransport = t.getTo();
				cOfDeletedTransport.removeTransport(t);
				possibleTransports.remove(t);
				break;
			}	
		}
		// Prüfen ob Kunde in einem anderen Transport
		for(Transport t : possibleTransports){
			Customer c = t.getTo();
			if(c.equals(cOfDeletedTransport)){
				deleteCustomer = false;
				break;
			}
		}
		// Prüfen ob Warehouse in einem anderen Transport
		for(Transport t : possibleTransports){
			Warehouse w = t.getFrom();
			if(w.equals(wOfDeletedTransport)){
				deleteWarehouse = false;
				break;
			}
		}
		
		// Warehouse löschen, falls Warehouse in keinem anderen Transport
		if(deleteWarehouse)
			this.warehouses.remove(wOfDeletedTransport);
		// Kunde löschen, falls Kunde in keinem anderen Transport
		if(deleteCustomer)
			this.customers.remove(cOfDeletedTransport);
	}
}
