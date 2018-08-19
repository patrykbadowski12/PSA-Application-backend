package net.respekto.psawebapi;

import lombok.val;

import java.util.UUID;

public class Given {
    private static Integer distance =1;
    public static ServiceDTO createFullModel(){
        val dto = new ServiceDTO();
        dto.setWho("who " + UUID.randomUUID().toString());
        dto.setServiceType("serviceType "+ UUID.randomUUID().toString());
        dto.setDescription("description "+ UUID.randomUUID().toString());
        dto.setWhen("2000-13");
        dto.setDistance(distance++);
        return dto;
    }
}
