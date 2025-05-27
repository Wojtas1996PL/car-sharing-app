package mate.academy.service;

public interface NotificationService {
    void sendMessage(String message);

    boolean isBotTokenNull();

    boolean isChatIdNull();
}
