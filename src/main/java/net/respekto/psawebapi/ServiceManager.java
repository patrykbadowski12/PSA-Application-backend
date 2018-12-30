package net.respekto.psawebapi;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceManager {

    @Autowired
    ServiceRepository serviceRepository;

    public byte[] createpdf(String period) {
        
        byte[] bytesFile = new byte[0];

        List<String> listWho = new ArrayList<>();

        serviceRepository.findAll()
                            .toStream()
                            .filter(it -> it.getWhen().substring(0, 7).equals(period))
                            .forEach(item -> {
                                 listWho.add(item.getWho());
                            });

        List<String> sortedClients = listWho.stream()
                .sorted()
                .distinct()
                .collect(Collectors.toList());

        try {
            File file = File.createTempFile("pdf", ".pdf");
            FileOutputStream fileout = new FileOutputStream(file);
            Document document = new Document();
            PdfWriter.getInstance(document, fileout);
            document.open();

            for (String x : sortedClients) {
                Paragraph date = new Paragraph(period);
                date.setAlignment(Element.ALIGN_CENTER);
                document.add(date);

                Paragraph client = new Paragraph(x);
                client.setAlignment(Element.ALIGN_CENTER);
                document.add(client);
                document.add(new Phrase("\n"));

                List<ServiceDbModel> collectByWho = serviceRepository.findByWho(x)
                        .toStream()
                        .collect(Collectors.toList());

                PdfPTable table = new PdfPTable(4);
                table.setWidths(new int[]{1, 1, 1, 1});

                table.addCell("Service Type");
                table.addCell("Description");
                table.addCell("Date");
                table.addCell("Distance");

                Long totalDistance = Long.valueOf(0);

                for (ServiceDbModel it : collectByWho)
                    if (it.getWhen().substring(0, 7).equals(period)) {
                        table.addCell(it.getServiceType());
                        table.addCell(it.getDescription());
                        table.addCell(it.getWhen());
                        table.addCell(String.valueOf(it.getDistance()));
                        totalDistance += it.getDistance();
                    }
                document.add(table);
                document.add(new Paragraph("Total distance: " + String.valueOf(totalDistance)));
                document.newPage();
            }
            document.close();
            bytesFile = Files.readAllBytes(file.toPath());
            //
            file.delete();

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
