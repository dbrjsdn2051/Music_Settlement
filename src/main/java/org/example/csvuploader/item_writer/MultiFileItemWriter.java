package org.example.csvuploader.item_writer;

import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.dto.segment.YoutubeSegmentWriterDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@StepScope
@Component
public class MultiFileItemWriter implements ItemWriter<YoutubeSegmentWriterDto> {

    private final String baseDir;
    private final String fileName;
    private final AtomicInteger count = new AtomicInteger();
    private static final String HEADER_NAMES =
            "Adjustment Type,Country,Day,Video ID,Video Channel ID,Asset ID,Asset Channel ID,Asset Title," +
                    "Asset Labels,Asset Type,Custom ID,ISRC,UPC,GRID,Artist,Album,Label,Claim Type,Content Type,Offer," +
                    "Owned Views,Monetized Views : Audio,Monetized Views : Audio Visual,Monetized Views," +
                    "YouTube Revenue Split,Partner Revenue : Pro Rata,Partner Revenue : Per Sub Min,Partner Revenue,환율," +
                    "Partner Revenue (KRW),정산요율,실연권요율,최종 정산금";


    public MultiFileItemWriter(
            @Value("#{jobParameters['artist']}") String artist
    ) {
        this.baseDir = FileConstants.getYoutubeDirectoryPath();
        LocalDateTime now = LocalDateTime.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        this.fileName = yearMonth + "_YouTube정산내역_구독_" + artist + "-";
    }

    @Override
    public void write(Chunk<? extends YoutubeSegmentWriterDto> chunk) throws Exception {
        if (chunk.isEmpty()) {
            return;
        }

        String filePath = baseDir + File.separator + this.fileName + count.addAndGet(1) + ".csv";

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.write(HEADER_NAMES);
            writer.newLine();

            for (YoutubeSegmentWriterDto item : chunk) {
                StringBuilder line = new StringBuilder();
                line.append(escapeCsvValue(item.getAdjustmentType())).append(",");
                line.append(escapeCsvValue(item.getCountry())).append(",");
                line.append(escapeCsvValue(item.getDayValue())).append(",");
                line.append(escapeCsvValue(item.getVideoId())).append(",");
                line.append(escapeCsvValue(item.getVideoChannelId())).append(",");
                line.append(escapeCsvValue(item.getAssetId())).append(",");
                line.append(escapeCsvValue(item.getAssetChannelId())).append(",");
                line.append(escapeCsvValue(item.getAssetTitle())).append(",");
                line.append(escapeCsvValue(item.getAssetLabels())).append(",");
                line.append(escapeCsvValue(item.getAssetType())).append(",");
                line.append(escapeCsvValue(item.getCustomId())).append(",");
                line.append(escapeCsvValue(item.getIsrc())).append(",");
                line.append(escapeCsvValue(item.getUpc())).append(",");
                line.append(escapeCsvValue(item.getGrid())).append(",");
                line.append(escapeCsvValue(item.getArtist())).append(",");
                line.append(escapeCsvValue(item.getAlbum())).append(",");
                line.append(escapeCsvValue(item.getLabel())).append(",");
                line.append(escapeCsvValue(item.getClaimType())).append(",");
                line.append(escapeCsvValue(item.getContentType())).append(",");
                line.append(escapeCsvValue(item.getOffer())).append(",");
                line.append(escapeCsvValue(item.getOwnedViews())).append(",");
                line.append(escapeCsvValue(item.getMonetizedViewsAudio())).append(",");
                line.append(escapeCsvValue(item.getMonetizedViewsAudioVisual())).append(",");
                line.append(escapeCsvValue(item.getMonetizedViews())).append(",");
                line.append(escapeCsvValue(item.getYouTubeRevenueSplit())).append(",");
                line.append(escapeCsvValue(item.getPartnerRevenueProRata())).append(",");
                line.append(escapeCsvValue(item.getPartnerRevenuePerSubMin())).append(",");
                line.append(escapeCsvValue(item.getPartnerRevenue())).append(",");
                line.append(escapeCsvValue(item.getExchangeRate())).append(",");
                line.append(escapeCsvValue(item.getPartnerRevenueKrw())).append(",");
                line.append(escapeCsvValue(item.getSettlementRate())).append(",");
                line.append(escapeCsvValue(item.getPerformanceRightFee())).append(",");
                line.append(escapeCsvValue(item.getFinalSettlementAmount()));

                writer.write(line.toString());
                writer.newLine();
            }

            writer.flush();
        }
    }

    private String escapeCsvValue(String value) {
        if (value == null) return "";
        return value;
    }
}