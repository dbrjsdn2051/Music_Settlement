package org.example.csvuploader.dto.settlement.flo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.csvuploader.domain.Flo;

@Getter
@Setter
@NoArgsConstructor
public class FloOriginalDataReaderDto {

    private String no;                   // No
    private String settlementMonth;      // 정산월
    private String salesMonth;           // 판매월
    private String companyCode;          // 회사 코드
    private String companyName;          // 회사 명
    private String contractCode;         // 계약 코드
    private String contractName;         // 계약명
    private String settlementCode;       // 정산 코드
    private String settlementCodeName;   // 정산 코드 명
    private String salesType;            // 판매 타입
    private String productType;          // 상품 타입
    private String mainCategory;         // 대 분류
    private String subCategory;          // 중 분류
    private String detailCategory;       // 소 분류
    private String albumCode;            // 앨범 코드
    private String albumName;            // 앨범명
    private String albumArtistName;      // 앨범 가수명
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
    private Double settlementAmount;     // 정산 금액

    @Builder
    public FloOriginalDataReaderDto(
            String no,
            String settlementMonth,
            String salesMonth,
            String companyCode,
            String companyName,
            String contractCode,
            String contractName,
            String settlementCode,
            String settlementCodeName,
            String salesType,
            String productType,
            String mainCategory,
            String subCategory,
            String detailCategory,
            String albumCode,
            String albumName,
            String albumArtistName,
            String upc,
            String companyAlbumCode,
            String songCode,
            String songName,
            String uci,
            String isrc,
            String companySongCode,
            String artistName,
            String agencyName,
            String hitCount,
            Double settlementAmount
    ) {
        this.no = no;
        this.settlementMonth = settlementMonth;
        this.salesMonth = salesMonth;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.settlementCode = settlementCode;
        this.settlementCodeName = settlementCodeName;
        this.salesType = salesType;
        this.productType = productType;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.detailCategory = detailCategory;
        this.albumCode = albumCode;
        this.albumName = albumName;
        this.albumArtistName = albumArtistName;
        this.upc = upc;
        this.companyAlbumCode = companyAlbumCode;
        this.songCode = songCode;
        this.songName = songName;
        this.uci = uci;
        this.isrc = isrc;
        this.companySongCode = companySongCode;
        this.artistName = artistName;
        this.agencyName = agencyName;
        this.hitCount = hitCount;
        this.settlementAmount = settlementAmount;
    }

    public static Flo from(FloOriginalDataReaderDto dto) {
        return Flo.builder()
                .no(dto.getNo())
                .settlementMonth(dto.getSettlementMonth())
                .salesMonth(dto.getSalesMonth())
                .companyCode(dto.getCompanyCode())
                .companyName(dto.getCompanyName())
                .contractCode(dto.getContractCode())
                .contractName(dto.getContractName())
                .settlementCode(dto.getSettlementCode())
                .settlementCodeName(dto.getSettlementCodeName())
                .salesType(dto.getSalesType())
                .productType(dto.getProductType())
                .mainCategory(dto.getMainCategory())
                .subCategory(dto.getSubCategory())
                .detailCategory(dto.getDetailCategory())
                .albumCode(dto.getAlbumCode())
                .albumName(dto.getAlbumName())
                .albumArtistName(dto.getAlbumArtistName())
                .upc(dto.getUpc())
                .companyAlbumCode(dto.getCompanyAlbumCode())
                .songCode(dto.getSongCode())
                .songName(dto.getSongName())
                .uci(dto.getUci())
                .isrc(dto.getIsrc())
                .companySongCode(dto.getCompanySongCode())
                .artistName(dto.getArtistName())
                .agencyName(dto.getAgencyName())
                .hitCount(dto.getHitCount())
                .settlementAmount(dto.getSettlementAmount())
                .build();
    }
}
