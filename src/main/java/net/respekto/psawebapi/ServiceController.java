package net.respekto.psawebapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@CrossOrigin(origins = "*", allowCredentials = "true")
@RestController
public class ServiceController {

    @Autowired
    ServiceManager serviceManager;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    StoredService storedService;


    @PostMapping("/api/session")
    public String session(WebSession session, @RequestBody String user){
        if(!session.isStarted()){
                session.start();
        }
        storedService.saveUser(session.getId(), user);
        String userByKey = storedService.findById(session.getId());
        System.out.println(userByKey);


        return session.getId();
    }


    @PostMapping("/api/services")
    public ResponseEntity<Mono<String>> createService(WebSession session, @Valid @RequestBody ServiceDTO serviceDTO) {

        ServiceDbModel newEntry = new ServiceDbModel();
        newEntry.setId(UUID.randomUUID().toString());
        newEntry.setDescription(serviceDTO.getDescription());
        newEntry.setDistance(serviceDTO.getDistance());
        newEntry.setServiceType(serviceDTO.getServiceType());
        newEntry.setWhen(serviceDTO.getWhen());
        newEntry.setWho(serviceDTO.getWho());
        newEntry.setUser(storedService.findById(session.getId()));
        Mono<ServiceDTO> result = serviceRepository.save(newEntry)
                .map(Convert::toDTO);

        return ResponseEntity.ok(result.map(it -> it.getId()));
    }

    @GetMapping("/api/services")
    public ResponseEntity<Flux<ServiceDTO>> findAll(WebSession session) {

        Flux<ServiceDTO> result = serviceRepository.findByUser(storedService.findById(session.getId()))
                .map(Convert::toDTO);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/api/services/search/{period}")
    public Mono<ResponseEntity<List<ServiceDTO>>> getAllByPeriod(WebSession session,@PathVariable("period") String period) {


            return serviceRepository
                .findByUser(storedService.findById(session.getId()))
                .map(Convert::toDTO)
                .reduce(new ArrayList<ServiceDTO>(), (acc,v)->{
                    if(v.getWhen().substring(0,7).equals(period)) {
                        acc.add(v);
                    }
                    return acc; })
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/api/services/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return serviceRepository.findById(id)
                .flatMap(existingService -> serviceRepository.delete(existingService)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/api/services/{id}")
    public Mono<ResponseEntity> put(@PathVariable("id") String id, @RequestBody ServiceDTO serviceDTO) {
        return serviceRepository.findById(id)
                .flatMap(existingService -> {
                    existingService.setDescription(serviceDTO.getDescription());
                    existingService.setWhen(serviceDTO.getWhen());
                    existingService.setServiceType(serviceDTO.getServiceType());
                    existingService.setDistance(serviceDTO.getDistance());
                    return serviceRepository.save(existingService);
                })
                .map(updatedService -> new ResponseEntity(HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/api/services/generatePdf/{period}", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Resource> downloadPDFFile(@PathVariable("period") String period, WebSession session) {

        byte[] body = serviceManager.createpdf(period, session);

        ByteArrayResource stream = new ByteArrayResource(body);

        return new ResponseEntity(stream, HttpStatus.OK);
    }

    @GetMapping("/api/services/clients")
    public Mono<ResponseEntity<List<String>>> listClients(WebSession session) {

        return serviceRepository.findByUser(storedService.findById(session.getId()))
                .map(Convert::toDTO)
                .reduce(new ArrayList<String>(), (acc, v) -> {
                    if(!acc.contains(v.getWho())){
                        acc.add(v.getWho());
                    }
                    return acc;
                })
                .map(ResponseEntity::ok);
    }

    @GetMapping("/api/services/clients2")
    public Flux<ServiceDbModel> getAllByPeriod2(WebSession session) {


        return serviceRepository.findAll();

    }
}
