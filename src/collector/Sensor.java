package collector;

public interface Sensor<U> {

	public final static int TIME_SENSOR = 0;
	public final static int TEMPERATURE_SENSOR = 1;
	public final static int LIGHT_SENSOR = 2;
	public final static int HUMIDITY_SENSOR = 3;
	
	public final static int SERVICE_GETSTATIONINFO = 0;
	public final static int SERVICE_GETSERVICEINFO = 1;
	public final static int SERVICE_GETLASTVALUE = 2;
	public final static int SERVICE_GETVALUES = 3;
	
	public String getName();
	public int getType();
	public String getInformation();
	public U[] getValues();
	public U getLastValue();
}
