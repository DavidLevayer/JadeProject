package collector;

import java.util.Timer;
import java.util.TimerTask;

public class TimeSensor implements Sensor<Long> {

	private final static int valueSize = 10;
	
	private Long[] values;
	private int pos;
	
	public TimeSensor(){
		
		Timer timer = new Timer();
		TimerTask minuteTask = new TimerTask() {
			
			@Override
			public void run() {
				values[pos] = System.currentTimeMillis();
				pos = (pos + 1) % valueSize;
			}
		};
		
		timer.schedule(minuteTask, 60000);
	}
	
	@Override
	public int getType() {
		return Sensor.TIME_SENSOR;
	}

	@Override
	public String getInformation() {
		return "Return the time when someone needs it!";
	}

	@Override
	public Long[] getValues() {
		return values;
	}

	@Override
	public Long getLastValue() {
		return values[(pos-1)%valueSize];
	}

}
