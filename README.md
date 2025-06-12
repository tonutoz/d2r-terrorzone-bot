# D2R Terror Zone Discord Bot

A simple Spring Boot-based Discord bot that automatically fetches and notifies **Diablo II: Resurrected** Terror Zones every hour via a Discord webhook.

## 🔔 Features

- Sends hourly updates of the **current** and **next** Terror Zones.
- Provides zone details including:
  - Korean zone name
  - Zone tier (S, A, B, ...)
  - Associated act (e.g., Act 2)
  - Image preview
- Highlights high-tier zones (B or higher) for quick visibility.
- Supports remote or local zone metadata via JSON.

## 🕐 Example Message

```
🟢 Current: 바위투성이 황무지, 바위 무덤 (act2) 🕐 15:00
🔜 Next: 탈 라샤의 무덤, 탈 라샤 방 (act2) 🕐 16:00
```

> High-tier zones will appear in bold or with alerts for better attention.

## ⚙️ Configuration

### 1. Webhook

Set your Discord webhook URL in `application.yml`:

```yaml
discord:
  webhook:
    url: https://discord.com/api/webhooks/...
```

### 2. Remote Zone Metadata (Optional)

You can load `zone-meta-by-location.json` from a remote URL (e.g., GitHub):

```yaml
zone:
  meta:
    url: https://raw.githubusercontent.com/tonutoz/d2r-terrorzone-bot/master/src/main/resources/zone-meta-by-location.json
```

If this value is missing or the download fails, a bundled fallback JSON will be used from `classpath`.

## 🛠 Tech Stack

- Java 17
- Spring Boot 3.x
- Scheduled Tasks (`@Scheduled`)
- RestTemplate (for API call)
- Discord Webhook integration
- Jackson (for JSON parsing)
- Lombok

## 📦 Project Structure

```
kr.tonuto.discordbot
└── d2r
    ├── TerrorZoneScheduler.java      // Scheduled job that sends the message
    ├── DiscordWebhookSender.java     // Sends message to Discord
    ├── ZoneMetaLoader.java           // Loads zone metadata (remote/local)
    ├── ZoneMeta.java                 // Metadata model (tier, image, ko name)
    └── zone-meta-by-location.json    // Local fallback data
```

## 🏁 How to Run

```bash
./gradlew bootRun
```

You can deploy this as a background service or containerize it via Docker for scheduled webhook delivery.

## 📝 License

MIT License

---

> Created and maintained by [@tonutoz](https://github.com/tonutoz)
