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
import java.util.stream.Collectors;


@CrossOrigin(origins = "*")
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
        System.out.println(session.getId());

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
    public ResponseEntity<List<ServiceDTO>> getAllByPeriod(WebSession session,@PathVariable("period") String period) {

        List<ServiceDTO> collect =
                serviceRepository.findByUser(storedService.findById(session.getId()))
                        .toStream()
                        .filter(it -> it.getWhen().substring(0, 7).equals(period))
                        .map(Convert::toDTO)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(collect);
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
    public ResponseEntity<Resource> downloadPDFFile(@PathVariable("period") String period) {

        byte[] body = serviceManager.createpdf(period);

        ByteArrayResource stream = new ByteArrayResource(body);

        return new ResponseEntity(stream, HttpStatus.OK);
    }

//!!Error do poprawy
    @GetMapping("/api/services/clients")
    public ResponseEntity<List<String>> listClients(WebSession session) {

        List<String> clientsList = new ArrayList<>();
        serviceRepository.findByUser(storedService.findById(session.getId()))
                .toStream()
                .map(Convert::toDTO)
                .forEach(item -> clientsList.add(item.getWho()));
        List<String> collect = clientsList.stream().sorted().distinct().collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

}
