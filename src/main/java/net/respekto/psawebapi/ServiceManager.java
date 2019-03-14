package net.respekto.psawebapi;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ServiceManager {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    StoredService storedService;

    @SneakyThrows
    public Mono<byte[]> createPdf(String period, WebSession session) {
        ArrayList<String> dupa = new ArrayList<>();

        File file = File.createTempFile("pdf", ".pdf");
        FileOutputStream fileout = new FileOutputStream(file);
        Document document = new Document();
        PdfWriter.getInstance(document, fileout);
        document.open();
        AtomicReference<String> temp = new AtomicReference<>("");

        PdfPTable table = new PdfPTable(4);
        Paragraph date = new Paragraph(period);

        return serviceRepository.findByUser(storedService.findById(session.getId()))
                .sort()
                .map(serviceDbModel -> {
                    try {
                        if (serviceDbModel.getWhen().substring(0, 7).equals(period)) {
                            if (!serviceDbModel.getWho().equals(temp.toString())) {

                                document.add(table);
                                table.flushContent();
                                temp.set(serviceDbModel.getWho());
                                document.newPage();
                                document.add(date);
                                date.setAlignment(Element.ALIGN_CENTER);
                                Paragraph client = new Paragraph(serviceDbModel.getWho());
                                client.setAlignment(Element.ALIGN_CENTER);
                                document.add(client);
                                document.add(new Phrase("\n"));
                                table.setWidths(new int[]{1, 1, 1, 1});

                                table.addCell("Service Type");
                                table.addCell("Description");
                                table.addCell("Date");
                                table.addCell("Distance");
                            }

                            table.addCell(serviceDbModel.getServiceType());
                            table.addCell(serviceDbModel.getDescription());
                            table.addCell(serviceDbModel.getWhen());
                            table.addCell(String.valueOf(serviceDbModel.getDistance()));
                        }

                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }

                    return serviceDbModel;
                })
                .reduce(new ArrayList<ServiceDbModel>(), (acc, it) -> {
                    return acc;
                })
                .map(ignored -> {
                    try {
                        document.add(table);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    document.close();

                    byte[] bytesFile = new byte[0];
                    try {
                        bytesFile = Files.readAllBytes(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    file.delete();

                    return bytesFile;
                });
    }
}