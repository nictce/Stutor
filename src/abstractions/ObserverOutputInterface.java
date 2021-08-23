package abstractions;

public interface ObserverOutputInterface {

    /**
     * @param data any data that is crucial to the pages for them to request the information that they need from the database
     */
    void update(String data);

}
