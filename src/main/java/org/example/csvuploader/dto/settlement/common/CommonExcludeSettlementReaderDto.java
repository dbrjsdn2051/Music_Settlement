package org.example.csvuploader.dto.settlement.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.csvuploader.domain.excluce.CommonExcludeSettlement;
import org.example.csvuploader.util.SimpleQuoteRemover;

@Getter
@Setter
@NoArgsConstructor
public class CommonExcludeSettlementReaderDto {

    private String trackCode;   // 트랙 코드
    private String memberShip;  // 멤버쉽
    private String songCode;    // 곡 코드

    @Builder
    public CommonExcludeSettlementReaderDto(String trackCode, String memberShip, String songCode) {
        this.trackCode = trackCode;
        this.memberShip = memberShip;
        this.songCode = songCode;
    }

    public static CommonExcludeSettlement from(CommonExcludeSettlementReaderDto dto) throws IllegalAccessException {
        dto = SimpleQuoteRemover.removeSingleQuotes(dto);
        return CommonExcludeSettlement.builder()
                .trackCode(dto.getTrackCode())
                .memberShip(dto.getMemberShip())
                .songCode(dto.getSongCode())
                .build();
    }
}
