package collector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import collector.Sensor;
import collector.TemperatureSensor;
import collector.TimeSensor;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class StationAgent extends Agent {

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
		
	}
	
	private void initiateSensors(){
		mSensors = new HashMap<Integer, Sensor<?>>();
		mSensors.put(Sensor.TIME_SENSOR, new TimeSensor());
		mSensors.put(Sensor.TEMPERATURE_SENSOR, new TemperatureSensor());
	}
	
	protected void takeDown() {
		// On retire la station de l'annuaire
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	private class OfferRequestsServer extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			
			// Récupération du message
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null) {			
				
				System.out.println("Request: "+msg.getContent());
				String[] content = msg.getContent().split("#");
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				List<String> sensorIDs;
				String temp = new String();
				
				switch(Integer.valueOf(content[0])){
				
				case Sensor.SERVICE_GETSTATIONINFO:
					temp = "Agent station name: "+getName();
					break;
					
				case Sensor.SERVICE_GETSERVICEINFO:
					for(Sensor<?> s: mSensors.values())
						temp = temp.concat(s.getInformation()+"\n");
					break;
					
				case Sensor.SERVICE_GETLASTVALUE:
					// On récupère les capteurs désirés par l'agent
					sensorIDs = Arrays.asList(content[1].split(";"));
					System.out.println(sensorIDs);
					for(Sensor<?> s: mSensors.values()){
						// Si le capteur est "intéressant"
						if(sensorIDs.contains(String.valueOf(s.getType()))){
							// On ajoute la dernière valeur qu'il a enregistré
							temp = temp.concat(s.getName()+":"+s.getLastValue().toString()+"\n");
						}							
					}						
					break;
					
				case Sensor.SERVICE_GETVALUES:
					// On récupère les capteurs désirés par l'agent
					sensorIDs = Arrays.asList(content[1].split(";"));
					for(Sensor<?> s: mSensors.values()){
						// Si le capteur est "intéressant"
						if(sensorIDs.contains(String.valueOf(s.getType()))){
							// On ajoute les dernières valeurs qu'il a enregistré
							Object[] values = s.getValues();
							temp = temp.concat(s.getName()+":");
							for(int i=0; i<values.length; i++)
								if(values[i]!=null)
									temp = temp.concat(values[i].toString()+";");
							temp = temp.concat("\n");
						}							
					}	
					break;
				}
				reply.setContent(temp);
				myAgent.send(reply);
			}
			else {
				block();
			}
			
		}
		
	}

}
