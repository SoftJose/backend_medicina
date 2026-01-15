package saitel.medicina.reportes;

import org.springframework.stereotype.Service;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.FichaSocial;
import saitel.medicina.entity.Inmunizaciones;
import com.itextpdf.kernel.colors.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.io.font.constants.StandardFonts;
import saitel.medicina.dto.DocumentoBase64Dto;

@Service
@RequiredArgsConstructor
public class HCU083InmunizacionesReporte {
        DeviceRgb gris_claro = new DeviceRgb(143, 140, 140);
        DeviceRgb lila = new DeviceRgb(204, 204, 255);
        DeviceRgb verde = new DeviceRgb(204, 255, 204);
        DeviceRgb celeste = new DeviceRgb(204, 255, 255);
        float bordeGrosor = 1.5f;
        SolidBorder bordeExterno = new SolidBorder(gris_claro, bordeGrosor);

        // Método auxiliar para crear celdas con formato
        private Cell createCell(String texto, boolean bold, Color bgColor,
                        TextAlignment alignment) {
                Paragraph p = new Paragraph(texto);
                p.setFontSize(8);
                if (bold) {
                        try {
                                p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
                Cell cell = new Cell().add(p);
                cell.setPadding(2f);
                if (bgColor != null)
                        cell.setBackgroundColor(bgColor);
                if (alignment != null)
                        cell.setTextAlignment(alignment);
                cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                return cell;
        }

        // Método para dibujar el pie de página
        private void drawFooter(PdfDocumentEvent docEvent) {
                PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage());
                Rectangle pageSize = docEvent.getPage().getPageSize();
                float y = pageSize.getBottom() + 15;
                try {
                        Canvas canvas = new Canvas(pdfCanvas, pageSize);
                        canvas.showTextAligned(new Paragraph("SNS-MSP / Form. CERT. 083 / 2019")
                                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                        .setFontSize(7), pageSize.getLeft() + 20, y, TextAlignment.LEFT);
                        canvas.showTextAligned(new Paragraph("REGISTRO DE INMUNIZACIONES PARA SALUD EN EL TRABAJO")
                                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                        .setFontSize(7), pageSize.getRight() - 20, y, TextAlignment.RIGHT);
                        canvas.close();
                } catch (java.io.IOException e) {
                        e.printStackTrace();
                }
        }

        public DocumentoBase64Dto generarPdf(DatosEmpleados empleado, FichaSocial ficha,
                        List<Inmunizaciones> inmunizaciones) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        PdfWriter writer = new PdfWriter(baos);
                        PdfDocument pdf = new PdfDocument(writer);
                        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, (IEventHandler) event -> {
                                drawFooter((PdfDocumentEvent) event);
                        });
                        Document document = new Document(pdf, PageSize.A4);
                        document.setMargins(20, 10, 20, 10); // margen inferior aumentado
                        System.out.println("Cantidad de inmunizaciones: "
                                        + (inmunizaciones != null ? inmunizaciones.size() : "null"));
                        // ---- TÍTULO PRINCIPAL ----
                        Paragraph tituloPrincipal = new Paragraph(
                                        "HCU - 083\nREGISTRO DE INMUNIZACIONES PARA SALUD EN EL TRABAJO")
                                        .setFontSize(14)
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                        .setMarginBottom(15);
                        document.add(new Paragraph("\n"));
                        document.add(tituloPrincipal);
        // A. DATOS DEL ESTABLECIMIENTO
{
        float[] anchoColsA = {80f, 80f, 80f, 80f, 80f, 80f};
        Table tablaEmpleado = new Table(anchoColsA).setWidth(PageSize.A4.getWidth() - 20f);
        Cell tituloAcell = new Cell(1, anchoColsA.length)
                .add(new Paragraph("A. DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO")
                        .setFontSize(9)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
                .setBackgroundColor(lila)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(2f)
                .setBorder(bordeExterno);
        tablaEmpleado.addCell(tituloAcell);
        tablaEmpleado.addCell(createCell("INSTITUCIÓN DEL SISTEMA O NOMBRE DE LA EMPRESA", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("RUC", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("CIIU", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("ESTABLECIMIENTO DE SALUD", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("NÚMERO DE HISTORIA CLÍNICA", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("NÚMERO DE ARCHIVO", false, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("SAITEL - " + empleado.getSucursal(), false, null, TextAlignment.LEFT));
        tablaEmpleado.addCell(createCell("1091728857001", false, null, TextAlignment.LEFT));
        tablaEmpleado.addCell(createCell("J619.04", false, null, TextAlignment.LEFT));
        tablaEmpleado.addCell(createCell("DEPARTAMENTO MEDICO PRIVADO - SAITEL", false, null, TextAlignment.LEFT));
        tablaEmpleado.addCell(createCell(empleado != null ? empleado.getCedula(): "", false, null, TextAlignment.LEFT));
        tablaEmpleado.addCell(createCell("00 - ", false, null, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("PRIMER APELLIDO", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("SEGUNDO APELLIDO", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("PRIMER NOMBRE", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("SEGUNDO NOMBRE", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("SEXO", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell("CARGO / OCUPACIÓN", true, verde, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell(empleado.getPrimerApellido(), false, null, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell(empleado.getSegundoApellido(), false, null, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell(empleado.getPrimerNombre(), false, null, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell(empleado.getSegundoNombre(), false, null, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell(empleado.getSexo(), false, null, TextAlignment.CENTER));
        tablaEmpleado.addCell(createCell(empleado.getCargo(), false, null, TextAlignment.LEFT));
        document.add(tablaEmpleado);
        document.add(new Paragraph("\n"));
}
        // B. INMUNIZACIONES
{
        float[] anchoColsB = { 70f, 30f, 55f, 40f, 55f, 45f, 40f, 60f }; // Ajustar para que quepa mejor
        Table tablaInmu = new Table(anchoColsB).setWidth(PageSize.A4.getWidth() - 20f);
        Cell tituloBcell = new Cell(1, anchoColsB.length)
                .add(new Paragraph("B. INMUNIZACIONES")
                .setFontSize(9)
                .setFont(PdfFontFactory
                .createFont(StandardFonts.HELVETICA_BOLD)))
                .setBackgroundColor(lila)
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(bordeExterno)
                .setPadding(2f);
        tablaInmu.addCell(tituloBcell);
        String[] headers = { "VACUNAS", "DOSIS", "FECHA (aaaa/mm/dd)", "LOTE",
                "ESQUEMA COMPLETO (marcar X)","NOMBRES COMPLETOS DEL RESPONSABLE DE LA VACUNACION",
                "ESTABLECIMIENTO DE SALUD DONDE SE COLOCÓ LA VACUNA", "OBSERVACIONES" };
        for (int i = 0; i < headers.length; i++) {
                Cell headerCell = createCell(headers[i], true, i == 0 ? celeste : verde,TextAlignment.CENTER);
                        headerCell.setFontSize(9);
                        headerCell.setPadding(3);
                        headerCell.setTextAlignment(TextAlignment.CENTER);
                        headerCell.setBackgroundColor(i == 0 ? celeste : verde);
         tablaInmu.addCell(headerCell);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        // Definir tipos fijos explícitos
                List<String> tiposFijosNombres = java.util.Arrays.asList(
                "Tétanos - Difteria","Hepatitis A","Hepatitis B","Influenza estacional",
                        "Sarampión - Rubéola","Fiebre amarilla","COVID");
                // Agrupar inmunizaciones por nombre de tipo
                        java.util.Map<String, java.util.List<Inmunizaciones>> inmunizacionesPorTipo = new java.util.HashMap<>();
                        if (inmunizaciones != null) {
                                for (Inmunizaciones i : inmunizaciones) {
                                        String nombreTipo = i.getIdTipoInmunizacion().getNombreInmunizacion();
                                        inmunizacionesPorTipo
                                        .computeIfAbsent(nombreTipo, k -> new java.util.ArrayList<>())
                                        .add(i);
                                }
                        }
        // Mostrar solo los tipos fijos arriba
                for (String nombreFijo : tiposFijosNombres) {
                java.util.List<Inmunizaciones> lista = inmunizacionesPorTipo.get(nombreFijo);
                if (lista != null && !lista.isEmpty()) {
                for (int i = 0; i < lista.size(); i++) {
                        Inmunizaciones registro = lista.get(i);
                        if (i == 0) {
                // Celda con rowspan para el tipo de vacuna
                        Cell cellTipo = new Cell(lista.size(), 1)
                        .add(new Paragraph(nombreFijo))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setFontSize(9)
                        .setPadding(2)
                        .setBackgroundColor(celeste);
        tablaInmu.addCell(cellTipo);
                        }
        // Las demás celdas de la fila
                tablaInmu.addCell(new Cell().add(new Paragraph(registro.getDosis()))
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                        .setPadding(2));
                tablaInmu.addCell(new Cell()
                        .add(new Paragraph(registro.getFecha() != null? registro.getFecha().format(formatter): ""))
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                        .setPadding(2));
                tablaInmu.addCell(new Cell().add(new Paragraph(registro.getLote() != null ? registro.getLote() : ""))
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                        .setPadding(2));
                tablaInmu.addCell(new Cell()
                        .add(new Paragraph(registro.getEsquemaCompleto() != null&& registro.getEsquemaCompleto() ? "X": ""))
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                        .setPadding(2));
                tablaInmu.addCell(new Cell().add(new Paragraph(registro.getResponsableVacunacion() != null? registro.getResponsableVacunacion(): ""))
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                        .setPadding(2));
                tablaInmu.addCell(new Cell().add(new Paragraph(registro.getEstablecimientoSalud() != null? registro.getEstablecimientoSalud(): ""))
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                        .setPadding(2));
                tablaInmu.addCell(new Cell()
                        .add(new Paragraph(registro.getObservaciones() != null? registro.getObservaciones(): ""))
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                        .setPadding(2));
        }} else {
        // Si no hay dosis para este tipo, muestra una fila en blanco con el nombre
                tablaInmu.addCell(new Cell().add(new Paragraph(nombreFijo))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(9).setPadding(2)
                        .setBackgroundColor(celeste));
                for (int j = 1; j < headers.length; j++) {
                tablaInmu.addCell(new Cell().add(new Paragraph(""))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(9).setPadding(2));
                                        }
                                }
                        }
        // INMUNIZACIONES DE ACUERDO AL TIPO DE EMPRESA Y RIESGO ---
                        Cell seccionNuevos = new Cell(1, headers.length)
                                        .add(new Paragraph("INMUNIZACIONES DE ACUERDO AL TIPO DE EMPRESA Y RIESGO")
                                        .setFontSize(8))
                                        .setBackgroundColor(null)
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(8);
                        tablaInmu.addCell(seccionNuevos);
        // Agrupar nuevas inmunizaciones por tipo (no fijos)
                java.util.Map<String, List<Inmunizaciones>> nuevasPorTipo = new java.util.HashMap<>();
                for (String nombreTipo : inmunizacionesPorTipo.keySet()) {
                if (!tiposFijosNombres.stream().anyMatch(fijo -> fijo.equalsIgnoreCase(nombreTipo))) {
                        nuevasPorTipo.put(nombreTipo, inmunizacionesPorTipo.get(nombreTipo));
                                }
                }
                if (!nuevasPorTipo.isEmpty()) {
                for (String nombreTipo : nuevasPorTipo.keySet()) {
                List<Inmunizaciones> lista = nuevasPorTipo.get(nombreTipo);
                for (int i = 0; i < lista.size(); i++) {
                        Inmunizaciones registro = lista.get(i);
                        if (i == 0) {
        // Celda con rowspan para el nuevo tipo de vacuna
                Cell cellTipo = new Cell(lista.size(), 1)
                        .add(new Paragraph(nombreTipo))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setFontSize(9)
                        .setPadding(2)
                        .setBackgroundColor(celeste);
        tablaInmu.addCell(cellTipo);
        }
        // Las demás celdas de la fila
        tablaInmu.addCell(new Cell().add(new Paragraph(registro.getDosis()))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                .setPadding(2));
        tablaInmu.addCell(new Cell()
                .add(new Paragraph(registro.getFecha() != null? registro.getFecha().format(formatter): ""))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                .setPadding(2));
        tablaInmu.addCell(new Cell().add(new Paragraph(registro.getLote() != null ? registro.getLote() : ""))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                .setPadding(2));
        tablaInmu.addCell(new Cell()
                .add(new Paragraph(registro.getEsquemaCompleto() != null && registro.getEsquemaCompleto() ? "X": ""))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                .setPadding(2));
        tablaInmu.addCell(new Cell().add(new Paragraph(
                registro.getResponsableVacunacion() != null? registro.getResponsableVacunacion(): ""))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                .setPadding(2));
        tablaInmu.addCell(new Cell().add(
                new Paragraph(registro.getEstablecimientoSalud() != null? registro.getEstablecimientoSalud(): ""))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                .setPadding(2));
        tablaInmu.addCell(new Cell()
                .add(new Paragraph(registro.getObservaciones() != null? registro.getObservaciones(): ""))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9)
                .setPadding(2));
                                        }
                                }
                        } else {
                                // Si no hay nuevas, muestra una fila en blanco con N/A y ancho de 30f
                                tablaInmu.addCell(new Cell().add(new Paragraph("N/A"))
                                                .setTextAlignment(TextAlignment.CENTER)
                                                .setFontSize(9).setPadding(2)
                                                .setHeight(30f)
                                                .setBackgroundColor(celeste));
                                for (int j = 1; j < headers.length; j++) {
                                        tablaInmu.addCell(new Cell().add(new Paragraph(""))
                                                        .setTextAlignment(TextAlignment.CENTER)
                                                        .setFontSize(9).setPadding(2)
                                                        .setHeight(30f));
                                }
                        }
                        // Celda informativa al final
                        Cell infoCell = new Cell(1, headers.length)
                                        .add(new Paragraph("La vacuna contra la Fiebre Amarilla es obligatorio para quien viva o se desplace en la Región Amazónica, su aplicación es hasta los 59 años de edad."))
                                        .setBackgroundColor(null)
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(9);
                        tablaInmu.addCell(infoCell);
                        document.add(tablaInmu);
}
                        document.close();
                        // Guardar en carpeta Descargas
                        String userHome = System.getProperty("user.home");
                        String baseName = "HCU083EvaluacionInmunizaciones_" + empleado.getIdEmpleado();
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
                                System.out.println("PDF de inmunizaciones guardado exitosamente en " + rutaDescargas);
                        } catch (Exception ex) {
                                System.out.println("Error al guardar el PDF de inmunizaciones: " + ex.getMessage());
                        }
                        String base64Pdf = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
                        return new DocumentoBase64Dto(base64Pdf);

                } catch (Exception e) {
                        System.out.println("Error al generar el PDF de receta médica: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                }
        }
}
