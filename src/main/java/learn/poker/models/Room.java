package learn.poker.models;

public class Room {

    private int roomId;
    private double stake;
    private int seats;

    public Room(int roomId, double stake, int seats) {
        this.roomId = roomId;
        this.stake = stake;
        this.seats = seats;
    }

    public Room(){}

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
