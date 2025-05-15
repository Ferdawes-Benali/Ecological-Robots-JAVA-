import java.util.*;
public class RobotRecycleur extends RobotEcologique {

	private String[] materialsHandled;
    private Map<String, Integer> recycledMaterials;
    private static final int ENERGIE_RECYCLAGE = 8;
    
    public RobotRecycleur(String id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
        this.materialsHandled = new String[]{"plastique", "verre", "papier", "metal"};
        this.recycledMaterials = new HashMap<>();
        for (String material : materialsHandled) {
            recycledMaterials.put(material, 0);
        }
        this.wasteCapacity = 200; // Higher capacity
    }
    
    public void recyclerMateriau(String material, int amount) throws RobotException {
        if (!Arrays.asList(materialsHandled).contains(material)) {
            throw new RobotException("Matériau non pris en charge: " + material);
        }
        
        this.verifierEnergie(ENERGIE_RECYCLAGE);
        try {
            this.processWaste(amount);
            this.recycledMaterials.put(material, this.recycledMaterials.get(material) + amount);
            this.consommerEnergie(ENERGIE_RECYCLAGE);
            this.ajouterHistorique("Recyclage de " + amount + " unités de " + material);
        } catch (WasteCapacityException e) {
            throw new RobotException("Impossible de recycler: " + e.getMessage());
        }
    }
    
    @Override
    public void deplacer(int x, int y) throws RobotException {
        // Recycler robots move more efficiently
        if (x == this.x && y == this.y) {
            return;
        }
        
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        if (distance > 100) {
            throw new RobotException("Distance très grande:" + distance + "> limite=100");
        }
        
        try {
            verifierMaintenance();
            int energieRequise = (int) Math.ceil(0.25 * distance); // More efficient movement
            
            verifierEnergie(energieRequise);
            consommerEnergie(energieRequise);
            
            int heuresAjoutés = (int) (distance / 12); // More efficient usage
            this.heuresUtilusation += heuresAjoutés;
            
            String action = "Déplacement de (" + this.x + "," + this.y + ") vers (" + x + "," + y + ") - Distance : " +
                    String.format("%.2f", distance) + " unités, Énergie consommée : " + energieRequise + "%";
            ajouterHistorique(action);
            
            this.x = x;
            this.y = y;
        } catch (EnergieInsuffisanteException | MaintenanceRequiseException e) {
            throw new RobotException("Impossible de déplacer le robot : " + e.getMessage());
        }
    }
    
    @Override
    public void effectuerTache() throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le Robot doit être démarré pour effectuer une tâche.");
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Quel matériau recycler? (plastique, verre, papier, metal):");
        String material = scanner.nextLine().trim().toLowerCase();
        
        System.out.println("Quelle quantité?");
        int amount = scanner.nextInt();
        
        recyclerMateriau(material, amount);
    }
    
    public String getRecyclingStats() {
        StringBuilder stats = new StringBuilder("Statistiques de recyclage:\n");
        for (Map.Entry<String, Integer> entry : recycledMaterials.entrySet()) {
            stats.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" unités\n");
        }
        return stats.toString();
    }
    
    @Override
    public String toString() {
        return "RobotRecycleur " + super.toString() + " [Matériaux traités: " + 
                String.join(", ", materialsHandled) + "]";
    }
}
