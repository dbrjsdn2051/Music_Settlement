package org.example.csvuploader.dto.transform.melon;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.csvuploader.util.SimpleQuoteRemover;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class MelonCsvWriterDto {
    private String settlementMonth; // 정산 월
    private String saleMonth;       // 판매 월
    private String songName;        // 곡명
    private String songCode;        // 곡코드
    private String artist;          // 아티스트
    private String lhSongCode;      // LH곡 코드
    private String albumName;       // 음반명
    private String albumCode;       // 음반 코드
    private String lhAlbumCode;     // LH 음반 코드
    private String barcode;         // 바코드
    private String mainArtist;      // 대표 아티스트
    private String releasesDate;    // 발매일
    private String agency;          // 기획사
    private String label;           // 음반사
    private String totalInfoFee;    // 정보 이용료
    private String totalSt;           // st
    private String totalDl;           // dl
    private Double totalRoyalty;    // 저작 인접권 료

    @Builder
    public MelonCsvWriterDto(
            String settlementMonth,
            String saleMonth,
            String songName,
            String songCode,
            String artist,
            String lhSongCode,
            String albumName,
            String albumCode,
            String lhAlbumCode,
            String barcode,
            String mainArtist,
            String releasesDate,
            String agency,
            String label,
            String totalInfoFee,
            String totalSt,
            String totalDl,
            Double totalRoyalty
    ) {
        this.settlementMonth = settlementMonth;
        this.saleMonth = saleMonth;
        this.songName = songName;
        this.songCode = songCode;
        this.artist = artist;
        this.lhSongCode = lhSongCode;
        this.albumName = albumName;
        this.albumCode = albumCode;
        this.lhAlbumCode = lhAlbumCode;
        this.barcode = barcode;
        this.mainArtist = mainArtist;
        this.releasesDate = releasesDate;
        this.agency = agency;
        this.label = label;
        this.totalInfoFee = totalInfoFee;
        this.totalSt = totalSt;
        this.totalDl = totalDl;
        this.totalRoyalty = totalRoyalty;
    }

    public static MelonCsvWriterDto of(MelonCsvReadDto dto) throws IllegalAccessException {
        LocalDateTime now = LocalDateTime.now();
        dto = SimpleQuoteRemover.removeTripleQuotes(dto);
        dto = SimpleQuoteRemover.removeSingleQuotes(dto);

        return MelonCsvWriterDto.builder()
                .settlementMonth(now.format(DateTimeFormatter.ofPattern("yyyyMM")))
                .saleMonth(now.minusMonths(2).format(DateTimeFormatter.ofPattern("yyyyMM")))
                .songName("\"" + dto.getSongName() + "\"")
                .songCode(dto.getSongCode())
                .artist("\"" + dto.getArtist() + "\"")
                .lhSongCode(dto.getLhSongCode())
                .albumName("\"" + dto.getAlbumName() + "\"")
                .albumCode(dto.getAlbumCode())
                .lhAlbumCode(dto.getLhAlbumCode())
                .barcode(dto.getBarcode())
                .mainArtist("\"" + dto.getMainArtist() + "\"")
                .releasesDate(dto.getReleasesDate())
                .agency("\"" + dto.getAgency() + "\"")
                .label(dto.getLabel())
                .totalInfoFee(dto.getTotalInfoFee())
                .totalSt(dto.getTotalSt())
                .totalDl(dto.getTotalDl())
                .totalRoyalty(Double.valueOf(dto.getTotalRoyalty()))
                .build();
    }
}
