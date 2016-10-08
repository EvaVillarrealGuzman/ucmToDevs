
package SimEnvironment.SAModel.Library;

public class Interval implements Comparable {

	private Double minValue;
	private Double maxValue;
	private Boolean checked;

	public Interval(Double minValue, Double maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.checked = false;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Boolean checked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public int compareTo(Object o) {
		Interval otherInterval = (Interval) o;
		if (this.getMinValue() == otherInterval.getMinValue()) {
			return 0;
		} else if (this.getMinValue() < otherInterval.getMinValue()) {
			return -1;
		} else {
			return 1;
		}
	}

}
