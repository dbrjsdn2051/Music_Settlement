package org.example.csvuploader.dto.transform.melon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MelonCsvReadDto {
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
    private String totalRoyalty;    // 저작 인접권 료
}
