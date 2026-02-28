package SEclasses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Comparable<Worker>{
    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    private int id;
    private String name;
    private Coordinates coordinates;
    private transient java.time.LocalDateTime creationDate;
    private int salary;
    private transient java.time.LocalDate startDate;
    private Position position;
    private Status status;
    private Person person;

    public Worker(String name, Coordinates coordinates, java.time.LocalDateTime creationDate, int salary, java.time.LocalDate startDate, Position position, Status status, Person person){
        this.id = idGenerator.getAndIncrement();
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
        return id == worker.id;
    }
    @Override
    public int hashCode(){return Integer.hashCode(id);}
    @Override
    public String toString(){
        return "Worker: id="+id+", name="+name+", coordinates="+coordinates+", creationDate="+creationDate+", salary="+salary+", startDate="+startDate+", position="+position+", status="+status+", person="+person;
    }
    public void setId(int id) {this.id = id;}
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
}
