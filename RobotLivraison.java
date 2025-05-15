import java.util.Scanner;


public class RobotLivraison extends RobotConnecte {

	public int colisActuel; //erreur fel enoncé 
	public String destination;
	public boolean enlivraison;
	public static final int ENERGIE_LIVRAISON=15;
	public static final int ENERGIE_CHARGEMENT=5;
	

	public RobotLivraison(String id, int x,int y) {
		super(id);
		this.x=x;
		this.y=y;
		this.colisActuel=0;  
		this.destination=null;
		this.enlivraison=false;
	}
	
	

	
	@Override
	public void effectuerTache() throws RobotException {
		if (! this.enMarche) {
			throw new RobotException("Le Robot doit être démarré pour effectuer une tâche. ");
			
		} Scanner scanner = new Scanner(System.in);
		if (this.enlivraison) {
			System.out.print("Entrer la destination X : ");
            int destX = scanner.nextInt();
            System.out.print("Entrer la destination Y : ");
            int destY = scanner.nextInt();
            FaireLivraison(destX, destY);
		}
		else {
			Scanner scanner1 = new Scanner(System.in);
            System.out.print("Souhaitez-vous charger un nouveau colis ? (oui/non) : ");
            String reponse = scanner1.next().trim().toLowerCase(); 
            
            if (reponse.equals("oui")) {
                verifierEnergie(5);
                System.out.println("Entrez la destination:");
                String destination = scanner.nextLine();
                this.chargerColis(destination);
            } else {
                ajouterHistorique("En attente de colis.");
            }
        }
	}
	
	
	
	public void FaireLivraison (int Destx, int DestY) throws RobotException {
		this.deplacer(Destx, DestY);
		this.enlivraison=false;
		this.destination=null;
		this.colisActuel=0;
		this.ajouterHistorique("Livraison terminée à "+ destination);
		
	}

	@Override
	public void deplacer(int x, int y) throws RobotException {
		if (x == this.x && y == this.y) {
	        return; 
	    }
		
		double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
		if (distance>100) {
			throw new RobotException("Distance très grande:"+distance+"> limite=100");
		}
		try {
	        verifierMaintenance();
	        int energieRequise = (int) Math.ceil(0.3 * distance);
	        
	        verifierEnergie(energieRequise);
	        consommerEnergie(energieRequise);

	        int heuresAjoutés = (int) (distance / 10);
	        this.heuresUtilusation+= heuresAjoutés;

	        String action = "Déplacement de (" + this.x + "," + this.y + ") vers (" + x + "," + y + ") - Distance : " +
	                        String.format("%.2f", distance) + " unités, Énergie consommée : " + energieRequise + "%";
	        ajouterHistorique(action);

	        this.x = x;
	        this.y = y;
	        //this.ajouterHistorique("Déplacement vers"+ x +","+ y);
	    } catch (EnergieInsuffisanteException | MaintenanceRequiseException e) {
	        throw new RobotException("Impossible de déplacer le robot : " + e.getMessage());
	    }
	}
	
	public void chargerColis(String destination) throws EnergieInsuffisanteException {
		if (!enlivraison && colisActuel==0) {
			this.verifierEnergie(ENERGIE_CHARGEMENT);
			this.colisActuel=1;
			this.destination=destination;
			this.consommerEnergie(ENERGIE_CHARGEMENT);
			this.ajouterHistorique("Chargement du colis vers"+destination);		
		}
	}
	
	@Override
	public String toString () {
		String a;
		if(this.connecte) {
			 a= "Oui";
		}else {a="Non";}
		
		return  "RobotLivraison [ID : " + id + ", Position : (" + this.x + "," + this.y + "), Énergie : " +
                this.energie + "%, Heures : " + this.heuresUtilusation + ", Colis"+this.colisActuel+" , Destination: "+this.destination+", Connecté:"+a+"]";
	    
	}
}
