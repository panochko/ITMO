package SEclasses;

public class Person {
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
