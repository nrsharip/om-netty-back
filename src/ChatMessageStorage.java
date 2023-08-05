import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChatMessageStorage {

    public List<ChatMessage> messages = new ArrayList<>();

    public static final ChatMessageStorage INSTANCE = new ChatMessageStorage();

    public List<ChatMessage> getMessages(long millis) {
        return messages.stream()
                .filter(m -> m.millis >= millis)
                .sorted((m1, m2) -> Long.compare(m2.millis, m1.millis))
                .collect(Collectors.toList());
    }

    public void addMessage(String message) {
        messages.add(new ChatMessage(System.currentTimeMillis(), message));
    }

}
