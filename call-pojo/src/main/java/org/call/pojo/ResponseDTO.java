package org.call.pojo;

public class ResponseDTO {
    String result;

    String telephoneNumber;

    public ResponseDTO(String result){
        this.result = result;
    }


    public ResponseDTO(String result, String telephoneNumber){
        this.result = result;
        this.telephoneNumber = telephoneNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
