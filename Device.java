package byod.model;

import java.time.LocalDateTime;

public class Device {

    public enum Type { LAPTOP, TABLET, SMARTPHONE }

    private int            id;
    private String         serialNo;
    private String         studentId;
    private Type           type;
    private String         brand;
    private String         model;
    private String         color;
    private String         macAddress;
    private LocalDateTime  registeredAt;

    public Device() {}

    public Device(String serialNo, String studentId, Type type,
                  String brand, String model, String color, String macAddress) {
        this.serialNo   = serialNo;
        this.studentId  = studentId;
        this.type       = type;
        this.brand      = brand;
        this.model      = model;
        this.color      = color;
        this.macAddress = macAddress;
    }

    // Getters
    public int           getId()           { return id; }
    public String        getSerialNo()     { return serialNo; }
    public String        getStudentId()    { return studentId; }
    public Type          getType()         { return type; }
    public String        getBrand()        { return brand; }
    public String        getModel()        { return model; }
    public String        getColor()        { return color; }
    public String        getMacAddress()   { return macAddress; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }

    // Setters
    public void setId(int id)                         { this.id = id; }
    public void setSerialNo(String serialNo)          { this.serialNo = serialNo; }
    public void setStudentId(String studentId)        { this.studentId = studentId; }
    public void setType(Type type)                    { this.type = type; }
    public void setBrand(String brand)                { this.brand = brand; }
    public void setModel(String model)                { this.model = model; }
    public void setColor(String color)                { this.color = color; }
    public void setMacAddress(String mac)             { this.macAddress = mac; }
    public void setRegisteredAt(LocalDateTime dt)     { this.registeredAt = dt; }

    public String getDisplayName() { return brand + " " + model + " (" + serialNo + ")"; }
}
