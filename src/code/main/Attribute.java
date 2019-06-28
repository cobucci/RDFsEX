package code.main;

public class Attribute {

	private String name;
	private String value;
	private boolean isMultivalued;
	private boolean isOptional = false;
	private String parent = "";
        private double min;
        private int max;
        
        public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public boolean isMultivalued() {
		return isMultivalued;
	}

	public void setMultivalued(boolean isMultivalued) {
		this.isMultivalued = isMultivalued;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object objeto) {
		if (objeto instanceof Attribute) {
			Attribute atribute = (Attribute) objeto;
			return atribute.getName().equals( this.name );
		}
		else
			return false;
	}

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }

}
