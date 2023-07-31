package learn.poker.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Images {
    @JsonProperty("svg")
    public String getSvg() {
        return this.svg; }
    public void setSvg(String svg) {
        this.svg = svg; }
    String svg;
    @JsonProperty("png")
    public String getPng() {
        return this.png; }
    public void setPng(String png) {
        this.png = png; }
    String png;
}
