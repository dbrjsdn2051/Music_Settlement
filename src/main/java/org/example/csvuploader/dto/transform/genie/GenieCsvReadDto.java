package org.example.csvuploader.dto.transform.genie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GenieCsvReadDto {
    private String songName;                // 곡명
    private String albumName;               // 음반명
    private String artist;                  // 아티스트 명
    private String lpName;                  // LP명
    private String songLid;                 // 곡 LID
    private String albumLid;                // 음반 LID
    private String songExternalCode;        // 곡 외부 코드
    private String albumExternalCode;       // 음반 외부 코드
    private String dn;                      // dn
    private String st;                      // st
    private String salesAmount;             // 판매 금액
    private String neighboringRightFee;     // 인접 권료
    private String copyrightFee;            // 저작 권료
    private String performanceRightFee;     // 실연 권료
}
