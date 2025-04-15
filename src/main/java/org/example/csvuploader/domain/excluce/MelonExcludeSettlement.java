package org.example.csvuploader.domain.excluce;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "melon_idx_track_code", columnList = "trackCode")
})
public class MelonExcludeSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackCode;   // 트랙 코드
    private String memberShip;  // 멤버쉽 여부

    @Builder
    public MelonExcludeSettlement(Long id, String trackCode, String memberShip) {
        this.id = id;
        this.trackCode = trackCode;
        this.memberShip = memberShip;
    }
}
