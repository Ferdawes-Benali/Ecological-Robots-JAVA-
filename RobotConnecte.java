
public abstract class RobotConnecte extends Robot implements Connectable {
	public boolean connecte;
	public String reseauConnecte;
	
	public RobotConnecte(String id) {
		super();
		this.id=id;
		this.connecte=false;
		this.reseauConnecte=null; 
	}
	
	@Override
	public void connecter ( String reseau ) throws RobotException{
		this.verifierEnergie(5);
		this.verifierMaintenance();
		this.connecte=true; 
		this.reseauConnecte=reseau;
		this.ajouterHistorique("Robot connecté au réseau "+reseau);
		this.consommerEnergie(5);
	}
	
	@Override
	public void deconnecter () {
		this.connecte=false;
		this.reseauConnecte=null;
		this.ajouterHistorique("Robot déconnecté du réseau");
	}
	
	public void envoyerDonnees ( String donnees ) throws RobotException{
		if (!this.connecte || this.reseauConnecte==null) {
			throw new RobotException("Impossible : Le Robot n'est pas connecté");
		}else {
			this.verifierEnergie(3);
			this.consommerEnergie(3);
			this.ajouterHistorique("Données envoyées sur "+this.reseauConnecte+ " : "+ donnees);
		}
	}
	
	
}
