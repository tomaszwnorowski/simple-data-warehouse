CREATE TABLE IF NOT EXISTS analytics_tb
(
    id          SERIAL PRIMARY KEY,
    data_source VARCHAR(128) NOT NULL,
    campaign    VARCHAR(128) NOT NULL,
    daily       DATE         NOT NULL,
    clicks      INT          NOT NULL,
    impressions INT          NOT NULL
);

CREATE INDEX analytics_data_source_idx on analytics_tb (data_source);
CREATE INDEX analytics_campaign_idx on analytics_tb (campaign);
CREATE INDEX analytics_daily_idx on analytics_tb (daily);

COMMENT ON TABLE analytics_tb IS 'Representation of CSV with analytical data';
COMMENT ON COLUMN analytics_tb.id IS 'Artificial identifier that guarantees uniqueness';
COMMENT ON COLUMN analytics_tb.data_source IS 'Provider of metrics';
COMMENT ON COLUMN analytics_tb.campaign IS 'Campaign during which metrics were collected';
COMMENT ON COLUMN analytics_tb.daily IS 'Date for which metrics were aggregated';
COMMENT ON COLUMN analytics_tb.clicks IS 'Number of clicks daily';
COMMENT ON COLUMN analytics_tb.impressions IS 'Number of impressions daily';