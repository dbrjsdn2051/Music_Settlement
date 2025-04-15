package org.example.csvuploader.dto.segment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.csvuploader.domain.Youtube;

@Getter
@Setter
@NoArgsConstructor
public class YoutubeSegmentInsertReaderDto {

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
    public YoutubeSegmentInsertReaderDto(
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

    public static Youtube from(YoutubeSegmentInsertReaderDto dto) {
        return Youtube.builder()
                .adjustmentType(dto.getAdjustmentType())
                .country(dto.getCountry())
                .dayValue(dto.getDayValue())
                .videoId(dto.getVideoId())
                .videoChannelId(dto.getVideoChannelId())
                .assetId(dto.getAssetId())
                .assetChannelId(dto.getAssetChannelId())
                .assetTitle(dto.getAssetTitle())
                .assetLabels(dto.getAssetLabels())
                .assetType(dto.getAssetType())
                .customId(dto.getCustomId())
                .isrc(dto.getIsrc())
                .upc(dto.getUpc())
                .grid(dto.getGrid())
                .artist(dto.getArtist())
                .album(dto.getAlbum())
                .label(dto.getLabel())
                .claimType(dto.getClaimType())
                .contentType(dto.getContentType())
                .offer(dto.getOffer())
                .ownedViews(dto.getOwnedViews())
                .monetizedViewsAudio(dto.getMonetizedViewsAudio())
                .monetizedViewsAudioVisual(dto.getMonetizedViewsAudioVisual())
                .monetizedViews(dto.getMonetizedViews())
                .youTubeRevenueSplit(dto.getYouTubeRevenueSplit())
                .partnerRevenueProRata(dto.getPartnerRevenueProRata())
                .partnerRevenuePerSubMin(dto.getPartnerRevenuePerSubMin())
                .partnerRevenue(dto.getPartnerRevenue())
                .exchangeRate(dto.getExchangeRate())
                .partnerRevenueKrw(dto.getPartnerRevenueKrw())
                .settlementRate(dto.getSettlementRate())
                .performanceRightFee(dto.getPerformanceRightFee())
                .finalSettlementAmount(dto.getFinalSettlementAmount())
                .build();
    }
}
