package view;

public class ChatMessage {
    private final String message;
    private final Sender sender;

    public enum Sender {
        USER, AI
    }

    public ChatMessage(String message, Sender sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public Sender getSender() {
        return sender;
    }
}
