package kr.tonuto.discordbot.d2r;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DiscordWebhookSender {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmbedZoneAlert(ZoneAlert alert) {
        Map<String, Object> currentEmbed = Map.of(
                "title", "🟢 현재: " + alert.getCurrentKo() + " (" + alert.getCurrentAct() + ") \n 🕐 " + alert.getCurrentTimeStr() + " 기준",
                "description", "Tier : " + highlightTier(alert.getCurrentTier()),
                "image", Map.of("url", alert.getCurrentImage())
        );

        Map<String, Object> nextEmbed = Map.of(
                "title", "🟡 다음: " + alert.getNextKo() + " (" + alert.getNextAct() + ") \n 🕑 " + alert.getNextTimeStr() + " 예고",
                "description", "Tier : " + highlightTier(alert.getNextTier()),
                "image", Map.of("url", alert.getNextImage())
        );

        Map<String, Object> payload = Map.of(
                "username", "Terror Zone Alert",
                "embeds", List.of(currentEmbed, nextEmbed)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(webhookUrl, new HttpEntity<>(payload, headers), String.class);
    }

    private String highlightTier(String tier) {
        return switch (tier) {
            case "S" -> "🟣 **S**";
            case "A" -> "🟢 **A**";
            case "B" -> "🟡 **B**";
            default -> tier;
        };
    }
}
