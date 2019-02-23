package net.respekto.psawebapi;

import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    @Autowired
    ServiceManager serviceManager;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void shouldStoreService() {
        //given
        val expected = Given.createFullModel();
        testRestTemplate.postForLocation("/api/services", expected);

        //when
        val actual = testRestTemplate.getForEntity("/api/services/search/2000-13", ServiceDTO[].class);

        //then
        Assertions.assertThat(actual.getBody()).contains(expected);

    }


    @Test
    public void shouldNotStoreService() {
        //given
        val expected = new ServiceDTO("", "Patryk", "Database", "creating tables","1932-12-03",5, "patryk_badowski@o2.pl");
        testRestTemplate.postForLocation("/api/services", expected);

        //when
        val actual = testRestTemplate.getForEntity("/api/services/search/1932-12", ServiceDTO[].class);

        //then
        Assertions.assertThat(actual.getBody()).doesNotContain(expected);

    }

    @Test
    public void shouldDeleteService() {
        //given
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/services", new ServiceDTO("", "Maciek and his Company", "computer services", "selling a computer", "1990-10-21", 6,""), String.class);
        testRestTemplate.delete("/api/services/" + response.getBody());

        //when
        val actual = testRestTemplate.getForEntity("/api/services/search/1990-10", ServiceDTO[].class);

        //then
        Assertions.assertThat(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void shouldPutService() {
        //given
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/services", new ServiceDTO("", "Patrykcorpo", "different services", "cooking a dinner", "2000-01", 7,""), String.class);
        testRestTemplate.put("/api/services/" + response.getBody(), new ServiceDTO("", "Patrykcorpo", "programming", "api and REST", "1994-04", 15,""));

        // when
        val actual = testRestTemplate.getForEntity("/api/services/search/1994-04", ServiceDTO[].class);

        //then
        ServiceDTO expected = new ServiceDTO("", "Patrykcorpo", "programming", "api and REST", "1994-04", 15,"");

        Assertions.assertThat(actual.getBody()).contains(expected);

    }

    @Test
    public void shouldNotPutService() {
        //given
        testRestTemplate.put("/api/services/asdasdasdasd", new ServiceDTO("", "Patryk", "Car", "engine oil", "1856-10-03", 3,""));

        //when
        val actual = testRestTemplate.getForEntity("/api/services/search/1856-10", ServiceDTO[].class);

        //then
        Assertions.assertThat(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void testingPDF() throws Exception {

        //given
        ServiceDTO serviceDTO = new ServiceDTO("", "Patryk CORPORATION", "Auto salon", "engine oil", "1995-01", 3,"");
        testRestTemplate.postForLocation("/api/services", serviceDTO);


        byte[] forObject = testRestTemplate.getForObject("/api/services/generatePdf/1995-01", byte[].class);
        //when

        PDDocument document = PDDocument.load(forObject);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = "";
        text = stripper.getText(document);

        //then
        Assertions.assertThat(text).contains(serviceDTO.getWho());
        Assertions.assertThat(text).contains(serviceDTO.getDescription());
        Assertions.assertThat(text).contains(serviceDTO.getServiceType());
        Assertions.assertThat(text).contains(serviceDTO.getWhen());
    }

    @Test
    public void shouldContainsClientName() {
        //given
        ServiceDTO serviceDTO = new ServiceDTO("", "Patryk CORPORATION", "Auto salon and service", "engine oil", "1995-01", 3,"");

        //when
        val body = testRestTemplate.getForEntity("/api/services/clients", String[].class).getBody();
        //then
        Assertions.assertThat(body).contains(serviceDTO.getWho());
    }

    @Test
    public void shouldContainsAllObjects() {

        //given
        ServiceDTO object1 = Given.createFullModel();
        testRestTemplate.postForLocation("/api/services", object1);
        ServiceDTO object2 = Given.createFullModel();
        testRestTemplate.postForLocation("/api/services", object2);
        ServiceDTO object3 = Given.createFullModel();
        testRestTemplate.postForLocation("/api/services", object3);

        //when
        val body = testRestTemplate.getForEntity("/api/services", ServiceDTO[].class).getBody();

        //then
        Assertions.assertThat(body).contains(object1).contains(object2).contains(object3);
    }

}

