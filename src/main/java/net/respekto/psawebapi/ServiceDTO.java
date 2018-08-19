package net.respekto.psawebapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class ServiceDTO {
    private String id;
    private String who;
    private String serviceType;
    private String description;
    private String when;
    private long distance;
}
