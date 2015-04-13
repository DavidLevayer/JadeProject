package collector;

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

		addBehaviour(new TickerBehaviour(this, 5000) {

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
		private String result;
		private int replieNumber;

		private final static int ASK_INFO = 0;
		private final static int COLLECT_ANSWERS = 1;
		private final static int SHOW_INFO = 2;
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
				cfp.setContent(String.valueOf(Sensor.SERVICE_GETSTATIONINFO));
				// L'identifiant permet de savoir de quelle conversation il s'agit
				cfp.setConversationId("station-info");
				cfp.setReplyWith("cfp"+System.currentTimeMillis());
				myAgent.send(cfp);

				// Préparation du template pour les réponses des stations
				replyTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("station-info"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

				state = SHOW_INFO;

				break;

			case COLLECT_ANSWERS:
				// On regarde si une réponse est disponible
				ACLMessage reply = myAgent.receive(replyTemplate);
				if (reply != null) {
					// Traitement de la réponse "demande d'informations"
					if (reply.getPerformative() == ACLMessage.INFORM) {
						result = reply.getContent();
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
				System.out.println("Information:" +result);
				myAgent.doDelete();
				break;
			}			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}

	}

}
