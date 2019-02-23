package net.respekto.psawebapi;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

@Service
public class ServiceManager {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    StoredService storedService;

    public byte[] createpdf(String period, WebSession session) {


        byte[] bytesFile = new byte[0];

        try {
            File file = File.createTempFile("pdf", ".pdf");
            FileOutputStream fileout = new FileOutputStream(file);
            Document document = new Document();
            PdfWriter.getInstance(document, fileout);
            document.open();

            serviceRepository.findByUser(storedService.findById(session.getId()))
                    .reduce(new ArrayList<String>(), (acc, it) -> {
                        System.out.println(storedService.findById(session.getId()));
                        acc.add(it.getServiceType());
                        System.out.println(it.getServiceType());

                        Paragraph date = new Paragraph(period);
                        date.setAlignment(Element.ALIGN_CENTER);
                        try {
                            document.add(date);
                            Paragraph client = new Paragraph(it.getWho());
                            client.setAlignment(Element.ALIGN_CENTER);
                            document.add(client);
                            document.add(new Phrase("\n"));

                            PdfPTable table = new PdfPTable(4);
                            table.setWidths(new int[]{1, 1, 1, 1});

                            table.addCell("Service Type");
                            table.addCell("Description");
                            table.addCell("Date");
                            table.addCell("Distance");

                            Long totalDistance = Long.valueOf(0);

                            document.add(table);
                            document.add(new Paragraph("Total distance: " + String.valueOf(totalDistance)));
                            document.newPage();


                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }

                        return acc;
                    }).then(
            ).subscribe();

            bytesFile = Files.readAllBytes(file.toPath());

            file.delete();
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return bytesFile;


    }


}