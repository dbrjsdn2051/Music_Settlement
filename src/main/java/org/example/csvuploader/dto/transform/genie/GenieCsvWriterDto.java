package org.example.csvuploader.dto.transform.genie;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.csvuploader.util.SimpleQuoteRemover;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class GenieCsvWriterDto {
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
    private String neighboringRightFee; // 인접 권료
    private String copyrightFee;        // 저작 권료
    private String performanceRightFee; // 실연 권료

    @Builder
    public GenieCsvWriterDto(
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
            String neighboringRightFee,
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

    public static GenieCsvWriterDto of(GenieCsvReadDto dto) throws IllegalAccessException {
        LocalDate now = LocalDate.now();
        dto = SimpleQuoteRemover.removeSingleQuotes(dto);
        String externalCode = dto.getAlbumExternalCode();
        if (externalCode.startsWith("=")) {
            dto.setAlbumExternalCode(externalCode.substring(2, externalCode.length() - 1));
        }

        return GenieCsvWriterDto.builder()
                .settlementMonth(now.format(DateTimeFormatter.ofPattern("yyyyMM")))
                .saleMonth(now.minusMonths(2).format(DateTimeFormatter.ofPattern("yyyyMM")))
                .songName("\"" + dto.getSongName() + "\"")
                .albumName("\"" + dto.getAlbumName() + "\"")
                .artist("\"" + dto.getArtist() + "\"")
                .lpName(dto.getLpName())
                .songLid(dto.getSongLid())
                .albumLid(dto.getAlbumLid())
                .songExternalCode(dto.getSongExternalCode())
                .albumExternalCode(dto.getAlbumExternalCode())
                .dn(dto.getDn())
                .st(dto.getSt())
                .salesAmount(dto.getSalesAmount())
                .settlementAmount(dto.getNeighboringRightFee())
                .neighboringRightFee(dto.getNeighboringRightFee())
                .copyrightFee(dto.getCopyrightFee())
                .performanceRightFee(dto.getPerformanceRightFee())
                .build();
    }
}
