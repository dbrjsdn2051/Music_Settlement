package org.example.csvuploader.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MelonTest {

    @Test
    void 빌더패턴으로_Melon객체생성() {
        // given & when
        Melon melon = Melon.builder()
                .id(1L)
                .settlementMonth("2024-01")
                .saleMonth("2024-01")
                .songName("테스트곡")
                .songCode("SONG001")
                .artist("테스트아티스트")
                .lhSongCode("LH001")
                .albumName("테스트앨범")
                .albumCode("ALBUM001")
                .lhAlbumCode("LH_ALBUM001")
                .barcode("1234567890123")
                .mainArtist("테스트메인아티스트")
                .releasesDate("2024-01-01")
                .agency("테스트기획사")
                .label("테스트음반사")
                .totalInfoFee("1000")
                .totalSt("100")
                .totalDl("50")
                .totalRoyalty(5000.0)
                .build();

        // then
        assertThat(melon.getId()).isEqualTo(1L);
        assertThat(melon.getSettlementMonth()).isEqualTo("2024-01");
        assertThat(melon.getSaleMonth()).isEqualTo("2024-01");
        assertThat(melon.getSongName()).isEqualTo("테스트곡");
        assertThat(melon.getSongCode()).isEqualTo("SONG001");
        assertThat(melon.getArtist()).isEqualTo("테스트아티스트");
        assertThat(melon.getLhSongCode()).isEqualTo("LH001");
        assertThat(melon.getAlbumName()).isEqualTo("테스트앨범");
        assertThat(melon.getAlbumCode()).isEqualTo("ALBUM001");
        assertThat(melon.getLhAlbumCode()).isEqualTo("LH_ALBUM001");
        assertThat(melon.getBarcode()).isEqualTo("1234567890123");
        assertThat(melon.getMainArtist()).isEqualTo("테스트메인아티스트");
        assertThat(melon.getReleasesDate()).isEqualTo("2024-01-01");
        assertThat(melon.getAgency()).isEqualTo("테스트기획사");
        assertThat(melon.getLabel()).isEqualTo("테스트음반사");
        assertThat(melon.getTotalInfoFee()).isEqualTo("1000");
        assertThat(melon.getTotalSt()).isEqualTo("100");
        assertThat(melon.getTotalDl()).isEqualTo("50");
        assertThat(melon.getTotalRoyalty()).isEqualTo(5000.0);
    }

    @Test
    void 기본생성자로_Melon객체생성() {
        // given & when
        Melon melon = new Melon();
        melon.setId(1L);
        melon.setSongName("테스트곡");
        melon.setArtist("테스트아티스트");

        // then
        assertThat(melon.getId()).isEqualTo(1L);
        assertThat(melon.getSongName()).isEqualTo("테스트곡");
        assertThat(melon.getArtist()).isEqualTo("테스트아티스트");
    }

    @Test
    void Melon객체_null값처리() {
        // given & when
        Melon melon = Melon.builder()
                .songName(null)
                .artist(null)
                .totalRoyalty(null)
                .build();

        // then
        assertThat(melon.getSongName()).isNull();
        assertThat(melon.getArtist()).isNull();
        assertThat(melon.getTotalRoyalty()).isNull();
    }
}
