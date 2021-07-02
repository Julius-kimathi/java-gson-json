package pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class  Transaction{
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("userId")
    @Expose
    private int userId;

    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("timestamp")
    @Expose
    private Long timestamp;

    @SerializedName("txnType")
    @Expose
    private String txnType;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("ip")
    @Expose
    private String ip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                ", txnType='" + txnType + '\'' +
                ", amount='" + amount + '\'' +
                ", location=" + location +
                ", ip='" + ip + '\'' +
                '}';
    }
}