package org.example.csvuploader.dto.settlement.genie;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.csvuploader.domain.Genie;

@Getter
@Setter
@NoArgsConstructor
public class GenieOriginalDataReaderDto {

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
    public GenieOriginalDataReaderDto(
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

    public static Genie from(GenieOriginalDataReaderDto dto) {
        return Genie.builder()
                .settlementMonth(dto.getSettlementMonth())
                .saleMonth(dto.getSaleMonth())
                .songName(dto.getSongName())
                .albumName(dto.getAlbumName())
                .artist(dto.getArtist())
                .lpName(dto.getLpName())
                .songLid(dto.getSongLid())
                .albumLid(dto.getAlbumLid())
                .songExternalCode(dto.getSongExternalCode())
                .albumExternalCode(dto.getAlbumExternalCode())
                .dn(dto.getDn())
                .st(dto.getSt())
                .salesAmount(dto.getSalesAmount())
                .settlementAmount(dto.getSettlementAmount())
                .neighboringRightFee(dto.getNeighboringRightFee())
                .copyrightFee(dto.getCopyrightFee())
                .performanceRightFee(dto.getPerformanceRightFee())
                .build();
    }
}
