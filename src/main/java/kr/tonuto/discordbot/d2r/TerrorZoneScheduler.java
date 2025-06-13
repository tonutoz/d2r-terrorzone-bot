package kr.tonuto.discordbot.d2r;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TerrorZoneScheduler {

    private final DiscordWebhookSender sender;
    private final RemoteZoneMetaRepository remoteZoneMetaRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_URL = "https://d2runewizard.com/api/terror-zone";

    @Scheduled(cron = "5 0 * * * *")
    public void sendTerrorZoneUpdate() {
        try {
            Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);
            Map<String, String> current = (Map<String, String>) response.get("currentTerrorZone");
            Map<String, String> next = (Map<String, String>) response.get("nextTerrorZone");

            sendTerrorZoneMessage(current.get("zone"), current.get("act"),
                    next.get("zone"), next.get("act"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTerrorZoneMessage(String currentZone, String currentAct,
                                          String nextZone, String nextAct) {

        Optional<ZoneMeta> currentMeta = remoteZoneMetaRepository.findByZoneName(currentZone);
        Optional<ZoneMeta> nextMeta = remoteZoneMetaRepository.findByZoneName(nextZone);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime nextHour = now.plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String currentTier = currentMeta.map(ZoneMeta::getTier).orElse("?");
        String nextTier = nextMeta.map(ZoneMeta::getTier).orElse("?");

        String currentKo = currentMeta.map(ZoneMeta::getKo).orElse(currentZone);
        String nextKo = nextMeta.map(ZoneMeta::getKo).orElse(nextZone);

        String currentImage = currentMeta.map(ZoneMeta::getImage).orElse("이미지 없음");
        String nextImage = nextMeta.map(ZoneMeta::getImage).orElse("이미지 없음");

        ZoneAlert alert = new ZoneAlert(
                currentKo, currentAct, currentTier, currentImage,
                nextKo, nextAct, nextTier, nextImage,
                now.format(formatter), nextHour.format(formatter)
        );
        sender.sendEmbedZoneAlert(alert);
    }
}