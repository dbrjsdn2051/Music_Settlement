package org.example.csvuploader.dto.transform.vibe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VibePlusCsvReadDto {

    private String settlementMonth;           // 정산월
    private String serviceMonth;              // 서비스 월
    private String companyCode;               // 업체 코드
    private String companyName;               // 업체명
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
}
