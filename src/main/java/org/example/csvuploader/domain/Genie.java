package org.example.csvuploader.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_genie_external_code", columnList = "songExternalCode"),
        @Index(name = "idx_genie_album_external_code", columnList = "albumExternalCode")
})
public class Genie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String settlementMonth;     // 정산 월
    private String saleMonth;           // 판매 월
    private String songName;            // 곡명
    private String albumName;           // 음반명
    private String artist;              // 아티스트 명
    private String lpName;              // LP명
    private String songLid;             // 곡 LID
    private String albumLid;            // 음반 LID
    private String songExternalCode;    // 곡 외부 코드
    private String albumExternalCode;   // 음반 외부 코드
    private String dn;                  // dn
    private String st;                  // st
    private String salesAmount;         // 판매 금액
    private String settlementAmount;    // 정산 금액
    private Double neighboringRightFee; // 인접 권료
    private String copyrightFee;        // 저작 권료
    private String performanceRightFee; // 실연 권료

    @Builder
    public Genie(
            String settlementMonth,
            String saleMonth,
            String songName,
            String albumName,
            String artist,
            String lpName,
            String songLid,
            String albumLid,
            String songExternalCode,
            String albumExternalCode,
            String dn,
            String st,
            String salesAmount,
            String settlementAmount,
            Double neighboringRightFee,
            String copyrightFee,
            String performanceRightFee
    ) {
        this.settlementMonth = settlementMonth;
        this.saleMonth = saleMonth;
        this.songName = songName;
        this.albumName = albumName;
        this.artist = artist;
        this.lpName = lpName;
        this.songLid = songLid;
        this.albumLid = albumLid;
        this.songExternalCode = songExternalCode;
        this.albumExternalCode = albumExternalCode;
        this.dn = dn;
        this.st = st;
        this.salesAmount = salesAmount;
        this.settlementAmount = settlementAmount;
        this.neighboringRightFee = neighboringRightFee;
        this.copyrightFee = copyrightFee;
        this.performanceRightFee = performanceRightFee;
    }
}
