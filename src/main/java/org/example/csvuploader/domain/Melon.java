package org.example.csvuploader.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_melon_song_code", columnList = "lhSongCode")
})
public class Melon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private String totalSt;         // st
    private String totalDl;         // dl
    private Double totalRoyalty;    // 저작 인접권 료

    @Builder
    public Melon(
            Long id,
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
        this.id = id;
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
}
