public class ChatMessage {
    long millis;
    String message;

    public ChatMessage(long millis, String message) {
        this.millis = millis;
        this.message = message;
    }

    @Override
    public String toString() {
        return "{ " +
                "\"millis\":" + millis +
                ", \"message\": \"" + message + '\"' +
                '}';
    }
}
