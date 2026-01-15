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
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.FichaSocial;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import saitel.medicina.dto.DocumentoBase64Dto;


@Service
public class FichaSocialReporte {
    DeviceRgb verde = new DeviceRgb(0xD0, 0xF2, 0xDB);
   
    private Cell createCell(String texto, boolean bold, com.itextpdf.kernel.colors.Color bgColor, int colspan, int rowspan, TextAlignment alignment) {
        Paragraph p = new Paragraph(texto);
        try {
            if (bold) p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
        } catch (java.io.IOException e) {
            // Si hay error de fuente, continuar sin negrita
        }
        Cell cell = new Cell(rowspan, colspan).add(p);
        if (bgColor != null) cell.setBackgroundColor(bgColor);
        if (alignment != null) cell.setTextAlignment(alignment);
        cell.setFontSize(10);
        return cell;
    }

    @SuppressWarnings("unchecked")
    public DocumentoBase64Dto generarPdf(DatosEmpleados empleado, FichaSocial ficha)  {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(10, 10, 10, 10);

        // Título principal
        Paragraph titulo = new Paragraph("FICHA SOCIAL")
                .setFontSize(16)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(verde)
                .setMarginBottom(5);
        document.add(titulo);


        float[] anchoCols = {120f, 120f, 120f, 120f, 120f, 120f};
        Table tabla = new Table(anchoCols).setWidth(PageSize.A4.getWidth() - 20f);
        tabla.addCell(createCell("Fecha", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(ficha.getFecha() != null ? ficha.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "", false, null, 2, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("No. historia", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(ficha.getNumeroHistoriaClinica() != null ? ficha.getNumeroHistoriaClinica() : "", false, null, 2, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Datos del paciente", true, verde, 6, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Nombres y apellidos", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(
            (empleado.getPrimerApellido() != null ? empleado.getPrimerApellido() : "") + " " +
            (empleado.getSegundoApellido() != null ? empleado.getSegundoApellido() : "") + " " +
            (empleado.getPrimerNombre() != null ? empleado.getPrimerNombre() : "") + " " +
            (empleado.getSegundoNombre() != null ? empleado.getSegundoNombre() : ""),
            false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Edad", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getEdad() != null ? empleado.getEdad().toString() : "", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Sexo", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getSexo() != null ? empleado.getSexo() : "", false, null, 1, 1, TextAlignment.LEFT));

        tabla.addCell(createCell("Documento de identidad", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getCedula() != null ? empleado.getCedula() : "", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Lugar y fecha de nacimiento", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell((empleado.getLugarNacimiento() != null ? empleado.getLugarNacimiento() : "") + " " + (empleado.getFechaNacimiento() != null ? empleado.getFechaNacimiento().toString() : ""), false, null, 3, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Dirección de residencia", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getDireccion() != null ? empleado.getDireccion() : "", false, null, 1, 1, TextAlignment.LEFT)); // No hay campo en DatosEmpleados
        tabla.addCell(createCell("Cargo en la empresa y área", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(
            (empleado.getCargo() != null ? empleado.getCargo() : "") + " / " +
            (empleado.getDepartamento() != null ? empleado.getDepartamento() : ""),
            false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Remuneración", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getSueldo() != null ? empleado.getSueldo().toString() : "", false, null, 1, 1, TextAlignment.LEFT));

        tabla.addCell(createCell("No. Teléfono celular", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getMovil() != null ? empleado.getMovil() : "", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Nivel de escolaridad", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getDatosAcademico() != null ? empleado.getDatosAcademico() : "", false, null, 3, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Estado Civil", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getEstadoCivil() != null ? empleado.getEstadoCivil() : "", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Religión", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(ficha.getReligion() != null ? ficha.getReligion().getNombre() : "", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Correo electrónico", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getEmail() != null ? empleado.getEmail() : "", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Discapacidad", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell((empleado.getDiscapacidad() != null && !empleado.getDiscapacidad().trim().isEmpty()) ? empleado.getDiscapacidad() : "NINGUNA",false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Grado o porcentaje", true, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getDisPorcentaje() != null ? empleado.getDisPorcentaje() : "0%", false, null, 3, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Contactos de emergencia", true, verde, 6, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Nombres", true, null, 2, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Parentesco", true, null, 2, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Teléfono", true, null, 2, 1, TextAlignment.LEFT));
        // Contactos de emergencia (de ficha.getContactosEmergencia)
        boolean contactoAgregado = false;
        Object contactos = ficha.getContactosEmergencia();
        // Conversión explícita si es String (JSON)
        if (contactos != null) {
            if (contactos instanceof String) {
                String contactosStr = (String) contactos;
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    if (contactosStr.trim().startsWith("[")) {
                        contactos = mapper.readValue(contactosStr, java.util.List.class);
                    } else {
                        contactos = mapper.readValue(contactosStr, java.util.Map.class);
                    }
                } catch (Exception e) {
                    System.out.println("Error deserializando contactosEmergencia: " + e.getMessage());
                }
            }
            if (contactos instanceof java.util.List) {
                java.util.List<?> lista = (java.util.List<?>) contactos;
                for (Object obj : lista) {
                    if (obj instanceof java.util.Map) {
                        java.util.Map<String, Object> val = (java.util.Map<String, Object>) obj;
                        String nombre = val.getOrDefault("nombre", "") != null ? val.getOrDefault("nombre", "").toString() : "";
                        String parentesco = val.getOrDefault("parentesco", "") != null ? val.getOrDefault("parentesco", "").toString() : "";
                        String telefono = val.getOrDefault("telefono", "") != null ? val.getOrDefault("telefono", "").toString() : "";
                        tabla.addCell(createCell(nombre, false, null, 2, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(parentesco, false, null, 2, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(telefono, false, null, 2, 1, TextAlignment.LEFT));
                        contactoAgregado = true;
                    }
                }
            } else if (contactos instanceof java.util.Map) {
                java.util.Map<String, Object> val = (java.util.Map<String, Object>) contactos;
                // Si tiene las claves típicas de contacto, lo tratamos como contacto único
                if (val.containsKey("nombre") && val.containsKey("telefono")) {
                    String nombre = val.getOrDefault("nombre", "") != null ? val.getOrDefault("nombre", "").toString() : "";
                    String parentesco = val.getOrDefault("parentesco", "") != null ? val.getOrDefault("parentesco", "").toString() : "";
                    String telefono = val.getOrDefault("telefono", "") != null ? val.getOrDefault("telefono", "").toString() : "";
                    tabla.addCell(createCell(nombre, false, null, 2, 1, TextAlignment.LEFT));
                    tabla.addCell(createCell(parentesco, false, null, 2, 1, TextAlignment.LEFT));
                    tabla.addCell(createCell(telefono, false, null, 2, 1, TextAlignment.LEFT));
                    contactoAgregado = true;
                } else {
                    // Formato alternativo: Map de parentesco -> datos
                    for (java.util.Map.Entry<String, Object> entry : val.entrySet()) {
                        String parentesco = entry.getKey() != null ? entry.getKey() : "";
                        Object value = entry.getValue();
                        if (value instanceof java.util.Map) {
                            java.util.Map<String, Object> datos = (java.util.Map<String, Object>) value;
                            String nombre = datos.getOrDefault("nombre", "") != null ? datos.getOrDefault("nombre", "").toString() : "";
                            String telefono = datos.getOrDefault("telefono", "") != null ? datos.getOrDefault("telefono", "").toString() : "";
                            tabla.addCell(createCell(nombre, false, null, 2, 1, TextAlignment.LEFT));
                            tabla.addCell(createCell(parentesco, false, null, 2, 1, TextAlignment.LEFT));
                            tabla.addCell(createCell(telefono, false, null, 2, 1, TextAlignment.LEFT));
                            contactoAgregado = true;
                        }
                    }
                }
            }
        }
        if (!contactoAgregado) {
            tabla.addCell(createCell("Sin contactos registrados", false, null, 6, 1, TextAlignment.LEFT));
        }
        tabla.addCell(createCell("Datos Académicos:", true, verde, 6, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Escolaridad", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Instituto", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Segundo nivel", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Universidad o Instituto", false, null, 1, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Posgrado", false, null, 2, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(empleado.getDatosAcademico() != null ? empleado.getDatosAcademico() : "", false, null, 6, 1, TextAlignment.LEFT));
        tabla.addCell(createCell("Genograma", true, verde, 6, 1, TextAlignment.LEFT));
        tabla.addCell(createCell(ficha.getGenograma() != null ? ficha.getGenograma() : "", false, null, 6, 1, TextAlignment.LEFT));

        document.add(tabla);
        document.close();

        // Guardar en Descargas
        String userHome = System.getProperty("user.home");
        String baseName = "FichaSocial_" + (empleado != null && empleado.getCedula() != null ? empleado.getCedula() : "");
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
            System.out.println("PDF de ficha social guardado exitosamente en " + rutaDescargas);
        } catch (Exception ex) {
            System.out.println("Error al guardar el PDF de ficha social: " + ex.getMessage());
        }
        // Devolver el PDF en base64
        String base64Pdf = java.util.Base64.getEncoder().encodeToString(out.toByteArray());
        return new DocumentoBase64Dto(base64Pdf);

        } catch (Exception e) {
            System.out.println("Error al generar el PDF de la ficha social: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

