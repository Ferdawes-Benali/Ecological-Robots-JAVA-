import java.util.*;


public class RobotEnvironnemental extends RobotConnecte implements Ecological {

	private Map<String, List<Double>> historiqueDonnees;
    private double[] limites; // [tempMin, tempMax, humidMin, humidMax, qualiteMin]
    private static final int ENERGIE_MESURE = 2;
    private String energySource;
    private double carbonFootprint;
    private int wasteCapacity;
    private int currentWasteLevel;
    private Map<String, Double> environmentalReadings;
    
    public RobotEnvironnemental(String id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
        this.energySource = "Standard";
        this.carbonFootprint = 10.0; // Default starting value
        this.wasteCapacity = 100;
        this.currentWasteLevel = 0;
        this.historiqueDonnees = new HashMap<>();
        this.historiqueDonnees.put("temperature", new ArrayList<>());
        this.historiqueDonnees.put("humidity", new ArrayList<>());
        this.historiqueDonnees.put("airQuality", new ArrayList<>());
        this.limites = new double[]{15.0, 30.0, 30.0, 70.0, 50.0};
        this.environmentalReadings = new HashMap<>();
        this.ajouterHistorique("Robot environnemental créé");
    }
    
    @Override
    public double calculateCarbonFootprint() {
        // Calculate based on usage, energy source, etc.
        double usage = this.heuresUtilusation * 0.5;
        if (this.energySource.equals("Solaire")) {
            usage *= 0.2;
        } else if (this.energySource.equals("Eolien")) {
            usage *= 0.3;
        }
        
        this.carbonFootprint = usage;
        this.ajouterHistorique("Empreinte carbone calculée: " + this.carbonFootprint);
        return this.carbonFootprint;
    }
    
    @Override
    public void switchToRenewableEnergy(String energyType) throws EnergySourceException {
        if (energyType.equals("Solaire") || energyType.equals("Eolien") || energyType.equals("Hydraulique")) {
            this.energySource = energyType;
            this.ajouterHistorique("Source d'énergie changée à " + energyType);
        } else {
            throw new EnergySourceException("Source d'énergie non reconnue: " + energyType);
        }
    }
    
    @Override
    public Map<String, Double> monitorEnvironment() {
        // Simulate environmental readings
        this.environmentalReadings.put("temperature", 22.5 + (Math.random() * 5));
        this.environmentalReadings.put("humidity", 45.0 + (Math.random() * 20));
        this.environmentalReadings.put("airQuality", 85.0 + (Math.random() * 15));
        this.ajouterHistorique("Environnement surveillé");
        return this.environmentalReadings;
    }
    
    @Override
    public void processWaste(int wasteAmount) throws WasteCapacityException {
        if (this.currentWasteLevel + wasteAmount > this.wasteCapacity) {
            throw new WasteCapacityException("Capacité de traitement des déchets dépassée");
        }
        this.currentWasteLevel += wasteAmount;
        this.ajouterHistorique("Déchets traités: " + wasteAmount + " unités");
    }
    
    // Utiliser la méthode envoyerDonnees de RobotConnecte
    public void envoyerDonneeEnvironnementales() throws RobotException {
        if (!this.connecte || this.reseauConnecte == null) {
            throw new RobotException("Impossible : Le Robot n'est pas connecté");
        } else {
            Map<String, Double> readings = this.monitorEnvironment();
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, Double> entry : readings.entrySet()) {
                data.append(entry.getKey()).append("=").append(String.format("%.2f", entry.getValue())).append(", ");
            }
            super.envoyerDonnees(data.toString());
        }
    }
    
    public void prendreRelevé() throws RobotException {
        this.verifierEnergie(ENERGIE_MESURE);
        Map<String, Double> readings = this.monitorEnvironment();
        
        // Store readings in history
        for (Map.Entry<String, Double> entry : readings.entrySet()) {
            this.historiqueDonnees.get(entry.getKey()).add(entry.getValue());
        }
        
        this.consommerEnergie(ENERGIE_MESURE);
        
        // Check if any values are outside safe limits
        if (readings.get("temperature") < this.limites[0] || readings.get("temperature") > this.limites[1]) {
            this.ajouterHistorique("ALERTE: Température hors limites: " + readings.get("temperature"));
            if (this.connecte) {
                try {
                    this.envoyerDonnees("ALERTE TEMPÉRATURE: " + readings.get("temperature"));
                } catch (RobotException e) {
                    // Already checked connection
                }
            }
        }
        
        this.ajouterHistorique("Relevé effectué: T=" + readings.get("temperature") + 
                "°C, H=" + readings.get("humidity") + "%, Q=" + readings.get("airQuality"));
    }
    
    @Override
    public void deplacer(int x, int y) throws RobotException {
        // Environmental robots move efficiently to reduce carbon footprint
        if (x == this.x && y == this.y) {
            return;
        }
        
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        if (distance > 100) {
            throw new RobotException("Distance très grande:" + distance + "> limite=100");
        }
        
        try {
            verifierMaintenance();
            int energieRequise = (int) Math.ceil(0.2 * distance); // Very efficient movement
            
            verifierEnergie(energieRequise);
            consommerEnergie(energieRequise);
            
            int heuresAjoutés = (int) (distance / 15); // Very efficient usage
            this.heuresUtilusation += heuresAjoutés;
            
            String action = "Déplacement de (" + this.x + "," + this.y + ") vers (" + x + "," + y + ") - Distance : " +
                    String.format("%.2f", distance) + " unités, Énergie consommée : " + energieRequise + "%";
            ajouterHistorique(action);
            
            this.x = x;
            this.y = y;
            
            // Take a reading at the new location
            prendreRelevé();
            
        } catch (EnergieInsuffisanteException | MaintenanceRequiseException e) {
            throw new RobotException("Impossible de déplacer le robot : " + e.getMessage());
        }
    }
    
    @Override
    public void effectuerTache() throws RobotException {
        if (!this.enMarche) {
            throw new RobotException("Le Robot doit être démarré pour effectuer une tâche.");
        }
        
        prendreRelevé();
        
        if (this.connecte) {
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, Double> entry : this.environmentalReadings.entrySet()) {
                data.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            }
            this.envoyerDonnees(data.toString());
        }
    }
    
    public String getEnvironmentalReport() {
        StringBuilder report = new StringBuilder("Rapport environnemental:\n");
        
        for (String metric : this.historiqueDonnees.keySet()) {
            List<Double> values = this.historiqueDonnees.get(metric);
            if (!values.isEmpty()) {
                double avg = values.stream().mapToDouble(a -> a).average().orElse(0.0);
                double min = values.stream().mapToDouble(a -> a).min().orElse(0.0);
                double max = values.stream().mapToDouble(a -> a).max().orElse(0.0);
                
                report.append(metric).append(": Moyenne=").append(String.format("%.2f", avg))
                      .append(", Min=").append(String.format("%.2f", min))
                      .append(", Max=").append(String.format("%.2f", max)).append("\n");
            }
        }
        
        return report.toString();
    }
    
    @Override
    public String toString() {
        String connecteStr = this.connecte ? "Oui (" + this.reseauConnecte + ")" : "Non";
        return "RobotEnvironnemental " + super.toString() + " [Connecté: " + connecteStr + 
               ", Mesures: " + this.historiqueDonnees.size() + "]";
    }
}
