package org.example.csvuploader.domain.excluce;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "common_idx_track_code", columnList = "trackCode"),
        @Index(name = "common_idx_song_code", columnList = "songCode")
})
public class CommonExcludeSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackCode;   // 트랙 코드
    private String memberShip;  // 멤버쉽
    private String songCode;    // 곡 코드

    @Builder
    public CommonExcludeSettlement(Long id, String trackCode, String memberShip, String songCode) {
        this.id = id;
        this.trackCode = trackCode;
        this.memberShip = memberShip;
        this.songCode = songCode;
    }
}
