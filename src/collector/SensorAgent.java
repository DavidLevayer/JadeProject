package collector;

import java.util.Map;

import collector.sensor.Sensor;
import collector.sensor.TemperatureSensor;
import collector.sensor.TimeSensor;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SensorAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	Map<Integer,Sensor<?>> mSensors;
	
	/**
	 * Fonction appelée lors de la création de l'agent
	 */
	protected void setup() {
		
		// Créer la liste des capteurs associés
		initiateSensors();
		
		// Enregistrement de la liste des services auprès d'un annuaire
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("sensor-station");
		sd.setName("JADE-sensor-station");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Ajout d'un service/comportement : requête pour demander des informations
		// sur les capteurs présents sur la station
		addBehaviour(new OfferRequestsServer());

		// Ajout d'un service/comportement : récupérer les valeurs des capteurs présents
		addBehaviour(new SensorValueServer());
		
	}
	
	private void initiateSensors(){
		mSensors.put(Sensor.TIME_SENSOR, new TimeSensor());
		mSensors.put(Sensor.TEMPERATURE_SENSOR, new TemperatureSensor());
	}
	
	private class OfferRequestsServer extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			
			// Récupération du message
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null) {			
				
				String content = msg.getContent();
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				
				switch(Integer.valueOf(content)){
				case Sensor.SERVICE_GETSTATIONINFO:
					reply.setContent("Agent station name: "+getName());
					break;
				case Sensor.SERVICE_GETSERVICEINFO:
					String temp = new String();
					for(Sensor<?> s: mSensors.values())
						temp = temp.concat(s.getInformation()+"\n");
					reply.setContent(temp);
					break;
				}
				
				myAgent.send(reply);
			}
			else {
				block();
			}
			
		}
		
	}
	
	private class SensorValueServer extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
