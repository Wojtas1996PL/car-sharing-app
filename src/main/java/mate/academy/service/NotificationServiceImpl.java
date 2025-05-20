package mate.academy.service;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
class NotificationServiceImpl implements NotificationService {
    private static final Dotenv DOTENV = Dotenv.configure().filename(".env").load();
    private final String botToken = DOTENV.get("TELEGRAM_BOT_TOKEN");
    private final String chatId = DOTENV.get("TELEGRAM_CHAT_ID");

    @Override
    public void sendMessage(String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = Map.of("chat_id", chatId, "text", message);
        restTemplate.postForObject(url, params, String.class);
    }
}
