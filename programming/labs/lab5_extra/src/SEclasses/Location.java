package SEclasses;

public class Location {
    private Integer x;
    private long y;
    private long z;
    private String name;
    public Location(Integer x, long y, long z, String name){
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }
    public Integer getX() {return x;}

    public long getY() {return y;}

    public long getZ() {return z;}

    public String getName() {return name;}
    @Override
    public String toString(){return "Location: ["+this.x+", "+this.y+", "+this.z+", "+this.name+"]";}
}
