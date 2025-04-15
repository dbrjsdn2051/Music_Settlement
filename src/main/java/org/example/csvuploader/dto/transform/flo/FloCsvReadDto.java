package org.example.csvuploader.dto.transform.flo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FloCsvReadDto {
    private String no;                   // No
    private String settlementMonth;      // 정산월
    private String salesMonth;           // 판매월
    private String companyCode;          // 회사 코드
    private String companyName;          // 회사명
    private String contractCode;         // 계약 코드
    private String contractName;         // 계약명
    private String settlementCode;       // 정산 코드
    private String settlementCodeName;   // 정산 코드 명
    private String salesType;            // 판매 타입
    private String productType;          // 상품 타입
    private String mainCategory;         // 대분류
    private String subCategory;          // 중분류
    private String detailCategory;       // 소분류
    private String albumCode;            // 앨범 코드
    private String albumName;            // 앨범명
    private String albumArtistName;      // 앨범 가수 명
    private String upc;                  // UPC
    private String companyAlbumCode;     // 업체 앨범 코드
    private String songCode;             // 곡코드
    private String songName;             // 곡명
    private String uci;                  // UCI
    private String isrc;                 // ISRC
    private String companySongCode;      // 업체 곡코드
    private String artistName;           // 가수명
    private String agencyName;           // 기획사 명
    private String hitCount;             // 히트수
    private String settlementAmount;     // 정산 금액
}
