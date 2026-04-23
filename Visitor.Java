import java.time.LocalDateTime;

public class Visitor extends Person {
    LocalDateTime timeOfEntry;
    boolean hasOverstayed = false;

    public Visitor(String nationalIdCard) {
        super(nationalIdCard, "Temporary Visitor");
    }
}
