package collector;

import java.util.ArrayList;
import java.util.List;

import collector.Sensor;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ExplorerAgent extends Agent {

	private static final long serialVersionUID = 1L;

	// La liste des stations accessibles
	private AID[] stationAgents;

	/**
	 * Fonction appelée lors de la création de l'agent
	 */
	protected void setup() {

		addBehaviour(new TickerBehaviour(this, 2000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				// On cherche les stations disponibles
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("sensor-station");
				dfd.addServices(sd);

				try{
					DFAgentDescription[] results = DFService.search(myAgent, dfd);
					stationAgents = new AID[results.length];
					System.out.println("Stations found:");
					for(int i=0; i<results.length;i++){
						stationAgents[i] = results[i].getName();
						System.out.println(stationAgents[i].getName());
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

				// On demande effectue une requête pour demander des informations
				myAgent.addBehaviour(new InformationRequest());
			}
		});
	}

	private class InformationRequest extends Behaviour{

		private static final long serialVersionUID = 1L;

		private MessageTemplate replyTemplate;
		private String result = "";
		private int replieNumber;

		private final static int ASK_INFO = 0;
		private final static int COLLECT_ANSWERS = 1;
		private final static int SHOW_INFO = 2;
		private final static int DONE = 3;
		private int state = ASK_INFO;

		@Override
		public void action() {
			switch(state){
			
			case ASK_INFO:				
				replieNumber = 0;
				// On prend contact avec toutes les stations
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < stationAgents.length; ++i) {
					cfp.addReceiver(stationAgents[i]);
				}
				
				// Création de la requête
				List<Integer> sensorIDs = new ArrayList<Integer>();
				sensorIDs.add(Sensor.TIME_SENSOR);
				sensorIDs.add(Sensor.TEMPERATURE_SENSOR);
				cfp.setContent(buildRequest(Sensor.SERVICE_GETVALUES, sensorIDs));
				// L'identifiant permet de savoir de quelle conversation il s'agit
				cfp.setConversationId("station-info");
				cfp.setReplyWith("cfp"+System.currentTimeMillis());
				myAgent.send(cfp);

				// Préparation du template pour les réponses des stations
				replyTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("station-info"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

				state = COLLECT_ANSWERS;
				
				break;

			case COLLECT_ANSWERS:
				// On regarde si une réponse est disponible
				ACLMessage reply = myAgent.receive(replyTemplate);
				if (reply != null) {
					// Traitement de la réponse "demande d'informations"
					if (reply.getPerformative() == ACLMessage.INFORM) {
						result = result.concat(reply.getContent()+"\n");
					}
					replieNumber++;
					if (replieNumber >= stationAgents.length) {
						state = SHOW_INFO; 
					}
				}
				else {
					block();
				}
				break;

			case SHOW_INFO:
				System.out.println("Information:\n" +result);
				myAgent.doDelete();
				state = DONE;
				break;
			}			
		}

		@Override
		public boolean done() {
			return (state==DONE);
		}

	}

	private String buildRequest(int valueOrValues, List<Integer> sensorIDs){
		String res = new String();
		res = String.valueOf(valueOrValues) + "#";
		for(Integer i: sensorIDs)
			res = res.concat(String.valueOf(i)+";");
		res = res.substring(0, res.length()-1);
		return res;
	}
}
