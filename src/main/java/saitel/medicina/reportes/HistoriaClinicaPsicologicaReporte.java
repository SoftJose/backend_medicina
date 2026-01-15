
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

import saitel.medicina.dto.DocumentoBase64Dto;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.HistoriaClinicaPsicologica;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class HistoriaClinicaPsicologicaReporte {
    DeviceRgb verde = new DeviceRgb(0xD0, 0xF2, 0xDB);

    private Cell createCell(String texto, boolean bold, com.itextpdf.kernel.colors.Color bgColor, int colspan, int rowspan, TextAlignment alignment) {
        Paragraph p = new Paragraph(texto);
        try {
            if (bold) p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
        } catch (java.io.IOException e) {}
        Cell cell = new Cell(rowspan, colspan).add(p);
        if (bgColor != null) cell.setBackgroundColor(bgColor);
        if (alignment != null) cell.setTextAlignment(alignment);
        cell.setFontSize(10);
        return cell;
    }
    @SuppressWarnings("unchecked")
    public DocumentoBase64Dto generarPdf(DatosEmpleados empleado, List<HistoriaClinicaPsicologica> historias) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(10, 10, 10, 10);
        for (int i = 0; i < historias.size(); i++) {
            HistoriaClinicaPsicologica historia = historias.get(i);
            // Título principal
            Paragraph titulo = new Paragraph("HISTORIA CLÍNICA PSICOLÓGICA")
                    .setFontSize(16)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(verde)
                    .setMarginBottom(5);
            document.add(titulo);

            // Tabla principal
            float[] anchoCols = {120f, 120f, 120f, 120f, 120f, 120f};
            Table tabla = new Table(anchoCols).setWidth(PageSize.A4.getWidth() - 20f);

            // Fila Fecha y No. historia
            tabla.addCell(createCell("Fecha atención", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getFechaAtencion() != null ? historia.getFechaAtencion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "", false, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Identificación", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(empleado != null && empleado.getCedula() != null ? empleado.getCedula() : "", false, null, 2, 1, TextAlignment.LEFT));

            // Sección Historia clínica
            tabla.addCell(createCell("Historia clínica", true, verde, 6, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Motivo de consulta", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getMotivoConsulta() != null ? historia.getMotivoConsulta() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Observación general del paciente (Examen mental)", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getObservacionGeneral() != null ? historia.getObservacionGeneral() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Aspectos relacionados con el motivo de consulta", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getAspectosMotivo() != null ? historia.getAspectosMotivo() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Historial de la situación", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getHistorialSituacion() != null ? historia.getHistorialSituacion() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Intentos previos para solucionarlo", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getIntentosPrevios() != null ? historia.getIntentosPrevios() : "", false, null, 4, 1, TextAlignment.LEFT));

            // Redes de apoyo
            tabla.addCell(createCell("Redes de apoyo del/la paciente", true, null, 6, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Nombre", true, null, 3, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Parentesco", true, null, 3, 1, TextAlignment.LEFT));
            
            Object redes = historia.getRedesApoyo();
            if (redes != null) {
                if (redes instanceof String) {
                    String redesStr = (String) redes;
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        if (redesStr.trim().startsWith("[")) {
                            redes = mapper.readValue(redesStr, java.util.List.class);
                        } else {
                            redes = mapper.readValue(redesStr, java.util.Map.class);
                        }
                    } catch (Exception e) {}
                }
                if (redes instanceof java.util.List) {
                    java.util.List<?> lista = (java.util.List<?>) redes;
                    for (Object obj : lista) {
                        if (obj instanceof java.util.Map) {
                            java.util.Map<?, ?> mapObj = (java.util.Map<?, ?>) obj;
                            if (mapObj instanceof java.util.Map) {
                                java.util.Map<String, Object> val = (java.util.Map<String, Object>) mapObj;
                                String nombre = val.getOrDefault("nombre", "").toString();
                                String parentesco = val.getOrDefault("parentesco", "").toString();
                                tabla.addCell(createCell(nombre, false, null, 3, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(parentesco, false, null, 3, 1, TextAlignment.LEFT));
                            }
                        }
                    }
                } else if (redes instanceof java.util.Map) {
                    java.util.Map<?, ?> mapObj = (java.util.Map<?, ?>) redes;
                    if (mapObj instanceof java.util.Map) {
                        java.util.Map<String, Object> val = (java.util.Map<String, Object>) mapObj;
                        String nombre = val.getOrDefault("nombre", "").toString();
                        String parentesco = val.getOrDefault("parentesco", "").toString();
                        tabla.addCell(createCell(nombre, false, null, 3, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(parentesco, false, null, 3, 1, TextAlignment.LEFT));
                    }
                } else {
                    tabla.addCell(createCell("Sin redes registradas", false, null, 6, 1, TextAlignment.LEFT));
                }
            } else {
                tabla.addCell(createCell("Sin redes registradas", false, null, 6, 1, TextAlignment.LEFT));
            }

            // Sección Aspectos familiares
            tabla.addCell(createCell("Aspectos familiares", true, verde, 6, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Nombre", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Parentesco", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Edad", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Escolaridad", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Ocupación", true, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Relación con el paciente", true, null, 1, 1, TextAlignment.LEFT));
            
            Object aspectos = historia.getAspectosFamilia();
            if (aspectos != null) {
                if (aspectos instanceof String) {
                    String aspectosStr = (String) aspectos;
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        if (aspectosStr.trim().startsWith("[")) {
                            aspectos = mapper.readValue(aspectosStr, java.util.List.class);
                        } else {
                            aspectos = mapper.readValue(aspectosStr, java.util.Map.class);
                        }
                    } catch (Exception e) {}
                }
                if (aspectos instanceof java.util.List) {
                    java.util.List<?> lista = (java.util.List<?>) aspectos;
                    for (Object obj : lista) {
                        if (obj instanceof java.util.Map) {
                            java.util.Map<?, ?> mapObj = (java.util.Map<?, ?>) obj;
                            if (mapObj instanceof java.util.Map) {
                                java.util.Map<String, Object> val = (java.util.Map<String, Object>) mapObj;
                                String nombre = val.getOrDefault("nombre", "").toString();
                                String parentesco = val.getOrDefault("parentesco", "").toString();
                                String edad = val.getOrDefault("edad", "").toString();
                                String escolaridad = val.getOrDefault("escolaridad", "").toString();
                                String ocupacion = val.getOrDefault("ocupacion", "").toString();
                                String relacion = val.getOrDefault("relacion", "").toString();
                                tabla.addCell(createCell(nombre, false, null, 1, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(parentesco, false, null, 1, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(edad, false, null, 1, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(escolaridad, false, null, 1, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(ocupacion, false, null, 1, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(relacion, false, null, 1, 1, TextAlignment.LEFT));
                            }
                        }
                    }
                } else if (aspectos instanceof java.util.Map) {
                    java.util.Map<?, ?> mapObj = (java.util.Map<?, ?>) aspectos;
                    if (mapObj instanceof java.util.Map) {
                        java.util.Map<String, Object> val = (java.util.Map<String, Object>) mapObj;
                        String nombre = val.getOrDefault("nombre", "").toString();
                        String parentesco = val.getOrDefault("parentesco", "").toString();
                        String edad = val.getOrDefault("edad", "").toString();
                        String escolaridad = val.getOrDefault("escolaridad", "").toString();
                        String ocupacion = val.getOrDefault("ocupacion", "").toString();
                        String relacion = val.getOrDefault("relacion", "").toString();
                        tabla.addCell(createCell(nombre, false, null, 1, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(parentesco, false, null, 1, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(edad, false, null, 1, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(escolaridad, false, null, 1, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(ocupacion, false, null, 1, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(relacion, false, null, 1, 1, TextAlignment.LEFT));
                    }
                } else {
                    tabla.addCell(createCell("Sin aspectos familiares registrados", false, null, 6, 1, TextAlignment.LEFT));
                }
            } else {
                tabla.addCell(createCell("Sin aspectos familiares registrados", false, null, 6, 1, TextAlignment.LEFT));
            }

            tabla.addCell(createCell("Observaciones del ambiente familiar", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getObservacionesFamilia() != null ? historia.getObservacionesFamilia() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Familiograma", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getFamiliograma() != null ? historia.getFamiliograma() : "", false, null, 4, 1, TextAlignment.LEFT));

            // Sección Área personal
            tabla.addCell(createCell("Área personal", true, verde, 6, 1, TextAlignment.LEFT));
            Object areaPersonal = historia.getAreaPersonal();
            String sueno = "", alimentacion = "", ejercicio = "", pasatiempos = "";
            if (areaPersonal != null) {
                if (areaPersonal instanceof String) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        areaPersonal = mapper.readValue((String) areaPersonal, java.util.Map.class);
                    } catch (Exception e) {}
                }
                if (areaPersonal instanceof java.util.Map) {
                    java.util.Map<?, ?> mapObj = (java.util.Map<?, ?>) areaPersonal;
                    if (mapObj instanceof java.util.Map) {
                        java.util.Map<String, Object> val = (java.util.Map<String, Object>) mapObj;
                        sueno = val.getOrDefault("sueno", "").toString();
                        alimentacion = val.getOrDefault("alimentacion", "").toString();
                        ejercicio = val.getOrDefault("ejercicio", "").toString();
                        pasatiempos = val.getOrDefault("pasatiempos", "").toString();
                    }
                }
            }
            tabla.addCell(createCell("Sueño: " + sueno, false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Alimentación: " + alimentacion, false, null, 3, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Ejercicio: " + ejercicio, false, null, 1, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Pasatiempo: " + pasatiempos, false, null, 1, 1, TextAlignment.LEFT));

            // Sección Historia académica y laboral
            tabla.addCell(createCell("Historia académica", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getHistoriaAcademica() != null ? historia.getHistoriaAcademica() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Área académica", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getAreaAcademica() != null ? historia.getAreaAcademica() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Historial laboral", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getHistorialLaboral() != null ? historia.getHistorialLaboral() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Antecedentes personales", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getAntecedentesPersonales() != null ? historia.getAntecedentesPersonales() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Relaciones de pareja", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getRelacionesPareja() != null ? historia.getRelacionesPareja() : "", false, null, 4, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Conductas de riesgo", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getConductasRiesgo() != null ? historia.getConductasRiesgo() : "", false, null, 4, 1, TextAlignment.LEFT));

            // Sección Impresión diagnóstica
            tabla.addCell(createCell("Impresión diagnóstica (DSM V / CIE-10)", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getImpresionDiagnostica() != null ? historia.getImpresionDiagnostica() : "", false, null, 4, 1, TextAlignment.LEFT));

            // Sección Reactivos aplicados
            tabla.addCell(createCell("Reactivos aplicados", true, verde, 6, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Nombre", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Resultados", true, null, 3, 1, TextAlignment.LEFT));
            tabla.addCell(createCell("Fecha", true, null, 1, 1, TextAlignment.LEFT));
            
            Object reactivos = historia.getReactivosAplicados();
            if (reactivos != null) {
                if (reactivos instanceof String) {
                    String reactivosStr = (String) reactivos;
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        if (reactivosStr.trim().startsWith("[")) {
                            reactivos = mapper.readValue(reactivosStr, java.util.List.class);
                        } else {
                            reactivos = mapper.readValue(reactivosStr, java.util.Map.class);
                        }
                    } catch (Exception e) {}
                }
                if (reactivos instanceof java.util.List) {
                    java.util.List<?> lista = (java.util.List<?>) reactivos;
                    for (Object obj : lista) {
                        if (obj instanceof java.util.Map) {
                            java.util.Map<?, ?> mapObj = (java.util.Map<?, ?>) obj;
                            if (mapObj instanceof java.util.Map) {
                                java.util.Map<String, Object> val = (java.util.Map<String, Object>) mapObj;
                                String nombre = val.getOrDefault("nombre", "").toString();
                                String resultados = val.getOrDefault("resultados", "").toString();
                                String fecha = val.getOrDefault("fecha", "").toString();
                                tabla.addCell(createCell(nombre, false, null, 2, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(resultados, false, null, 3, 1, TextAlignment.LEFT));
                                tabla.addCell(createCell(fecha, false, null, 1, 1, TextAlignment.LEFT));
                            }
                        }
                    }
                } else if (reactivos instanceof java.util.Map) {
                    java.util.Map<?, ?> mapObj = (java.util.Map<?, ?>) reactivos;
                    if (mapObj instanceof java.util.Map) {
                        java.util.Map<String, Object> val = (java.util.Map<String, Object>) mapObj;
                        String nombre = val.getOrDefault("nombre", "").toString();
                        String resultados = val.getOrDefault("resultados", "").toString();
                        String fecha = val.getOrDefault("fecha", "").toString();
                        tabla.addCell(createCell(nombre, false, null, 2, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(resultados, false, null, 3, 1, TextAlignment.LEFT));
                        tabla.addCell(createCell(fecha, false, null, 1, 1, TextAlignment.LEFT));
                    }
                } else {
                    tabla.addCell(createCell("Sin reactivos registrados", true, null, 6, 1, TextAlignment.LEFT));
                }
            } else {
                tabla.addCell(createCell("Sin reactivos registrados", true, null, 6, 1, TextAlignment.LEFT));
            }

            // Sección Plan de tratamiento
            tabla.addCell(createCell("Plan de tratamiento", true, null, 2, 1, TextAlignment.LEFT));
            tabla.addCell(createCell(historia.getPlanTratamiento() != null ? historia.getPlanTratamiento() : "", false, null, 4, 1, TextAlignment.LEFT));

            document.add(tabla);
            if (i < historias.size() - 1) {
                document.add(new com.itextpdf.layout.element.AreaBreak(com.itextpdf.layout.properties.AreaBreakType.NEXT_PAGE));
            }
        }
        document.close();
         // Guardar en Descargas con nombre y sufijo incremental si existe
        String userHome = System.getProperty("user.home");
        String baseName;
        // Si tienes idHistoria, úsalo, si no, usa idEmpleado
        if (historias != null && historias.size() == 1 && historias.get(0).getId() != null) {
            baseName = "historia_clinica_psicologica_" + historias.get(0).getId();
        } else {
            baseName = "historia_clinica_psicologica_" + (empleado != null && empleado.getIdEmpleado() != null ? empleado.getIdEmpleado() : "");
        }
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
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(rutaDescargas)) {
            fos.write(baos.toByteArray());
            System.out.println("PDF de historia clínica guardado exitosamente en " + rutaDescargas);
        } catch (Exception ex) {
            System.out.println("Error al guardar el PDF de historia clínica: " + ex.getMessage());
        }
            // Devolver el PDF en base64
            String base64Pdf = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
            return new DocumentoBase64Dto(base64Pdf);

        } catch (Exception e) {
            System.out.println("Error al generar el PDF de historia clínica: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
