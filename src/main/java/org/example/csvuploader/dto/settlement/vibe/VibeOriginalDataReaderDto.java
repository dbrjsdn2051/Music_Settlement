package org.example.csvuploader.dto.settlement.vibe;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.csvuploader.domain.Vibe;

@Getter
@Setter
@NoArgsConstructor
public class VibeOriginalDataReaderDto {

    private String settlementMonth;           // 정산월
    private String serviceMonth;              // 서비스 월
    private String companyCode;               // 업체 코드
    private String companyName;               // 업체명
    private String agencyCode;                // 기획사 코드
    private String agencyName;                // 기획사 명
    private String contractCode;              // 계약 코드
    private String contractName;              // 계약명
    private String channelName;               // 채널명
    private String serviceCode;               // 서비스 코드
    private String productCode;               // 상품 코드
    private String serviceName;               // 서비스 명
    private String hasMusicVideo;             // 뮤비 여부
    private String isFree;                    // 무료 여부
    private String songCode;                  // 곡코드
    private String companySongCode;           // 업체 곡코드
    private String uci;                       // UCI
    private String songName;                  // 곡명
    private String albumCode;                 // 앨범 코드
    private String companyAlbumCode;          // 업체 앨범 코드
    private String albumName;                 // 앨범명
    private String artistName;                // 아티스트 명
    private String hitCount;                  // 히트수
    private String vpsFinalRights;            // VPS 최종 인접권 료

    @Builder
    public VibeOriginalDataReaderDto(
            String settlementMonth,
            String serviceMonth,
            String companyCode,
            String companyName,
            String agencyCode,
            String agencyName,
            String contractCode,
            String contractName,
            String channelName,
            String serviceCode,
            String productCode,
            String serviceName,
            String hasMusicVideo,
            String isFree,
            String songCode,
            String companySongCode,
            String uci,
            String songName,
            String albumCode,
            String companyAlbumCode,
            String albumName,
            String artistName,
            String hitCount,
            String vpsFinalRights
    ) {
        this.settlementMonth = settlementMonth;
        this.serviceMonth = serviceMonth;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.agencyCode = agencyCode;
        this.agencyName = agencyName;
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.channelName = channelName;
        this.serviceCode = serviceCode;
        this.productCode = productCode;
        this.serviceName = serviceName;
        this.hasMusicVideo = hasMusicVideo;
        this.isFree = isFree;
        this.songCode = songCode;
        this.companySongCode = companySongCode;
        this.uci = uci;
        this.songName = songName;
        this.albumCode = albumCode;
        this.companyAlbumCode = companyAlbumCode;
        this.albumName = albumName;
        this.artistName = artistName;
        this.hitCount = hitCount;
        this.vpsFinalRights = vpsFinalRights;
    }

    public static Vibe from(VibeOriginalDataReaderDto dto) {
        return Vibe.builder()
                .settlementMonth(dto.getSettlementMonth())
                .serviceMonth(dto.getServiceMonth())
                .companyCode(dto.getCompanyCode())
                .companyName(dto.getCompanyName())
                .agencyCode(dto.getAgencyCode())
                .agencyName(dto.getAgencyName())
                .contractCode(dto.getContractCode())
                .contractName(dto.getContractName())
                .channelName(dto.getChannelName())
                .serviceCode(dto.getServiceCode())
                .productCode(dto.getProductCode())
                .serviceName(dto.getServiceName())
                .hasMusicVideo(dto.getHasMusicVideo())
                .isFree(dto.getIsFree())
                .songCode(dto.getSongCode())
                .companySongCode(dto.getCompanySongCode())
                .uci(dto.getUci())
                .songName(dto.getSongName())
                .albumCode(dto.getAlbumCode())
                .companyAlbumCode(dto.getCompanyAlbumCode())
                .albumName(dto.getAlbumName())
                .artistName(dto.getArtistName())
                .hitCount(dto.getHitCount())
                .vpsFinalRights(dto.getVpsFinalRights())
                .build();
    }
}
