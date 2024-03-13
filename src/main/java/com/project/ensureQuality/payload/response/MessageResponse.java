package com.project.ensureQuality.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {

    private String EM;
    private int EC;



    public MessageResponse(String EM, int EC) {
        this.EM = EM;
        this.EC = EC;
    }
}
