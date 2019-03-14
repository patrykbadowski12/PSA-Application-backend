package net.respekto.psawebapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "serviceDTO")
public class ServiceDbModel implements Comparable<ServiceDbModel>{
    @Id
    private String id;
    private String who;
    private String serviceType;
    private String description;
    private String when;
    private long distance;
    private String user;

    @Override
    public int compareTo(ServiceDbModel o) {
        return this.who.hashCode() - o.getWho().hashCode();
    }
}
