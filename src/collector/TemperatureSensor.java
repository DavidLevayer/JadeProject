package collector;

import java.util.List;

public class TemperatureSensor implements Sensor<Float> {

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getInformation() {
		return "Temperature Sensor (return int values)";
	}

	@Override
	public List<Float> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Float getLastValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
