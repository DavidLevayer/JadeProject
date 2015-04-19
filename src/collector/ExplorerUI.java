package collector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ExplorerUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private ExplorerAgent mAgent;
	private Sensor<?>[] sensors;
	private JCheckBox[] boxes;
	private JRadioButton[] services;

	public ExplorerUI(ExplorerAgent agent){
		super(agent.getLocalName());

		mAgent = agent;

		JPanel panel = new JPanel();		

		panel.add(new JLabel("Capteur(s) recherché(s)"));
		getContentPane().add(panel, BorderLayout.NORTH);

		panel = new JPanel();
		sensors = SensorManager.getSensors();
		panel.setLayout(new GridLayout(0, 1));		

		boxes = new JCheckBox[sensors.length];
		for(int i=0; i<sensors.length; i++){
			boxes[i] = new JCheckBox(sensors[i].getName());
			panel.add(boxes[i]);
		}
		
		panel.add(new JLabel("Service demandé"));
		ButtonGroup group = new ButtonGroup();
		
		services = new JRadioButton[4];
		services[0] = new JRadioButton("Station info");
		services[1] = new JRadioButton("Services info");
		services[2] = new JRadioButton("Dernières valeurs");
		services[3] = new JRadioButton("Toutes les valeurs");
		
		for(int i=0; i<4; i++){
			group.add(services[i]);
			panel.add(services[i]);
		}

		getContentPane().add(panel, BorderLayout.CENTER);

		panel = new JPanel();
		JButton validButton = new JButton("Modifier");
		validButton.addActionListener(this);

		panel.add(validButton);
		getContentPane().add(panel, BorderLayout.SOUTH);

		// Quand on ferme la fenêtre, la station est supprimée	
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mAgent.doDelete();
			}
		} );
		
		setResizable(false);
	}

	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		List<Integer> sensorIDs = new ArrayList<Integer>();
		for(int i=0; i<boxes.length; i++){
			if(boxes[i].isSelected())
				sensorIDs.add(sensors[i].getType());
		}
		int serviceType = 0;
		for(int i=0; i<services.length; i++){
			if(services[i].isSelected()){
				serviceType = i;
				break;
			}
		}
		
		mAgent.setResearch(serviceType, sensorIDs);
	}	
}
