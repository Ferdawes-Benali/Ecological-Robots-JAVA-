
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public abstract class Robot {
	public String id;
	public int x,y;
	public int energie;
	public int heuresUtilusation;
	public boolean enMarche;
	public List<String>  historiqueActions;
	
	
	public Robot() {
		this.id="";
		this.x=this.y=0;
		this.energie=0;
		this.heuresUtilusation=0;
		this.enMarche=false;
		this.historiqueActions =new ArrayList <> () ;
		ajouterHistorique("Robot créé");
	}
	
	public void ajouterHistorique(String action) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm:ss");
        String dateHeure = LocalDateTime.now().format(formatter);
        String entree = dateHeure + " " + action;
        this.historiqueActions.add(entree);
        System.out.println("Historique ajouté : " + entree);
	}
	
	
	public void verifierEnergie(int energieRequise) throws EnergieInsuffisanteException {
        if (this.energie < energieRequise) {
            throw new EnergieInsuffisanteException("Énergie insuffisante : requise = " + energieRequise + ", disponible = " + this.energie);
        } else {
            System.out.println("Énergie suffisante pour l'action.");
        }
    }
	
	
	 public void verifierMaintenance() throws MaintenanceRequiseException {
	        if (this.heuresUtilusation >= 100) {
	            throw new MaintenanceRequiseException("Maintenance requise : le robot a été utilisé pendant " + this.heuresUtilusation + " heures.");
	        } else {
	            System.out.println("Aucune maintenance nécessaire. Heures d'utilisation : " + this.heuresUtilusation);
	        }
	    }
	 
	 
	 public void démarrer() throws RobotException {
		 if (this.energie<=10) {
			 throw new RobotException("Manque d'énergie");
		 }else {
			 this.enMarche=true;
			 this.ajouterHistorique("Démarrage du Robot");
		 }
	 }
	 
	 public void arreter() {
		 this.enMarche=false;
		 this.ajouterHistorique("Arret du Robot");
	 }
	 
	 public void consommerEnergie(int quantite) {
		 if ((this.energie - quantite)>=0) {
			 this.energie-=quantite;
		 }else {
			 this.energie=0;
		 }
	 }
	 public void recharger (int quantite) {
		 if ((this.energie + quantite)<=100) {
			 this.energie+=quantite;
		 }else {
			 this.energie=100;
		 }
	 }
	 
	 public abstract void deplacer(int x,int y) throws RobotException;
	 public abstract void effectuerTache() throws RobotException;
	 
	 public String getHistorique() {
		 return String.join("\n", this.historiqueActions);
	 }
	 
	 @Override
	 public String toString() {
		 return getClass().getSimpleName() + " [ID : " + id + ", Position : (" + this.x + "," + this.y + "), Énergie : " +
	                this.energie + "%, Heures : " + this.heuresUtilusation + "]";
	    
	 }
	 
	 
}
