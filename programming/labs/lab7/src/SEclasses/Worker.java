package SEclasses;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Comparable<Worker>, Serializable {
    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    @Serial
    private static final long serialVersionUID = 4773939761504182406L;
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private transient java.time.LocalDateTime creationDate;
    private int salary;
    private transient java.time.LocalDate startDate;
    private Position position;
    private Status status;
    private Person person;

    public Worker() {};
    public Worker(String name, Coordinates coordinates, java.time.LocalDateTime creationDate, int salary, java.time.LocalDate startDate, Position position, Status status, Person person){
        this.id = 0;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.salary = salary;
        this.startDate = startDate;
        this.position = position;
        this.status = status;
        this.person = person;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Worker worker = (Worker) obj;
        return Objects.equals(id, worker.id);
    }
    @Override
    public int hashCode(){return Integer.hashCode(id);}
    @Override
    public String toString(){
        return id + "," + name + "," + coordinates + "," + creationDate + "," + salary + "," + startDate + "," + position + "," + status + "," + person;
    }
    public void DBSetId(int id) {this.id = id;}

    public void setId() {this.id = idGenerator.getAndIncrement();}
    public void setName(String name) {this.name = name;}
    public void setCoordinates(Coordinates coordinates) {this.coordinates = coordinates;}
    public void setCreationDate(LocalDateTime creationDate) {this.creationDate = creationDate;}
    public void setSalary(int salary) {this.salary = salary;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}
    public void setPosition(Position position) {this.position = position;}
    public void setStatus(Status status) {this.status = status;}
    public void setPerson(Person person) {this.person = person;}


    public int getId(){return id;}
    public String getName(){return name;}
    public Coordinates getCoordinates(){return coordinates;}
    public java.time.LocalDate getStartDate(){return startDate;}
    public int getSalary(){return salary;}
    public java.time.LocalDateTime getCreationDate(){return creationDate;}
    public Position getPosition(){return position;}
    public Status getStatus(){return status;}
    public Person getPerson(){return person;}

    @Override
    public int compareTo(Worker worker){return this.getId() - worker.getId();}

    public static void resetGenerator(Collection<Worker> collection) {
        int maxId = collection.stream()
                .mapToInt(Worker::getId)
                .max()
                .orElse(0);
        idGenerator.set(maxId + 1);
    }
    public static void resetGenerator(Map<Integer, Worker> mapCollection) {
        int maxId = mapCollection.values().stream()
                .mapToInt(Worker::getId)
                .max()
                .orElse(0);
        idGenerator.set(maxId + 1);
    }
}
