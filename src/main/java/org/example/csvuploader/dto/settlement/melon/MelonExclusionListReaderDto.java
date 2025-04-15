package org.example.csvuploader.dto.settlement.melon;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.csvuploader.domain.excluce.MelonExcludeSettlement;

@Getter
@Setter
@NoArgsConstructor
public class MelonExclusionListReaderDto {

    private String trackCode;   // 트랙 코드
    private String memberShip;  // 멤버쉽 여부

    @Builder
    public MelonExclusionListReaderDto(String trackCode, String memberShip) {
        this.trackCode = trackCode;
        this.memberShip = memberShip;
    }

    public static MelonExcludeSettlement from(MelonExclusionListReaderDto dto) {
        return MelonExcludeSettlement.builder()
                .trackCode(dto.getTrackCode())
                .memberShip(dto.getMemberShip())
                .build();
    }
}
