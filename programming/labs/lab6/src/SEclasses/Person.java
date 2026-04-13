package SEclasses;

import java.io.Serial;
import java.io.Serializable;

public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 8128692665860789625L;
    private double weight;
    private Location location;

    public Person(double weight, Location location){
        this.weight = weight;
        this.location = location;
    }
    public double getWeight() {return weight;}
    @Override
    public String toString(){return "Person: ["+this.weight+", "+this.location+"]";}
}
