package byod.model;

import java.time.LocalDateTime;

public class DeviceLog {

    public enum Action { IN, OUT }

    private int           id;
    private int           deviceId;
    private String        studentId;
    private Action        action;
    private int           loggedBy;
    private LocalDateTime timestamp;
    private String        notes;

    // Joined display fields (not in DB columns)
    private String studentName;
    private String serialNo;
    private String guardName;

    public DeviceLog() {}

    public DeviceLog(int deviceId, String studentId, Action action, int loggedBy, String notes) {
        this.deviceId  = deviceId;
        this.studentId = studentId;
        this.action    = action;
        this.loggedBy  = loggedBy;
        this.notes     = notes;
    }

    // Getters
    public int           getId()          { return id; }
    public int           getDeviceId()    { return deviceId; }
    public String        getStudentId()   { return studentId; }
    public Action        getAction()      { return action; }
    public int           getLoggedBy()    { return loggedBy; }
    public LocalDateTime getTimestamp()   { return timestamp; }
    public String        getNotes()       { return notes; }
    public String        getStudentName() { return studentName; }
    public String        getSerialNo()    { return serialNo; }
    public String        getGuardName()   { return guardName; }

    // Setters
    public void setId(int id)                     { this.id = id; }
    public void setDeviceId(int deviceId)         { this.deviceId = deviceId; }
    public void setStudentId(String studentId)    { this.studentId = studentId; }
    public void setAction(Action action)          { this.action = action; }
    public void setLoggedBy(int loggedBy)         { this.loggedBy = loggedBy; }
    public void setTimestamp(LocalDateTime ts)    { this.timestamp = ts; }
    public void setNotes(String notes)            { this.notes = notes; }
    public void setStudentName(String name)       { this.studentName = name; }
    public void setSerialNo(String sn)            { this.serialNo = sn; }
    public void setGuardName(String name)         { this.guardName = name; }
}
