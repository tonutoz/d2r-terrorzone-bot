package kr.tonuto.discordbot.d2r;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZoneAlert {
    private String currentKo;
    private String currentAct;
    private String currentTier;
    private String currentImage;

    private String nextKo;
    private String nextAct;
    private String nextTier;
    private String nextImage;

    private String currentTimeStr; // e.g. "15:00"
    private String nextTimeStr;    // e.g. "16:00"
}
