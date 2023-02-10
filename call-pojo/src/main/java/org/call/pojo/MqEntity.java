package org.call.pojo;

public class MqEntity {
    private Long uuid;
    private String telephoneNumber;

    public MqEntity(Long uuid) {
        this.uuid = uuid;
    }

    public MqEntity(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public MqEntity(Long uuid, String telephoneNumber) {
        this.uuid = uuid;
        this.telephoneNumber = telephoneNumber;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public String toString() {
        return "MqEntity{" +
                "uuid=" + uuid +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                '}';
    }
}
