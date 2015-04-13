package collector.sensor;


import java.util.ArrayList;
import java.util.List;

public class TimeSensor implements Sensor<Long> {

	@Override
	public int getType() {
		return Sensor.TIME_SENSOR;
	}

	@Override
	public String getInformation() {
		return "Return the time when someone needs it!";
	}

	@Override
	public List<Long> getValues() {
		List<Long> time = new ArrayList<Long>();
		time.add(System.currentTimeMillis());
		return time;
	}

	@Override
	public Long getLastValue() {
		return System.currentTimeMillis();
	}

}
