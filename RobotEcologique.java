
import java.util.*;

public abstract class RobotEcologique extends Robot implements Ecological {

	protected String energySource;
    protected double carbonFootprint;
    protected int wasteCapacity;
    protected int currentWasteLevel;
    protected Map<String, Double> environmentalReadings;
    
    public RobotEcologique(String id) {
        super();
        this.id = id;
        this.energySource = "Standard";
        this.carbonFootprint = 10.0; // Default starting value
        this.wasteCapacity = 100;
        this.currentWasteLevel = 0;
        this.environmentalReadings = new HashMap<>();
        this.ajouterHistorique("Robot écologique créé");
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
    
    public void recyclerDechets() {
        int recycled = this.currentWasteLevel;
        this.currentWasteLevel = 0;
        this.ajouterHistorique("Recyclage de " + recycled + " unités de déchets");
    }
    
    // Override consommerEnergie to be more efficient
    @Override
    public void consommerEnergie(int quantite) {
        // Ecological robots consume less energy
        int actualConsumption = (int)(quantite * 0.8);
        super.consommerEnergie(actualConsumption);
    }
    
    @Override
    public String toString() {
        return super.toString() + " [Source d'énergie: " + this.energySource + 
               ", Empreinte carbone: " + this.carbonFootprint +
               ", Niveau de déchets: " + this.currentWasteLevel + "/" + this.wasteCapacity + "]";
    }
    @Override
    public void deplacer(int x, int y) throws RobotException {
        // Provide a concrete implementation for deplacer
        this.x = x;
        this.y = y;
        this.ajouterHistorique("Déplacement vers (" + x + ", " + y + ")");
    }

}
