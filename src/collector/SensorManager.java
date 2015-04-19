package collector;

public class SensorManager {

	public final static Sensor<?>[] getSensors(){
		Sensor<?>[] sensors = new Sensor[2];
		sensors[0] = new TimeSensor();
		sensors[1] = new TemperatureSensor();
		return sensors;
	}

	public final static Sensor<?> getSensorFromId(int id){
		switch(id){
		case Sensor.TIME_SENSOR:
			return new TimeSensor();
		case Sensor.TEMPERATURE_SENSOR:
			return new TemperatureSensor();
		default:
			return null;
		}
	}
}
