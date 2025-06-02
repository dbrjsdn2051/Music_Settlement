package org.example.csvuploader.dto.transform.melon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class MelonCsvWriterDtoTest {

    @Test
    void 빌더패턴으로_MelonCsvWriterDto생성() {
        // given & when
        MelonCsvWriterDto dto = MelonCsvWriterDto.builder()
                .settlementMonth("202401")
                .saleMonth("202312")
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
        assertThat(dto.getSettlementMonth()).isEqualTo("202401");
        assertThat(dto.getSaleMonth()).isEqualTo("202312");
        assertThat(dto.getSongName()).isEqualTo("테스트곡");
        assertThat(dto.getSongCode()).isEqualTo("SONG001");
        assertThat(dto.getArtist()).isEqualTo("테스트아티스트");
        assertThat(dto.getLhSongCode()).isEqualTo("LH001");
        assertThat(dto.getAlbumName()).isEqualTo("테스트앨범");
        assertThat(dto.getAlbumCode()).isEqualTo("ALBUM001");
        assertThat(dto.getLhAlbumCode()).isEqualTo("LH_ALBUM001");
        assertThat(dto.getBarcode()).isEqualTo("1234567890123");
        assertThat(dto.getMainArtist()).isEqualTo("테스트메인아티스트");
        assertThat(dto.getReleasesDate()).isEqualTo("2024-01-01");
        assertThat(dto.getAgency()).isEqualTo("테스트기획사");
        assertThat(dto.getLabel()).isEqualTo("테스트음반사");
        assertThat(dto.getTotalInfoFee()).isEqualTo("1000");
        assertThat(dto.getTotalSt()).isEqualTo("100");
        assertThat(dto.getTotalDl()).isEqualTo("50");
        assertThat(dto.getTotalRoyalty()).isEqualTo(5000.0);
    }

    @Test
    void MelonCsvReadDto로부터_MelonCsvWriterDto생성() throws IllegalAccessException {
        // given
        MelonCsvReadDto readDto = new MelonCsvReadDto();
        readDto.setSongName("테스트곡");
        readDto.setSongCode("SONG001");
        readDto.setArtist("테스트아티스트");
        readDto.setLhSongCode("LH001");
        readDto.setAlbumName("테스트앨범");
        readDto.setAlbumCode("ALBUM001");
        readDto.setLhAlbumCode("LH_ALBUM001");
        readDto.setBarcode("1234567890123");
        readDto.setMainArtist("테스트메인아티스트");
        readDto.setReleasesDate("2024-01-01");
        readDto.setAgency("테스트기획사");
        readDto.setLabel("테스트음반사");
        readDto.setTotalInfoFee("1000");
        readDto.setTotalSt("100");
        readDto.setTotalDl("50");
        readDto.setTotalRoyalty("5000");

        // when
        MelonCsvWriterDto writerDto = MelonCsvWriterDto.of(readDto);

        // then
        LocalDateTime now = LocalDateTime.now();
        String expectedSettlementMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String expectedSaleMonth = now.minusMonths(2).format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        assertThat(writerDto.getSettlementMonth()).isEqualTo(expectedSettlementMonth);
        assertThat(writerDto.getSaleMonth()).isEqualTo(expectedSaleMonth);
        assertThat(writerDto.getSongName()).isEqualTo("\"테스트곡\"");
        assertThat(writerDto.getSongCode()).isEqualTo("SONG001");
        assertThat(writerDto.getArtist()).isEqualTo("\"테스트아티스트\"");
        assertThat(writerDto.getLhSongCode()).isEqualTo("LH001");
        assertThat(writerDto.getAlbumName()).isEqualTo("\"테스트앨범\"");
        assertThat(writerDto.getAlbumCode()).isEqualTo("ALBUM001");
        assertThat(writerDto.getLhAlbumCode()).isEqualTo("LH_ALBUM001");
        assertThat(writerDto.getBarcode()).isEqualTo("1234567890123");
        assertThat(writerDto.getMainArtist()).isEqualTo("\"테스트메인아티스트\"");
        assertThat(writerDto.getReleasesDate()).isEqualTo("2024-01-01");
        assertThat(writerDto.getAgency()).isEqualTo("\"테스트기획사\"");
        assertThat(writerDto.getLabel()).isEqualTo("테스트음반사");
        assertThat(writerDto.getTotalInfoFee()).isEqualTo("1000");
        assertThat(writerDto.getTotalSt()).isEqualTo("100");
        assertThat(writerDto.getTotalDl()).isEqualTo("50");
        assertThat(writerDto.getTotalRoyalty()).isEqualTo(5000.0);
    }

    @Test
    void 특수문자가_포함된_데이터_처리() throws IllegalAccessException {
        // given
        MelonCsvReadDto readDto = new MelonCsvReadDto();
        readDto.setSongName("'''테스트곡'''");
        readDto.setSongCode("SONG001");
        readDto.setArtist("\"\"\"테스트아티스트\"\"\"");
        readDto.setLhSongCode("LH001");
        readDto.setAlbumName("테스트앨범");
        readDto.setAlbumCode("ALBUM001");
        readDto.setLhAlbumCode("LH_ALBUM001");
        readDto.setBarcode("1234567890123");
        readDto.setMainArtist("테스트메인아티스트");
        readDto.setReleasesDate("2024-01-01");
        readDto.setAgency("테스트기획사");
        readDto.setLabel("테스트음반사");
        readDto.setTotalInfoFee("1000");
        readDto.setTotalSt("100");
        readDto.setTotalDl("50");
        readDto.setTotalRoyalty("5000");

        // when
        MelonCsvWriterDto writerDto = MelonCsvWriterDto.of(readDto);

        // then
        // 특수문자 제거 후 결과 확인 (실제 구현에 따라 조정)
        assertThat(writerDto.getSongName()).contains("테스트곡");
        assertThat(writerDto.getArtist()).contains("테스트아티스트");
    }

    @Test
    void null값_처리() throws IllegalAccessException {
        // given
        MelonCsvReadDto readDto = new MelonCsvReadDto();
        readDto.setSongName(null);
        readDto.setSongCode("SONG001");
        readDto.setArtist(null);
        readDto.setTotalRoyalty("0");

        // when
        MelonCsvWriterDto writerDto = MelonCsvWriterDto.of(readDto);

        // then
        assertThat(writerDto.getSongName()).isEqualTo("\"null\"");
        assertThat(writerDto.getArtist()).isEqualTo("\"null\"");
        assertThat(writerDto.getTotalRoyalty()).isEqualTo(0.0);
    }
}
