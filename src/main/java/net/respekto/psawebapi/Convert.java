package net.respekto.psawebapi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Convert {

   static  ServiceDTO toDTO(ServiceDbModel it) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(it.getId());
        dto.setDescription(it.getDescription());
        dto.setDistance(it.getDistance());
        dto.setWho(it.getWho());
        dto.setWhen(it.getWhen());
        dto.setServiceType(it.getServiceType());
        dto.setUser(it.getUser());
        return dto;
    }
}
