package backend.budget.common.dto;

import lombok.Data;

@Data
public class DiscordMessage<T> {
    private String content;
    private boolean tts = false; // 텍스트 음성 변환
    private  T data;
}