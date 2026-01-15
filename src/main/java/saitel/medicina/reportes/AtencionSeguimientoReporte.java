package saitel.medicina.reportes;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import org.springframework.stereotype.Service;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.AtencionSeguimientoPsicologico;
import saitel.medicina.entity.HistoriaClinicaPsicologica;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import saitel.medicina.dto.DocumentoBase64Dto;

@Service
public class AtencionSeguimientoReporte {
    DeviceRgb verde = new DeviceRgb(0xD0, 0xF2, 0xDB);

    private Cell createCell(String texto, boolean bold, com.itextpdf.kernel.colors.Color bgColor, int colspan,
            int rowspan, TextAlignment alignment) {
        Paragraph p = new Paragraph(texto);
        try {
            if (bold)
                p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
        } catch (java.io.IOException e) {
        }
        Cell cell = new Cell(rowspan, colspan).add(p);
        if (bgColor != null)
            cell.setBackgroundColor(bgColor);
        if (alignment != null)
            cell.setTextAlignment(alignment);
        cell.setFontSize(10);
        return cell;
    }

    @SuppressWarnings("unchecked")
    public DocumentoBase64Dto generarPdf(DatosEmpleados empleado, List<AtencionSeguimientoPsicologico> seguimientos)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(10, 10, 10, 10);
        for (int i = 0; i < seguimientos.size(); i++) {
            AtencionSeguimientoPsicologico seguimiento = seguimientos.get(i);
            HistoriaClinicaPsicologica historia = seguimiento.getHistoriaClinicaPsicologica();
            // Título principal
            Paragraph titulo = new Paragraph("ATENCIÓN Y SEGUIMIENTO PSICOSOCIAL")
                    .setFontSize(16)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(verde)
                    .setMarginBottom(5);
            document.add(titulo);
            // Tabla principal (igual que en generarPdf individual)
            float[] anchoCols = { 120f, 120f, 120f, 120f };
            Table tabla = new Table(anchoCols).setWidth(PageSize.A4.getWidth() - 20f);

            // Fila superior: Identificación y Motivo consulta
            tabla.addCell(createCell("Identificación paciente", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(empleado != null && empleado.getCedula() != null ? empleado.getCedula() : "",
                    false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Motivo consulta - Historia Clínica", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(
                    historia != null && historia.getMotivoConsulta() != null ? historia.getMotivoConsulta() : "", false,
                    null, 1, 1, TextAlignment.LEFT));

            // Primera fila: Atención, Psicopatología, Sesión
            tabla.addCell(createCell(
                    "Atención: " + (seguimiento.getTipoAtencion() != null ? seguimiento.getTipoAtencion() : ""), true,
                    null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Psicopatología", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getPsicopatologia() != null ? seguimiento.getPsicopatologia() : "",
                    false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Sesión: " + (seguimiento.getSesion() != null ? seguimiento.getSesion() : ""),
                    true, null, 1, 1, TextAlignment.LEFT));

            // Segunda fila: Código, Consumo A/S, Fecha, Riesgo social, Hora, Otro
            tabla.addCell(createCell("Código", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getCodigo() != null ? seguimiento.getCodigo() : "", false, null, 1, 1,
                    TextAlignment.LEFT));
            tabla.addCell(createCell("Consumo A/S", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getConsumoAs() != null ? seguimiento.getConsumoAs() : "", false, null,
                    1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Fecha", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getFechaAtencion() != null
                    ? seguimiento.getFechaAtencion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "", false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Riesgo social", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getRiesgoSocial() != null ? seguimiento.getRiesgoSocial() : "", false,
                    null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Hora", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(
                    createCell(seguimiento.getHoraAtencion() != null ? seguimiento.getHoraAtencion().toString() : "",
                            false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Otro", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getOtros() != null ? seguimiento.getOtros() : "", false, null, 2, 1,
                    TextAlignment.LEFT));

            // Temas tratados
            tabla.addCell(createCell("Temas tratados:", true, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getTemasTratados() != null ? seguimiento.getTemasTratados() : "",
                    false, null, 4, 1, TextAlignment.LEFT));

            // Reactivos/Test aplicados
            tabla.addCell(createCell("Reactivos/Test aplicados:", true, null, 4, 1, TextAlignment.LEFT));
            Object reactivos = seguimiento.getReactivosAplicados();
            if (reactivos != null) {
                if (reactivos instanceof String) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        if (((String) reactivos).trim().startsWith("[")) {
                            reactivos = mapper.readValue((String) reactivos, java.util.List.class);
                        } else {
                            reactivos = mapper.readValue((String) reactivos, java.util.Map.class);
                        }
                    } catch (Exception e) {
                    }
                }
                if (reactivos instanceof java.util.List) {
                    java.util.List<?> lista = (java.util.List<?>) reactivos;
                    for (Object obj : lista) {
                        if (obj instanceof java.util.Map) {
                            java.util.Map<String, Object> val = (java.util.Map<String, Object>) obj;
                            String nombre = val.getOrDefault("nombre", "").toString();
                            String tipo = val.getOrDefault("tipo", "").toString();
                            String resultados = val.getOrDefault("resultados", "").toString();
                            String fecha = val.getOrDefault("fecha", "").toString();
                            tabla.addCell(createCell("Nombre: " + nombre + " | Tipo: " + tipo + " | Resultados: "
                                    + resultados + " | Fecha: " + fecha, false, null, 4, 1, TextAlignment.LEFT));
                        }
                    }
                } else if (reactivos instanceof java.util.Map) {
                    java.util.Map<String, Object> val = (java.util.Map<String, Object>) reactivos;
                    String nombre = val.getOrDefault("nombre", "").toString();
                    String tipo = val.getOrDefault("tipo", "").toString();
                    String resultados = val.getOrDefault("resultados", "").toString();
                    String fecha = val.getOrDefault("fecha", "").toString();
                    tabla.addCell(createCell("Nombre: " + nombre + " | Tipo: " + tipo + " | Resultados: " + resultados
                            + " | Fecha: " + fecha, false, null, 4, 1, TextAlignment.LEFT));
                } else {
                    tabla.addCell(createCell("Sin reactivos registrados", false, null, 4, 1, TextAlignment.LEFT));
                }
            } else {
                tabla.addCell(createCell("Sin reactivos registrados", false, null, 4, 1, TextAlignment.LEFT));
            }

            // Resultados
            tabla.addCell(createCell("Resultados:", true, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getResultados() != null ? seguimiento.getResultados() : "", false,
                    null, 4, 1, TextAlignment.LEFT));

            // Herramientas / Enfoques / Técnicas aplicadas
            tabla.addCell(
                    createCell("Herramientas / Enfoques / Técnicas aplicadas:", true, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(
                    seguimiento.getHerramientasEnfoques() != null ? seguimiento.getHerramientasEnfoques() : "", false,
                    null, 4, 1, TextAlignment.LEFT));

            // Avances
            tabla.addCell(createCell("Avances:", true, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getAvances() != null ? seguimiento.getAvances() : "", false, null, 4,
                    1, TextAlignment.LEFT));

            Object familiar = seguimiento.getFamiliarContactado();
            String contactado = "", nombreF = "", parentesco = "", telefonoF = "", relato = "";
            if (familiar != null) {
                if (familiar instanceof String) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        familiar = mapper.readValue((String) familiar, java.util.Map.class);
                    } catch (Exception e) {
                    }
                }
                if (familiar instanceof java.util.Map) {
                    java.util.Map<String, Object> val = (java.util.Map<String, Object>) familiar;
                    Object contactadoObj = val.get("contactado");
                    if (contactadoObj instanceof Boolean) {
                        contactado = ((Boolean) contactadoObj) ? "SI" : "NO";
                    } else if (contactadoObj != null) {
                        contactado = contactadoObj.toString().equalsIgnoreCase("true") ? "SI" : "NO";
                    }
                    nombreF = val.getOrDefault("nombre", "").toString();
                    parentesco = val.getOrDefault("parentesco", "").toString();
                    telefonoF = val.getOrDefault("telefono", "").toString();
                    relato = val.getOrDefault("relato", "").toString();
                }
            }
            // Seguimiento social, Familiar contactado, Teléfono, Nombres
            tabla.addCell(createCell("Seguimiento social", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Familiar contactado: " + contactado, true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Teléfono", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Nombres", true, null, 1, 1, TextAlignment.LEFT));
            // Fila: Seguimiento social, Parentesco, Teléfono, Nombres (con etiquetas)
            tabla.addCell(createCell(seguimiento.getSeguimientoSocial() != null ? seguimiento.getSeguimientoSocial() : "",false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(parentesco, false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(telefonoF, false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(nombreF, false, null, 1, 1, TextAlignment.LEFT));

            // Versión de los hechos/relato de familiares
            tabla.addCell(
                    createCell("Versión de los hechos/relato de familiares:", true, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(relato, false, null, 4, 1, TextAlignment.LEFT));

            // Verificadores del seguimiento
            tabla.addCell(createCell("Verificadores del seguimiento:", true, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(
                    seguimiento.getVerificadoresSeguimiento() != null ? seguimiento.getVerificadoresSeguimiento() : "",
                    false, null, 4, 1, TextAlignment.LEFT));

            // Firmas
            tabla.addCell(createCell("Firma Psicólogo", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(
                    createCell(seguimiento.getFirmaPsicologo() != null && seguimiento.getFirmaPsicologo() ? "" : "",
                            false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Firma empleado.", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(seguimiento.getFirmaEmpleado() != null && seguimiento.getFirmaEmpleado() ? "" : "",
                    false, null, 1, 1, TextAlignment.LEFT));

            document.add(tabla);
            if (i < seguimientos.size() - 1) {
                document.add(new com.itextpdf.layout.element.AreaBreak(
                        com.itextpdf.layout.properties.AreaBreakType.NEXT_PAGE));
            }
        }
        document.close();

        // Guardar en Descargas
        String userHome = System.getProperty("user.home");
        String baseName = "AtencionSeguimientoPsicosocial_"
                + (empleado != null && empleado.getCedula() != null ? empleado.getCedula() : "");
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
            fos.write(out.toByteArray());
            System.out.println("PDF de atención y seguimiento guardado exitosamente en " + rutaDescargas);
        } catch (Exception ex) {
            System.out.println("Error al guardar el PDF de atención y seguimiento: " + ex.getMessage());
        }
        // Devolver el PDF en base64
        String base64Pdf = java.util.Base64.getEncoder().encodeToString(out.toByteArray());
        return new DocumentoBase64Dto(base64Pdf);
    }
}
