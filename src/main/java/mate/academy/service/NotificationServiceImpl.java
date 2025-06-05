package mate.academy.service;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
class NotificationServiceImpl implements NotificationService {
    private static final Dotenv DOTENV = Dotenv.configure().filename(".env").load();
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public String telegramBotToken() {
        return DOTENV.get("TELEGRAM_BOT_TOKEN");
    }

    public String telegramChatId() {
        return DOTENV.get("TELEGRAM_CHAT_ID");
    }

    @Override
    public void sendMessage(String message) {
        if (telegramBotToken() == null || telegramChatId() == null) {
            log.warn("Missing Telegram credentials. Notification skipped.");
            return;
        }
        String url = "https://api.telegram.org/bot" + telegramBotToken() + "/sendMessage";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = Map.of("chat_id", telegramChatId(), "text", message);
        restTemplate.postForObject(url, params, String.class);
    }

    @Override
    public boolean isBotTokenNull() {
        return telegramBotToken() == null;
    }

    @Override
    public boolean isChatIdNull() {
        return telegramChatId() == null;
    }
}
