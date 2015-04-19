package collector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StationUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private StationAgent mStation;
	private Sensor<?>[] sensors;
	private JCheckBox[] boxes;

	public StationUI(StationAgent station){
		super(station.getLocalName());

		mStation = station;

		JPanel panel = new JPanel();		

		panel.add(new JLabel("Capteur(s) disponible(s)"));
		getContentPane().add(panel, BorderLayout.NORTH);

		panel = new JPanel();
		sensors = SensorManager.getSensors();
		panel.setLayout(new GridLayout(0, 1));		

		boxes = new JCheckBox[sensors.length];
		for(int i=0; i<sensors.length; i++){
			boxes[i] = new JCheckBox(sensors[i].getName());
			panel.add(boxes[i]);
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
				mStation.doDelete();
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
		for(int i=0; i<boxes.length; i++){
			if(boxes[i].isSelected())
				mStation.addSensor(sensors[i]);
			else
				mStation.removeSensor(sensors[i]);
		}
	}	

}
