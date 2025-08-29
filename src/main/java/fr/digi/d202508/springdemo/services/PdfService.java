package fr.digi.d202508.springdemo.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.digi.d202508.springdemo.dtos.CityDto;
import fr.digi.d202508.springdemo.dtos.DepartmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CityService cityService;

    /**
     * Génère un PDF avec les informations d'un département et ses villes
     * @param departmentCode code du département
     * @return les bytes du PDF généré
     * @throws Exception si erreur lors de la génération
     */
    public byte[] generateDepartmentPdf(String departmentCode) throws Exception {
        // Récupération des données
        DepartmentDto department = departmentService.getDepartmentByCode(departmentCode);
        List<CityDto> cities = cityService.getCitiesByDepartmentCodeAndMinPopulation(departmentCode, 0);

        // Création du document PDF
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Police pour le titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Titre du document
        Paragraph title = new Paragraph("Département " + department.getName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Informations du département
        Paragraph deptInfo = new Paragraph();
        deptInfo.add(new Chunk("Code du département : ", headerFont));
        deptInfo.add(new Chunk(department.getCode() + "\n", normalFont));
        deptInfo.add(new Chunk("Nom du département : ", headerFont));
        deptInfo.add(new Chunk(department.getName() + "\n", normalFont));
        deptInfo.setSpacingAfter(20);
        document.add(deptInfo);

        // Titre de la section villes
        Paragraph citiesTitle = new Paragraph("Liste des villes", headerFont);
        citiesTitle.setSpacingAfter(10);
        document.add(citiesTitle);

        // Tableau des villes
        if (!cities.isEmpty()) {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1f});

            // En-têtes du tableau
            PdfPCell headerName = new PdfPCell(new Phrase("Nom de la ville", headerFont));
            headerName.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerName.setPadding(8);
            table.addCell(headerName);

            PdfPCell headerPopulation = new PdfPCell(new Phrase("Population", headerFont));
            headerPopulation.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerPopulation.setPadding(8);
            table.addCell(headerPopulation);

            // Données des villes
            for (CityDto city : cities) {
                PdfPCell nameCell = new PdfPCell(new Phrase(city.getName(), normalFont));
                nameCell.setPadding(5);
                table.addCell(nameCell);

                PdfPCell populationCell = new PdfPCell(new Phrase(String.format("%,d", city.getPopulation()), normalFont));
                populationCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                populationCell.setPadding(5);
                table.addCell(populationCell);
            }

            document.add(table);
        } else {
            Paragraph noCities = new Paragraph("Aucune ville trouvée pour ce département.", normalFont);
            document.add(noCities);
        }

        document.close();
        return outputStream.toByteArray();
    }
}