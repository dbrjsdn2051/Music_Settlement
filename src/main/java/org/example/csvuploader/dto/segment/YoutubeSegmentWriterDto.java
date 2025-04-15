package org.example.csvuploader.dto.segment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.csvuploader.domain.Youtube;
import org.example.csvuploader.util.SimpleQuoteRemover;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class YoutubeSegmentWriterDto {

    private String adjustmentType;
    private String country;
    private String dayValue;
    private String videoId;
    private String videoChannelId;
    private String assetId;
    private String assetChannelId;
    private String assetTitle;
    private String assetLabels;
    private String assetType;
    private String customId;
    private String isrc;
    private String upc;
    private String grid;
    private String artist;
    private String album;
    private String label;
    private String claimType;
    private String contentType;
    private String offer;
    private String ownedViews;
    private String monetizedViewsAudio;
    private String monetizedViewsAudioVisual;
    private String monetizedViews;
    private String youTubeRevenueSplit;
    private String partnerRevenueProRata;
    private String partnerRevenuePerSubMin;
    private String partnerRevenue;
    private String exchangeRate;
    private String partnerRevenueKrw;
    private String settlementRate;              // 정산 요율
    private String performanceRightFee;         // 실연권 요율
    private String finalSettlementAmount;       // 최종 정산금

    @Builder
    public YoutubeSegmentWriterDto(
            String adjustmentType,
            String country,
            String dayValue,
            String videoId,
            String videoChannelId,
            String assetId,
            String assetChannelId,
            String assetTitle,
            String assetLabels,
            String assetType,
            String customId,
            String isrc,
            String upc,
            String grid,
            String artist,
            String album,
            String label,
            String claimType,
            String contentType,
            String offer,
            String ownedViews,
            String monetizedViewsAudio,
            String monetizedViewsAudioVisual,
            String monetizedViews,
            String youTubeRevenueSplit,
            String partnerRevenueProRata,
            String partnerRevenuePerSubMin,
            String partnerRevenue,
            String exchangeRate,
            String partnerRevenueKrw,
            String settlementRate,
            String performanceRightFee,
            String finalSettlementAmount
    ) {
        this.adjustmentType = adjustmentType;
        this.country = country;
        this.dayValue = dayValue;
        this.videoId = videoId;
        this.videoChannelId = videoChannelId;
        this.assetId = assetId;
        this.assetChannelId = assetChannelId;
        this.assetTitle = assetTitle;
        this.assetLabels = assetLabels;
        this.assetType = assetType;
        this.customId = customId;
        this.isrc = isrc;
        this.upc = upc;
        this.grid = grid;
        this.artist = artist;
        this.album = album;
        this.label = label;
        this.claimType = claimType;
        this.contentType = contentType;
        this.offer = offer;
        this.ownedViews = ownedViews;
        this.monetizedViewsAudio = monetizedViewsAudio;
        this.monetizedViewsAudioVisual = monetizedViewsAudioVisual;
        this.monetizedViews = monetizedViews;
        this.youTubeRevenueSplit = youTubeRevenueSplit;
        this.partnerRevenueProRata = partnerRevenueProRata;
        this.partnerRevenuePerSubMin = partnerRevenuePerSubMin;
        this.partnerRevenue = partnerRevenue;
        this.exchangeRate = exchangeRate;
        this.partnerRevenueKrw = partnerRevenueKrw;
        this.settlementRate = settlementRate;
        this.performanceRightFee = performanceRightFee;
        this.finalSettlementAmount = finalSettlementAmount;
    }

    public static YoutubeSegmentWriterDto of(Youtube data) {
        return YoutubeSegmentWriterDto.builder()
                .adjustmentType(data.getAdjustmentType())
                .country(data.getCountry())
                .dayValue(data.getDayValue())
                .videoId(data.getVideoId())
                .videoChannelId(data.getVideoChannelId())
                .assetId(data.getAssetId())
                .assetChannelId(data.getAssetChannelId())
                .assetLabels(data.getAssetLabels())
                .assetType(data.getAssetType())
                .customId(data.getCustomId())
                .isrc(data.getIsrc())
                .upc(data.getUpc())
                .grid(data.getGrid())
                .assetTitle(data.getAssetTitle().startsWith("\"") ? data.getAssetTitle() : "\"" + data.getAssetTitle() + "\"")
                .artist(data.getArtist().startsWith("\"") ? data.getArtist() : "\"" + data.getArtist() + "\"")
                .album(data.getAlbum().startsWith("\"") ? data.getAlbum() : "\"" + data.getAlbum() + "\"")
                .label("\"" + data.getLabel() + "\"")
                .claimType(data.getClaimType())
                .contentType(data.getContentType())
                .offer(data.getOffer())
                .ownedViews(data.getOwnedViews())
                .monetizedViewsAudio(data.getMonetizedViewsAudio())
                .monetizedViewsAudioVisual(data.getMonetizedViewsAudioVisual())
                .monetizedViews(data.getMonetizedViews())
                .youTubeRevenueSplit(BigDecimal.valueOf(Double.parseDouble(data.getYouTubeRevenueSplit())).toPlainString())
                .partnerRevenueProRata(BigDecimal.valueOf(Double.parseDouble(data.getPartnerRevenueProRata())).toPlainString())
                .partnerRevenuePerSubMin(BigDecimal.valueOf(Double.parseDouble(data.getPartnerRevenuePerSubMin())).toPlainString())
                .partnerRevenue(BigDecimal.valueOf(Double.parseDouble(data.getPartnerRevenue())).toPlainString())
                .exchangeRate(data.getExchangeRate())
                .partnerRevenueKrw(data.getPartnerRevenueKrw())
                .settlementRate(String.valueOf((int) (Double.parseDouble(data.getSettlementRate()) * 100)) + "%")
                .performanceRightFee(String.format("%.2f", Double.parseDouble(data.getPerformanceRightFee()) * 100) + "%")
                .finalSettlementAmount(data.getFinalSettlementAmount())
                .build();
    }
}
