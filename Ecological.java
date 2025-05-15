import java.util.Map;

public interface Ecological {
	    double calculateCarbonFootprint();
	    
	    void switchToRenewableEnergy(String energyType) throws EnergySourceException;
	    
	    Map<String, Double> monitorEnvironment();
	    
	    void processWaste(int wasteAmount) throws WasteCapacityException;
	
}
