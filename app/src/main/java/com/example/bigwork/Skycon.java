package com.example.bigwork;

public enum Skycon {
    CLEAR_DAY("晴（白天）",R.drawable.clear_day),
    CLEAR_NIGHT("晴（夜间）",R.drawable.clear_night),
    PARTLY_CLOUDY_DAY("多云（白天）",R.drawable.partly_cloud_day),
    PARTLY_CLOUDY_NIGHT("多云（夜间）",R.drawable.partly_cloudy_night),
    CLOUDY("阴",R.drawable.cloudy),
    LIGHT_HAZE("轻度雾霾",R.drawable.light_haze),
    MODERATE_HAZE("中度雾霾",R.drawable.moderate_haze),
    HEAVY_HAZE("重度雾霾",R.drawable.heavy_haze),
    LIGHT_RAIN("小雨",R.drawable.light_rain),
    MODERATE_RAIN("中雨",R.drawable.moderate_rain),
    HEAVY_RAIN("大雨",R.drawable.storm_rain),
    STORM_RAIN("暴雨",R.drawable.storm_rain),
    FOG("雾",R.drawable.fog),
    LIGHT_SNOW("小雪",R.drawable.light_snow),
    MODERATE_SNOW("中雪",R.drawable.moderate_snow),
    HEAVY_SNOW("大雪",R.drawable.heavy_snow),
    STORM_SNOW("暴雪",R.drawable.storm_snow),
    DUST("浮尘",R.drawable.dust),
    SAND("沙尘",R.drawable.sand),
    WIND("大风",R.drawable.wind);
    public String getDec() {
        return dec;
    }
    public int getSrc(){
        return src;
    }
    private String dec;
    private int src;
    private Skycon(String dec,int src){
        this.dec=dec;
        this.src=src;
    }
}
