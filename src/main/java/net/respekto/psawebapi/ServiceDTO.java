package net.respekto.psawebapi;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class ServiceDTO {
    private String id;

    @NotNull
    @Size(min=10)
    private String who;

    @NotNull
    @Size(min=5)
    private String serviceType;

    @NotNull
    @Size(min=5)
    private String description;

    @NotNull
    @Size(min=7, max = 10)
    private String when;

    @Min(0)
    private long distance;

    private String user;
}
