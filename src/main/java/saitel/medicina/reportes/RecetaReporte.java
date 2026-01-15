package saitel.medicina.reportes;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saitel.medicina.entity.RecetasEnviada;
import saitel.medicina.service.ImpLog.RecetaServiceImpLog;
import com.itextpdf.io.font.constants.StandardFonts;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import saitel.medicina.dto.DocumentoBase64Dto;


@Service
@RequiredArgsConstructor
public class RecetaReporte {
    private final saitel.medicina.service.ImpLog.DatosEmpleadoServiceImpLog datosEmpleadoServiceImpLog;
    private final RecetaServiceImpLog recetaServiceImpLog;
    public DocumentoBase64Dto generarReceta(Integer idEvaluacion) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            PdfFont boldFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            Paragraph titulo = new Paragraph("RECETA MÉDICA")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10f);
            document.add(titulo);

    List<RecetasEnviada> recetas = recetaServiceImpLog.findByIdEvaluacion(idEvaluacion);

            String cedulaPaciente = "";
            String nombrePaciente = "";
            if (recetas.isEmpty()) {
                document.add(new Paragraph("No se encontró ninguna receta para esta evaluación."));
            } else {
                RecetasEnviada receta = recetas.get(0);
                // Marcar la receta como impresa
                receta.setImpresa(true);
                recetaServiceImpLog.guardar(receta);
                try {
                    saitel.medicina.entity.Evaluacion evaluacion = receta.getIdEvaluacion();
                    saitel.medicina.entity.FichaSocial fichaSocial = evaluacion.getFichaSocial();
                    Integer idEmpleado = fichaSocial.getIdEmpleado();
                    saitel.medicina.entity.DatosEmpleados empleado = datosEmpleadoServiceImpLog.obtenerPorId(idEmpleado).orElse(null);
                    if (empleado != null) {
                        nombrePaciente = empleado.getNombre() + " " + empleado.getApellido();
                        cedulaPaciente = empleado.getCedula();
                    }
                } catch (Exception ex) {
                    System.out.println("Error obteniendo datos del paciente: " + ex.getMessage());
                }

                Paragraph nombreParrafo = new Paragraph("Nombre del paciente: " + (nombrePaciente != null ? nombrePaciente : ""))
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(2f);
                document.add(nombreParrafo);
                Paragraph cedulaParrafo = new Paragraph("Identificación: " + (cedulaPaciente != null ? cedulaPaciente : ""))
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(10f);
                document.add(cedulaParrafo);

                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2})).useAllAvailableWidth();
                PdfFont cellBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                DeviceRgb bgColor = new DeviceRgb(173, 216, 230);

                table.addCell(createCell("Número de receta", cellBold, bgColor));
                table.addCell(createCell(receta.getNumeroReceta(), null, null));

                table.addCell(createCell("Fecha", cellBold, bgColor));
                table.addCell(createCell(receta.getFecha().toString(), null, null));

                table.addCell(createCell("Doctor/a", cellBold, bgColor));
                table.addCell(createCell(receta.getDoctorA(), null, null));

                table.addCell(createCell("Diagnóstico", cellBold, bgColor));
                table.addCell(createCell(receta.getDiagnostico(), null, null));

                table.addCell(createCell("Receta", cellBold, bgColor));
                table.addCell(createCell(receta.getReceta(), null, null));

                table.addCell(createCell("Indicaciones", cellBold, bgColor));
                table.addCell(createCell(receta.getIndicaciones(), null, null));

                document.add(table);
            }

            document.close();

            // Guardar en carpeta Descargas
            String userHome = System.getProperty("user.home");
            String baseName = "receta_" + (cedulaPaciente != null ? cedulaPaciente : "");
            String extension = ".pdf";
            String downloadsDir = userHome + "/Downloads/";
            String rutaDescargas = downloadsDir + baseName + extension;
            int contador = 1;
            java.io.File archivo = new java.io.File(rutaDescargas);
            while (archivo.exists()) {
                rutaDescargas = downloadsDir + baseName + "(" + contador + ")" + extension;
                archivo = new java.io.File(rutaDescargas);
                contador++;
            }
                try (FileOutputStream fos = new FileOutputStream(rutaDescargas)) {
                fos.write(baos.toByteArray());
                System.out.println("Receta médica guardada exitosamente en " + rutaDescargas);
            } catch (Exception ex) {
                System.out.println("Error al guardar el PDF de receta médica: " + ex.getMessage());
            }
            // Devolver el PDF en base64
            String base64Pdf = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
            return new DocumentoBase64Dto(base64Pdf);

        } catch (Exception e) {
            System.out.println("Error al generar el PDF de receta médica: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Cell createCell(String text, PdfFont font, DeviceRgb bgColor) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(font).setFontSize(12));
        if (bgColor != null) cell.setBackgroundColor(bgColor);
        return cell;
    }
}
