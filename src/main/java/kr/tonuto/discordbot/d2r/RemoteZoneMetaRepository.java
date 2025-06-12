package kr.tonuto.discordbot.d2r;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RemoteZoneMetaRepository {

    private final Map<String, ZoneMeta> zoneMap = new ConcurrentHashMap<>();

    @Value("${zone.meta.url:}") // 빈 문자열이면 fallback 처리
    private String remoteUrl;

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    @Scheduled(fixedRate = 3600_000) // 1시간마다 재시도
    public void loadZoneMeta() {
        if (remoteUrl != null && !remoteUrl.isBlank()) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(remoteUrl, String.class);
                Map<String, ZoneMeta> remote = mapper.readValue(response.getBody(), new TypeReference<>() {});
                zoneMap.clear();
                zoneMap.putAll(remote);
                System.out.println("✅ 외부 JSON 로드 완료 (" + zoneMap.size() + "개)");
                return;
            } catch (Exception e) {
                System.err.println("⚠️ 외부 zone.meta.url 로드 실패, classpath 로 fallback: " + e.getMessage());
            }
        }

        try (InputStream is = getClass().getResourceAsStream("/zone-meta-by-location.json")) {
            if (is == null) throw new FileNotFoundException("classpath zone-meta-by-location.json 없음");
            Map<String, ZoneMeta> local = mapper.readValue(is, new TypeReference<>() {});
            zoneMap.clear();
            zoneMap.putAll(local);
            System.out.println("✅ classpath JSON 로드 완료 (" + zoneMap.size() + "개)");
        } catch (Exception e) {
            System.err.println("❌ zone-meta 전체 로드 실패: " + e.getMessage());
        }
    }

    public Optional<ZoneMeta> findByZoneName(String zoneName) {
        if (zoneName == null || zoneName.isBlank()) return Optional.empty();
        return zoneMap.entrySet().stream()
                .filter(e -> zoneName.toLowerCase().contains(e.getKey().toLowerCase()))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
