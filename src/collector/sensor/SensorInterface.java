package collector.sensor;

import java.util.List;

public interface SensorInterface<U> {


	public final static int TIME_SENSOR = 0;
	public final static int TEMPERATURE_SENSOR = 1;
	public final static int LIGHT_SENSOR = 2;
	public final static int HUMIDITY_SENSOR = 3;
	
	public int getType();
	public String getInformation();
	public List<U> getValues();
	public U getLastValue();
}
