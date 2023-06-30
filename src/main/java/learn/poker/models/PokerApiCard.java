package learn.poker.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PokerApiCard {
    @JsonProperty("code")
    public String getCode() {
        return this.code; }
    public void setCode(String code) {
        this.code = code; }
    String code;
    @JsonProperty("image")
    public String getImage() {
        return this.image; }
    public void setImage(String image) {
        this.image = image; }
    String image;
    @JsonProperty("images")
    public Images getImages() {
        return this.images; }
    public void setImages(Images images) {
        this.images = images; }
    Images images;
    @JsonProperty("value")
    public String getValue() {
        return this.value; }
    public void setValue(String value) {
        this.value = value; }
    String value;
    @JsonProperty("suit")
    public String getSuit() {
        return this.suit; }
    public void setSuit(String suit) {
        this.suit = suit; }
    String suit;
}
