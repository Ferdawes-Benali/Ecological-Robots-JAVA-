
import java.util.*; 
public class RobotSolaire extends RobotEcologique {
    public  boolean panneauDeplié;
    public  final int efficacitéPanneau; // percentage
    public  int durééChargeSolaire; // hours
    
    public RobotSolaire(String id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
        this.panneauDeplié = false;
        this.efficacitéPanneau = 80;
        this.durééChargeSolaire = 0;
        this.energySource = "Solaire";
    }
    
    public void déplierPanneau() throws RobotException {
    if (!this.panneauDeplié) {
        this.panneauDeplié = true;
        this.ajouterHistorique("Panneau solaire déplié");
    } else {
        throw new RobotException("Le panneau est déjà déplié");
    }
}
    
    public void replierPanneau() throws RobotException {
    if (this.panneauDeplié) {
        this.panneauDeplié = false;
        this.ajouterHistorique("Panneau solaire replié");
    } else {
        throw new RobotException("Le panneau est déjà replié");
    }
}
    
    public int chargerViaSolaire(int heures) {
    if (!this.panneauDeplié) {
        this.ajouterHistorique("Impossible de charger: panneau non déplié");
        return 0;
    }
    
    int energieProduite = (int)(heures * (this.efficacitéPanneau / 10.0));
    this.recharger(energieProduite);
    this.durééChargeSolaire += heures;
    this.ajouterHistorique("Chargé via panneau solaire: +" + energieProduite + "% d'énergie");
    
    return energieProduite; // Return the energy produced   
    }
    @Override
    public double calculateCarbonFootprint() {
        // Solar robots have even lower carbon footprint
        double footprint = super.calculateCarbonFootprint();
        // Reduce footprint based on solar charging
        footprint -= this.durééChargeSolaire * 0.1;
        if (footprint < 0) footprint = 0;
        
        this.carbonFootprint = footprint;
        return footprint;
    }
    
    @Override
    public void deplacer(int x, int y) throws RobotException {
        // Must fold panels before moving
        if (this.panneauDeplié) {
            replierPanneau();
        }
        
        super.deplacer(x, y);
    }
    
    @Override
    public void effectuerTache() throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le Robot doit être démarré pour effectuer une tâche.");
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Déplier panneau");
        System.out.println("2. Replier panneau");
        System.out.println("3. Charger via panneau solaire");
        System.out.print("Choisissez une action: ");
        int choix = scanner.nextInt();
        
        switch (choix) {
            case 1 -> déplierPanneau();
            case 2 -> replierPanneau();
            case 3 -> {
                System.out.print("Combien d'heures de charge? ");
                int heures = scanner.nextInt();
                chargerViaSolaire(heures);
            }
            default -> ajouterHistorique("Action non reconnue");
        }
    }
    
    @Override
    public String toString() {
        return "RobotSolaire " + super.toString() + 
               " [Panneau déplié: " + (this.panneauDeplié ? "Oui" : "Non") + 
               ", Efficacité: " + this.efficacitéPanneau + "%]";
    }
}
