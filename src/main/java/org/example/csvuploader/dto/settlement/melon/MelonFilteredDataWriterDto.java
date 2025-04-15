package org.example.csvuploader.dto.settlement.melon;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.csvuploader.domain.Melon;

@Getter
@Setter
@NoArgsConstructor
public class MelonFilteredDataWriterDto {

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
    public MelonFilteredDataWriterDto(
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

    public static MelonFilteredDataWriterDto of(Melon data) {
        return MelonFilteredDataWriterDto.builder()
                .settlementMonth(data.getSettlementMonth())
                .saleMonth(data.getSaleMonth())
                .songName(data.getSongName())
                .songCode(data.getSongCode())
                .artist(data.getArtist())
                .lhSongCode(data.getLhSongCode())
                .albumName(data.getAlbumName())
                .albumCode(data.getAlbumCode())
                .lhAlbumCode(data.getLhAlbumCode())
                .barcode(data.getBarcode())
                .mainArtist(data.getMainArtist())
                .releasesDate(data.getReleasesDate())
                .agency(data.getAgency())
                .label(data.getLabel())
                .totalInfoFee(data.getTotalInfoFee())
                .totalSt(data.getTotalSt())
                .totalDl(data.getTotalDl())
                .totalRoyalty(data.getTotalRoyalty())
                .build();
    }
}
