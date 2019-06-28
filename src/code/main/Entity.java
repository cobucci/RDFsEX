package code.main;


import java.util.ArrayList;
import java.util.List;

public class Entity {

    protected String name;

    private double avgNumberOfBytes;

    public List<Attribute> attributes = new ArrayList<>();

    public List<Instance> instances = new ArrayList<>();

    public double getAvgNumberOfBytes() {
        return avgNumberOfBytes;
    }

    public void setAvgNumberOfBytes(double avgNumberOfBytes) {
        this.avgNumberOfBytes = avgNumberOfBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAtributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void setAttibute(Attribute a) {
        this.attributes.add(a);
    }

    public Attribute getAttribute(int index) {
        return this.attributes.get(index);
    }

    public void setMultivaluedAttribute(int index, boolean isMultivalued) {
        this.attributes.get(index).setMultivalued(isMultivalued);
    }

    public void setOptionalAttribute(int index, boolean isOptional) {
        this.attributes.get(index).setOptional(isOptional);
    }

    @Override
    public boolean equals(Object objeto) {
        if (objeto instanceof Entity) {
            Entity entity = (Entity) objeto;
            return entity.getName().equals(this.name);
        } else {
            return false;
        }
    }

    /**
     * @return the instances
     */
    public List<Instance> getInstances() {
        return instances;
    }

    /**
     * @param instances the instances to set
     */
    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }

}
