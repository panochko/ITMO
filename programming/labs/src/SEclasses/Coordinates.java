package SEclasses;

import java.util.Objects;

public class Coordinates {
    private Integer x;
    private float y;
    public Coordinates(Integer x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinates coordinates = (Coordinates) obj;
        return Objects.equals(x, coordinates.x) && y == coordinates.y;
    }
    @Override
    public int hashCode(){return (int) (this.x + this.y);}
    @Override
    public String toString(){return "Coordinates: ["+this.x+", "+this.y+"]";}

    public Integer getX(){return x;}
    public float getY() {return y;}
}
