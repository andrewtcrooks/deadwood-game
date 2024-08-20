/**
 * Subject interface for the Observer pattern.
 */
public interface Subject {
    public void registerObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyObservers(String eventType, Object eventData);
}