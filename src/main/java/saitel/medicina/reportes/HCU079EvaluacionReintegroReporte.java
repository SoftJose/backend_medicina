package saitel.medicina.reportes;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import saitel.medicina.dto.DocumentoBase64Dto;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.layout.borders.SolidBorder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saitel.medicina.entity.*;
import saitel.medicina.event.MySignatureFieldEvent;
import saitel.medicina.repository.*;
import saitel.medicina.service.ImpLog.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HCU079EvaluacionReintegroReporte {
private final EvaluacionRepository evaluacionRepository;
private final DatosEmpleadoServiceImpLog datosEmpleadoServiceImpLog;
private final DatosProfesionalServiceImpLog datosProfesionalServiceImpLog;
private final MotivoConsultaServiceImpLog motivoConsultaServiceImpLog;
private final EnfermedadActualServiceImpLog enfermedadActualServiceImpLog;
private final SignoVitalServiceImpLog signoVitalServiceImpLog;
private final ExamenFisicoServiceImpLog examenFisicoServiceImpLog;
private final ExamenServiceImpLog examenServiceImpLog;
private final DiagnosticoServiceImpLog diagnosticoServiceImpLog;
private final AptitudMedicaServiceImpLog aptitudMedicaServiceImpLog;
private final RecomendacionTratamientoServiceImpLog recomendacionTratamientoServiceImpLog;

DeviceRgb gris_claro = new DeviceRgb(143, 140, 140);
DeviceRgb celeste = new DeviceRgb(211, 244, 245);
DeviceRgb verde = new DeviceRgb(226, 247, 210);
DeviceRgb lila = new DeviceRgb(203, 208, 242);
float tablaAncho = PageSize.A4.getWidth() - 20f;
float bordeGrosor = 1f;
float[] anchoCols = { 575f };
private final float fontSize = 8f;
private PdfFont fontNormal;
SolidBorder bordeExterno = new SolidBorder(gris_claro, bordeGrosor);

// Método auxiliar para crear celdas con formato
private Cell createCell(String texto, boolean bold, Color bgColor, int colspan,
                int rowspan, TextAlignment alignment) {
        Paragraph p = new Paragraph(texto != null ? texto : "");
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
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setFontSize(8);
        return cell;
}

// Método auxiliar para crear celdas de selección (checkbox)
private Cell createSelectionCell(String check) {
        Cell cell = new Cell(1, 1);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setHeight(14f);
        Paragraph p = new Paragraph(check != null ? check : "");
        try {
                p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
        } catch (Exception e) {
        }
        p.setFontColor(new DeviceRgb(0, 0, 0)); // negro
        p.setFontSize(7);
        cell.add(p);
        return cell;
}

// Método para crear celdas con Paragraph (permite estilos mixtos)
private Cell createCellParagraph(Paragraph p, Color bgColor, int colspan, int rowspan,
                TextAlignment alignment) {
        Cell cell = new Cell(rowspan, colspan).add(p);
        if (bgColor != null)
                cell.setBackgroundColor(bgColor);
        if (alignment != null)
                cell.setTextAlignment(alignment);
        cell.setFontSize(8);
        return cell;
}

// Método auxiliar para crear celdas con tamaño de letra personalizado
private Cell createCellWithFontSize(String texto, boolean bold, DeviceRgb color, int colspan, int rowspan,
                TextAlignment align, int fontSize) {
        Paragraph p = new Paragraph(texto != null ? texto : "").setFontSize(fontSize);
        try {
                if (bold)
                        p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
        } catch (java.io.IOException e) {
        }
        Cell cell = new Cell(rowspan, colspan).add(p);
        if (color != null)
                cell.setBackgroundColor(color);
        if (align != null)
                cell.setTextAlignment(align);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        return cell;
}

// Sobrecarga para soportar fuentes y tamaños de fuente
private Cell createCell(String texto, boolean bold, Color bgColor, int colspan,
            int rowspan, TextAlignment alignment, PdfFont font, float fontSize) {
        Paragraph p = new Paragraph(texto != null ? texto : "");
        try {
            if (font != null) {
                p.setFont(font);
            } else if (bold) {
                p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
            } else {
                p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            }
        } catch (Exception e) {
        }
        p.setFontSize(fontSize);
        Cell cell = new Cell(rowspan, colspan).add(p);
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
                canvas.showTextAligned(new Paragraph("SNS-MSP / Form. HCU 079 / 2019")
                                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                .setFontSize(7), pageSize.getLeft() + 20, y, TextAlignment.LEFT);
                canvas.showTextAligned(new Paragraph("EVALUACIÓN - REINTEGRO")
                                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                .setFontSize(7), pageSize.getRight() - 20, y, TextAlignment.RIGHT);
                canvas.close();
        } catch (java.io.IOException e) {
                e.printStackTrace();
        }
}

public DocumentoBase64Dto generarpdf(Integer idEvaluacion) {
         if (idEvaluacion == null) {
        throw new IllegalArgumentException("El idEvaluacion no puede ser nulo");
       }
        Optional<Evaluacion> evaluacionOpt = evaluacionRepository.findById(idEvaluacion);
        if (evaluacionOpt.isEmpty())
                throw new IllegalArgumentException("Evaluación no encontrada");
        Evaluacion evaluacion = evaluacionOpt.get();
        FichaSocial ficha = evaluacion.getFichaSocial();
        DatosEmpleados empleado = datosEmpleadoServiceImpLog.obtenerPorId(ficha.getIdEmpleado()).orElse(null);
        MotivoConsulta motivoConsulta = null;
        List<MotivoConsulta> motivoConsultaList = motivoConsultaServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (motivoConsultaList != null && !motivoConsultaList.isEmpty())
                motivoConsulta = motivoConsultaList.get(0);
        AptitudLaboral aptitud = aptitudMedicaServiceImpLog.findByIdEvaluacion(idEvaluacion).orElse(null);
        EnfermedadActual enfermedadActual = null;
        List<EnfermedadActual> enfermedadActualList = enfermedadActualServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (enfermedadActualList != null && !enfermedadActualList.isEmpty())
                enfermedadActual = enfermedadActualList.get(0);
        SignoVital signoVital = null;
        List<SignoVital> signoVitalList = signoVitalServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (signoVitalList != null && !signoVitalList.isEmpty())
                signoVital = signoVitalList.get(0);
        ExamenFisico examenFisico = null;
        List<ExamenFisico> examenFisicoList = examenFisicoServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (examenFisicoList != null && !examenFisicoList.isEmpty())
                examenFisico = examenFisicoList.get(0);
        List<Examenes> examenList = examenServiceImpLog.findByIdEvaluacion(idEvaluacion);
        List<Diagnostico> diagnosticoList = diagnosticoServiceImpLog.findByIdEvaluacion(idEvaluacion);
        List<Recomendaciones> recomendacionesList = recomendacionTratamientoServiceImpLog.findByIdEvaluacion(idEvaluacion);
        DatosProfesional datosProfesional = null;
        List<DatosProfesional> datosProfesionalList = datosProfesionalServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (datosProfesionalList != null && !datosProfesionalList.isEmpty())
                datosProfesional = datosProfesionalList.get(0);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                PdfWriter writer = new PdfWriter(out);
                PdfDocument pdf = new PdfDocument(writer);
                pdf.addEventHandler(PdfDocumentEvent.END_PAGE, (IEventHandler) event -> {
                        drawFooter((PdfDocumentEvent) event);
                });
                Document document = new Document(pdf, PageSize.A4);
                document.setMargins(20, 10, 20, 10); // margen inferior aumentado
                float[] anchoCols = { 575f };
                // Título principal
                Paragraph titulo = new Paragraph("HCU-079\nEVALUACIÓN DE REINTEGRO")
                                .setFontSize(14)
                                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                .setTextAlignment(TextAlignment.CENTER)
                                .setBackgroundColor(null)
                                .setMarginBottom(5);
                document.add(titulo);

                // Sección A: DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO
{
                float[] anchoColsA = { 30f, 30f, 30f, 30f, 30f, 30f, 38f, 30f, 30f, 30f, 30f, 30f };
                Table tablaA = new Table(anchoColsA).setWidth(tablaAncho);
                tablaA.setBorder(bordeExterno);
                tablaA.setMarginBottom(8);
                // Título
                tablaA.addCell(createCell("A. DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO", true, lila, 12, 1,
                                TextAlignment.LEFT));
                // Encabezados
                tablaA.addCell(createCell("INSTITUCIÓN DEL SISTEMA O NOMBRE DE LA EMPRESA", true, verde, 3, 1,
                                TextAlignment.CENTER));
                tablaA.addCell(createCell("RUC", true, verde, 2, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("CIIU", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("ESTABLECIMIENTO DE SALUD", true, verde, 3, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("NÚMERO DE HISTORIA CLÍNICA", true, verde, 2, 1,
                                TextAlignment.CENTER));
                tablaA.addCell(createCell("NÚMERO DE ARCHIVO", true, verde, 1, 1, TextAlignment.CENTER));

                // Datos
                tablaA.addCell(createCell("SAITEL - " + (empleado != null ? empleado.getSucursal() : ""), false,
                                null, 3, 1, TextAlignment.LEFT));
                tablaA.addCell(createCell("1091728857001", false, null, 2, 1, TextAlignment.LEFT));
                tablaA.addCell(createCell("J619.04", false, null, 1, 1, TextAlignment.LEFT));
                tablaA.addCell(createCell("DEPARTAMENTO MEDICO - SAITEL", false, null, 3, 1,TextAlignment.LEFT));
                tablaA.addCell(createCell(empleado != null ? empleado.getCedula() : "", false, null, 2,1, TextAlignment.LEFT));
                tablaA.addCell(createCell("00 - ", false, null, 1, 1, TextAlignment.CENTER));
                // Encabezados 2
                tablaA.addCell(createCell("PRIMER APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("SEGUNDO APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("PRIMER NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("SEGUNDO NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("SEXO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("EDAD (AÑOS)", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("PUESTO DE TRABAJO (CIUO)", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("FECHA DEL ÚLTIMO DÍA LABORAL", true, verde, 1, 1,
                                TextAlignment.CENTER));
                tablaA.addCell(createCell("FECHA DE REINGRESO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("TOTAL (DÍAS)", true, verde, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell("CAUSA DE SALIDA", true, verde, 2, 1, TextAlignment.CENTER));
                // Datos2
                tablaA.addCell(createCell(empleado != null ? empleado.getPrimerApellido() : "", false, null, 1,1, TextAlignment.CENTER));
                tablaA.addCell(createCell(empleado != null ? empleado.getSegundoApellido() : "", false, null, 1,1, TextAlignment.CENTER));
                tablaA.addCell(createCell(empleado != null ? empleado.getPrimerNombre() : "", false, null, 1, 1,TextAlignment.CENTER));
                tablaA.addCell(createCell(empleado != null ? empleado.getSegundoNombre() : "", false, null, 1,1, TextAlignment.CENTER));
                tablaA.addCell(createCell(empleado != null ? empleado.getSexo() : "", false, null, 1, 1,TextAlignment.CENTER));
                tablaA.addCell(createCell(empleado != null && empleado.getEdad() != null? String.valueOf(empleado.getEdad()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell(evaluacion != null && evaluacion.getPuestoCiuo() != null ? evaluacion.getPuestoCiuo() : "",false, null, 2, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell(evaluacion.getFechaUltimoDiaLaboral() != null? evaluacion.getFechaUltimoDiaLaboral().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell(evaluacion.getFechaReingreso() != null? evaluacion.getFechaReingreso().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell(evaluacion.getTotalDias() != null ? String.valueOf(evaluacion.getTotalDias()): "",false, null, 1, 1, TextAlignment.CENTER));
                tablaA.addCell(createCell(evaluacion.getCausaSalida() != null ? evaluacion.getCausaSalida() : "", false,null, 2, 1, TextAlignment.CENTER));
                document.add(tablaA);
}
                // Sección B: MOTIVO DE CONSULTA / CONDICIÓN DE REINTEGRO
{
                Table tablaB = new Table(anchoCols).setWidth(tablaAncho);
                tablaB.setBorder(bordeExterno);
                tablaB.setMarginBottom(8);
                tablaB.addCell(createCell("B. MOTIVO DE CONSULTA / CONDICIÓN DE REINTEGRO", true, lila, 1, 1,
                                TextAlignment.LEFT));
                tablaB.addCell(createCellParagraph(new Paragraph()
                        .add(new Text("Descripción: ")
                        .setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(motivoConsulta != null && motivoConsulta
                        .getMotivo() != null? motivoConsulta.getMotivo(): "")),null, 1, 1, TextAlignment.LEFT));
                document.add(tablaB);
}
                // Sección C: ENFERMEDAD ACTUAL
{
                Table tablaC = new Table(anchoCols).setWidth(tablaAncho);
                tablaC.setBorder(bordeExterno);
                tablaC.setMarginBottom(8);
                tablaC.addCell(createCell("C. ENFERMEDAD ACTUAL", true, lila, 1, 1, TextAlignment.LEFT));
                tablaC.addCell(createCellParagraph(new Paragraph()
                        .add(new Text("Descripción: ")
                        .setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(enfermedadActual != null&& enfermedadActual.getDescripcion() != null? enfermedadActual.getDescripcion(): "")),null, 1, 1, TextAlignment.LEFT));
                document.add(tablaC);
}
                // Sección D: CONSTANTES VITALES Y ANTROPOMETRÍA
{
                float[] anchoColsD = { 64f, 64f, 64f, 64f, 64f, 64f, 64f, 64f, 63f };
                Table tablaD = new Table(anchoColsD).setWidth(tablaAncho);
                tablaD.setBorder(bordeExterno);
                tablaD.setMarginBottom(8);
                tablaD.addCell(createCell("D. CONSTANTES VITALES Y ANTROPOMETRÍA", true, lila, 9, 1,TextAlignment.LEFT));
                tablaD.addCell(createCellWithFontSize("PRESIÓN ARTERIAL", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaD.addCell(createCellWithFontSize("TEMPERATURA", true, celeste, 1, 1, TextAlignment.CENTER,8));
                tablaD.addCell(createCellWithFontSize("FRECUENCIA CARDIACA", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaD.addCell(createCellWithFontSize("SATURACIÓN DE OXÍGENO", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaD.addCell(createCellWithFontSize("FRECUENCIA RESPIRATORIA", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaD.addCell(createCellWithFontSize("PESO", true, celeste, 1, 1, TextAlignment.CENTER, 8));
                tablaD.addCell(createCellWithFontSize("TALLA", true, celeste, 1, 1, TextAlignment.CENTER, 8));
                tablaD.addCell(createCellWithFontSize("ÍNDICE DE MASA CORPORAL", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaD.addCell(createCellWithFontSize("PERÍMETRO ABDOMINAL", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                // Fila de datos debajo de los encabezados
                String presionArterial = "";
                if (signoVital != null && signoVital.getPresionArterialA() != null&& signoVital.getPresionArterialB() != null) {
                        presionArterial = signoVital.getPresionArterialA() + "/"+ signoVital.getPresionArterialB();
                }
                tablaD.addCell(createCell(presionArterial, false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getTemperatura() != null? String.valueOf(signoVital.getTemperatura()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getPerimetroAbdominal() != null? String.valueOf(signoVital.getPerimetroAbdominal()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getSaturacionOxg() != null? String.valueOf(signoVital.getSaturacionOxg()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getFrecuenciaRespiratoria() != null? String.valueOf(signoVital.getFrecuenciaRespiratoria()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getPeso() != null? String.valueOf(signoVital.getPeso()): "",false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getTalla() != null? String.valueOf(signoVital.getTalla()): "",false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getMasaCorporal() != null? String.valueOf(signoVital.getMasaCorporal()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaD.addCell(createCell(signoVital != null && signoVital.getPerimetroAbdominal() != null? String.valueOf(signoVital.getPerimetroAbdominal()): "", false, null, 1, 1, TextAlignment.CENTER));
                document.add(tablaD);
}
                // Sección E: EXAMEN FÍSICO REGIONAL
{
                float anchoNumero = 18f;
                float anchoLetra = 20f;
                float anchoX = 12f;
                float[] anchoColsE = {anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX};
                Table tablaE = new Table(anchoColsE).setWidth(tablaAncho);
                tablaE.setBorder(bordeExterno);
                tablaE.setMarginBottom(8);
                // Título
                tablaE.addCell(createCell("E. EXAMEN FÍSICO REGIONAL", true, lila, 15, 1, TextAlignment.LEFT));
                tablaE.addCell(createCell("REGIONES", true, verde, 15, 1, TextAlignment.LEFT));
                Cell celdaPiel = new Cell(3, 1);
                Paragraph pPiel = new Paragraph("1. Piel").setFontSize(6).setRotationAngle(Math.PI / 2);
                celdaPiel.add(pPiel);
                celdaPiel.setBackgroundColor(celeste);
                celdaPiel.setTextAlignment(TextAlignment.CENTER);
                celdaPiel.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celdaPiel.setHeight(30f);
                tablaE.addCell(celdaPiel);
                tablaE.addCell(createCell("a. Cicatrices", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCicatrices()) ? "X" : ""));
                Cell celdaOido = new Cell(3, 1);
                Paragraph pOido = new Paragraph("3. Oído").setFontSize(6).setRotationAngle(Math.PI / 2);
                celdaOido.add(pOido);
                celdaOido.setBackgroundColor(celeste);
                celdaOido.setTextAlignment(TextAlignment.CENTER);
                celdaOido.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celdaOido.setHeight(30f);
                tablaE.addCell(celdaOido);
                tablaE.addCell(createCell("a. C. auditivo externo", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getConductoAuditivoExterno()) ? "X" : ""));
                Cell celdanariz = new Cell(4, 1); // 4 filas, 1 columna
                Paragraph pNariz = new Paragraph("5. Nariz").setFontSize(6).setRotationAngle(Math.PI / 2);
                celdanariz.add(pNariz);
                celdanariz.setBackgroundColor(celeste);
                celdanariz.setTextAlignment(TextAlignment.CENTER);
                celdanariz.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celdanariz.setHeight(30f);
                tablaE.addCell(celdanariz);
                tablaE.addCell(createCell("a. Tabique", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTabique()) ? "X" : ""));
                Cell celtoraxx = new Cell(2, 1); // 4 filas, 1 columna
                Paragraph pToraxx = new Paragraph("8. Tórax").setFontSize(6).setRotationAngle(Math.PI / 2);
                celtoraxx.add(pToraxx);
                celtoraxx.setBackgroundColor(celeste);
                celtoraxx.setTextAlignment(TextAlignment.CENTER);
                celtoraxx.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celtoraxx.setHeight(30f);
                tablaE.addCell(celtoraxx);
                tablaE.addCell(createCell("a. Pulmones", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPulmones()) ? "X" : ""));
                Cell celpelvis = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph pPelvis = new Paragraph("11. Pelvis").setFontSize(6).setRotationAngle(Math.PI / 2);
                celpelvis.add(pPelvis);
                celpelvis.setBackgroundColor(celeste);
                celpelvis.setTextAlignment(TextAlignment.CENTER);
                celpelvis.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celpelvis.setHeight(30f);
                tablaE.addCell(celpelvis);
                tablaE.addCell(createCell("a.Pelvis", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPelvis()) ? "X" : ""));
                tablaE.addCell(createCell("b. Tatuajes", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTatuajes()) ? "X" : ""));
                tablaE.addCell(createCell("b. Pabellón", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPabellon()) ? "X" : ""));
                tablaE.addCell(createCell("b. Cornetes", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCornetes()) ? "X" : ""));
                tablaE.addCell(createCell("b. Parrilla Costal", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParrillaCostal()) ? "X" : ""));
                tablaE.addCell(createCell("b. Genitales", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getGenitales()) ? "X" : ""));
                tablaE.addCell(createCell("c. Piel y Faneras", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPielFaneras()) ? "X" : ""));
                tablaE.addCell(createCell("c. Tímpanos", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTimpanos()) ? "X" : ""));
                tablaE.addCell(createCell("c. Mucosas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMucosas()) ? "X" : ""));
                Cell celabdomen = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph pAbdomen = new Paragraph("9. Abdomen").setFontSize(6).setRotationAngle(Math.PI / 2);
                celabdomen.add(pAbdomen);
                celabdomen.setBackgroundColor(celeste);
                celabdomen.setTextAlignment(TextAlignment.RIGHT);
                celabdomen.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celabdomen.setHeight(30f);
                tablaE.addCell(celabdomen);
                tablaE.addCell(createCell("a.  Vísceras", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getVisceras()) ? "X" : ""));
                Cell celextremidades = new Cell(3, 1); // 3 filas, 1 columna
                Paragraph pExtremidades = new Paragraph("12. Extremidades").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celextremidades.add(pExtremidades);
                celextremidades.setBackgroundColor(celeste);
                celextremidades.setTextAlignment(TextAlignment.RIGHT);
                celextremidades.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celextremidades.setHeight(30f);
                tablaE.addCell(celextremidades);
                tablaE.addCell(createCell("a. Vascular", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getVascular()) ? "X" : ""));
                Cell celojos = new Cell(5, 1); // 5 filas, 1 columna
                Paragraph pOjosParagraph = new Paragraph("2. Ojos").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celojos.add(pOjosParagraph);
                celojos.setBackgroundColor(celeste);
                celojos.setTextAlignment(TextAlignment.CENTER);
                celojos.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celojos.setHeight(30f);
                tablaE.addCell(celojos);
                tablaE.addCell(createCell("a. Párpados", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParpados()) ? "X" : ""));
                Cell celorofaringe = new Cell(5, 1); // 5 filas, 1 columna
                Paragraph pOrofaringe = new Paragraph("4. Oro Faringe").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celorofaringe.add(pOrofaringe);
                celorofaringe.setBackgroundColor(celeste);
                celorofaringe.setTextAlignment(TextAlignment.CENTER);
                celorofaringe.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celorofaringe.setHeight(30f);
                tablaE.addCell(celorofaringe);
                tablaE.addCell(createCell("a. Labios", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getLabios()) ? "X" : ""));
                tablaE.addCell(createCell("d. Senos paranasales", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getSenosParanasales()) ? "X" : ""));
                tablaE.addCell(createCell("b. Pared abdominal", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParedAbdominal()) ? "X" : ""));
                tablaE.addCell(createCell("b. Miembros superiores", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMiembrosSuperiores()) ? "X" : ""));
                tablaE.addCell(createCell("b. Conjuntivas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getConjuntivas()) ? "X" : ""));
                tablaE.addCell(createCell("b. Lengua", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getLengua()) ? "X" : ""));
                Cell celcuello = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph pcuello = new Paragraph("6. Cuello").setFontSize(6).setRotationAngle(Math.PI / 2);
                celcuello.add(pcuello);
                celcuello.setBackgroundColor(celeste);
                celcuello.setTextAlignment(TextAlignment.CENTER);
                celcuello.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celcuello.setHeight(30f);
                tablaE.addCell(celcuello);
                tablaE.addCell(createCell("a. Tiroides / masas", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTiroidesMasas()) ? "X" : ""));
                Cell celcolumna = new Cell(4, 1); // 4 filas, 1 columna
                Paragraph pcolumna = new Paragraph("10. Columna").setFontSize(6).setRotationAngle(Math.PI / 2);
                celcolumna.add(pcolumna);
                celcolumna.setBackgroundColor(celeste);
                celcolumna.setTextAlignment(TextAlignment.CENTER);
                celcolumna.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celcolumna.setHeight(30f);
                tablaE.addCell(celcolumna);
                tablaE.addCell(createCell("a. Flexibilidad", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFlexibilidad()) ? "X" : ""));
                tablaE.addCell(createCell("c. Miembros inferiores", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMiembrosInferiores()) ? "X" : ""));
                tablaE.addCell(createCell("c.Pupilas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPupilas()) ? "X" : ""));
                tablaE.addCell(createCell("c. Faringe", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFaringe()) ? "X" : ""));
                tablaE.addCell(createCell("b. Movilidad", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMovilidadCuello()) ? "X" : ""));
                tablaE.addCell(createCell("b. Desviación", false, celeste, 1, 2, TextAlignment.LEFT));
                Cell celdaDesviacionResp = new Cell(2, 1);
                celdaDesviacionResp.add(new Paragraph(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDesviacion()) ? "X" : ""));
                celdaDesviacionResp.setTextAlignment(TextAlignment.CENTER);
                celdaDesviacionResp.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                tablaE.addCell(celdaDesviacionResp);
                Cell celneurologico = new Cell(4, 1); // 4 filas, 1 columna
                Paragraph pneurologico = new Paragraph("13. Neurologico").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celneurologico.add(pneurologico);
                celneurologico.setBackgroundColor(celeste);
                celneurologico.setTextAlignment(TextAlignment.CENTER);
                celneurologico.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celneurologico.setHeight(30f);
                tablaE.addCell(celneurologico);
                tablaE.addCell(createCell("a. Fuerza", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFuerza()) ? "X" : ""));
                tablaE.addCell(createCell("d. Córnea", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCornea()) ? "X" : ""));
                tablaE.addCell(createCell("d. Amígdalas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getAmigdalas()) ? "X" : ""));
                Cell celtorax = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph ptorax = new Paragraph("7. Torax").setFontSize(6).setRotationAngle(Math.PI / 2);
                celtorax.add(ptorax);
                celtorax.setBackgroundColor(celeste);
                celtorax.setTextAlignment(TextAlignment.CENTER);
                celtorax.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celtorax.setHeight(30f);
                tablaE.addCell(celtorax);
                tablaE.addCell(createCell("a. Mamas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMamas()) ? "X" : ""));
                tablaE.addCell(createCell("b. Sensibilidad", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getSensibilidad()) ? "X" : ""));
                tablaE.addCell(createCell("e. Motilidad", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMotilidadOcular()) ? "X" : ""));
                tablaE.addCell(createCell("e. Dentadura", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDentadura()) ? "X" : ""));
                tablaE.addCell(createCell("b. Corazón", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCorazon()) ? "X" : ""));
                tablaE.addCell(createCell("c. Dolor", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDolor()) ? "X" : ""));
                tablaE.addCell(createCell("c. Marcha", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMarcha()) ? "X" : ""));
                tablaE.addCell(createCellWithFontSize("CP = CON EVIDENCIA DE PATOLOGÍA: MARCAR 'X' Y DESCRIBIR EN LA SIGUIENTE SECCIÓN",false, null, 6, 1, TextAlignment.LEFT, 6));
                tablaE.addCell(createCellWithFontSize("SP = SIN EVIDENCIA DE PATOLOGÍA: MARCAR 'X' Y NO DESCRIBIR",false, null, 6, 1, TextAlignment.LEFT, 6));
                tablaE.addCell(createCell("d. Reflejos", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaE.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getReflejos()) ? "X" : ""));
                tablaE.addCell(
                createCellParagraph(new Paragraph()
                        .add(new Text("Observaciones: ")
                        .setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(examenFisico != null&& examenFisico.getObservaciones() != null? examenFisico.getObservaciones(): "")),null, 15, 1, TextAlignment.LEFT));
                document.add(tablaE);

                // Sección F: RESULTADOS DE EXÁMENES (IMAGEN, LABORATORIO Y OTROS)
                float[] anchoColsF = { 150f, 100f, 325f };
                Table tablaF = new Table(anchoColsF).setWidth(tablaAncho);
                tablaF.setBorder(bordeExterno);
                tablaF.setMarginBottom(8);
                tablaF.addCell(createCell("F. RESULTADOS DE EXÁMENES (IMAGEN, LABORATORIO Y OTROS)", true, lila,3, 1,TextAlignment.LEFT));
                // Encabezado
                tablaF.addCell(createCell("EXAMEN", true, verde, 1, 1, TextAlignment.CENTER));
                tablaF.addCell(createCell("FECHA\naaaa / mm / dd", true, verde, 1, 1, TextAlignment.CENTER));
                tablaF.addCell(createCell("RESULTADO", true, verde, 1, 1, TextAlignment.CENTER));
                // Filas de exámenes 
                if (examenList != null && !examenList.isEmpty()) {
                        for (Examenes ex : examenList) {
                                tablaF.addCell(createCell(ex.getNombreExamen() != null ? ex.getNombreExamen(): "",false, null,1, 1, TextAlignment.LEFT));
                                tablaF.addCell(createCell(
                                                ex.getFechaExamen() != null? ex.getFechaExamen().toString(): "",false, null, 1, 1, TextAlignment.CENTER));
                                tablaF.addCell(createCell(ex.getResultado() != null ? ex.getResultado() : "",false, null, 1, 1,TextAlignment.LEFT));
                        }
                }
                // Fila de observaciones
                String observaciones = "";
                if (examenList != null && !examenList.isEmpty()) {
                        for (Examenes ex : examenList) {
                                if (ex.getObservaciones() != null && !ex.getObservaciones().isEmpty()) {observaciones += ex.getObservaciones() + " | ";
                                }
                        }
                }
                tablaF.addCell(createCellParagraph( new Paragraph()
                        .add(new Text("Observaciones: ").setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(observaciones.trim())),null, 3, 1, TextAlignment.LEFT));
                document.add(tablaF);
}
                // Sección G: DIAGNÓSTICO
{
                float[] anchoColsG = { 30f, 400f, 80f, 40f, 40f };
                Table tablaG = new Table(anchoColsG).setWidth(tablaAncho);
                tablaG.setBorder(bordeExterno);
                tablaG.setMarginBottom(8);
                tablaG.addCell(createCell("G. DIAGNÓSTICO", true, lila, 2, 1, TextAlignment.LEFT));
                tablaG.addCell(createCell("PRE= PRESUNTIVO    DEF= DEFINITIVO", true, lila, 3, 1, TextAlignment.LEFT));
                tablaG.addCell(createCell("", false, verde, 1, 1, TextAlignment.CENTER)); // Número
                tablaG.addCell(createCell("Descripción", true, verde, 1, 1, TextAlignment.LEFT));
                tablaG.addCell(createCell("CIE", true, verde, 1, 1, TextAlignment.CENTER));
                tablaG.addCell(createCell("PRE", true, verde, 1, 1, TextAlignment.CENTER));
                tablaG.addCell(createCell("DEF", true, verde, 1, 1, TextAlignment.CENTER));
                if (diagnosticoList != null && !diagnosticoList.isEmpty()) {
                        for (int i = 0; i < diagnosticoList.size(); i++) {
                        Diagnostico diag = diagnosticoList.get(i);
                        String num = String.valueOf(i + 1);
                        tablaG.addCell(createCell(num, false, verde, 1, 1, TextAlignment.CENTER));
                        tablaG.addCell(createCell(diag.getDescripcion() != null ? diag.getDescripcion() : "",false, null, 1, 1, TextAlignment.LEFT));
                        tablaG.addCell(createCell(diag.getCie() != null ? diag.getCie() : "",false, null, 1, 1, TextAlignment.CENTER));
                        tablaG.addCell(createSelectionCell(Boolean.TRUE.equals(diag.getEsPresuntivo()) ? "X" : ""));
                        tablaG.addCell(createSelectionCell(Boolean.TRUE.equals(diag.getEsDefinitivo()) ? "X" : ""));
                        }
                }
                document.add(tablaG);
}
                // Sección H: APTITUD MÉDICA PARA EL TRABAJO
{
                float[] anchoColsH = { 120f, 30f, 120f, 30f, 120f, 30f, 120f, 30f };
                Table tablaH = new Table(anchoColsH).setWidth(tablaAncho);
                tablaH.setBorder(bordeExterno);
                tablaH.setMarginBottom(8);

                tablaH.addCell(createCell("H. APTITUD MÉDICA PARA EL TRABAJO", true, lila, 8, 1,
                                TextAlignment.LEFT));
                String resultado = aptitud != null ? aptitud.getResultadoAptitud() : "";
                tablaH.addCell(createCell("APTO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createSelectionCell("APTO".equals(resultado) ? "X" : ""));
                tablaH.addCell(createCell("APTO EN OBSERVACIÓN", true, verde, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createSelectionCell("APTO EN OBSERVACIÓN".equals(resultado) ? "X" : ""));
                tablaH.addCell(createCell("APTO CON LIMITACIONES", true, verde, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createSelectionCell("APTO CON LIMITACIONES".equals(resultado) ? "X" : ""));
                tablaH.addCell(createCell("NO APTO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createSelectionCell("NO APTO".equals(resultado) ? "X" : ""));
                tablaH.addCell(createCell("Observación", true, celeste, 1, 1, TextAlignment.LEFT));
                tablaH.addCell(createCell(aptitud != null ? aptitud.getDetalleObservaciones() : "", false, null,8, 1, TextAlignment.LEFT));
                tablaH.addCell(createCell("Limitación", true, celeste, 1, 1, TextAlignment.LEFT));
                tablaH.addCell(createCell(aptitud != null ? aptitud.getLimitacion() : "", false, null, 8, 1,TextAlignment.LEFT));
                tablaH.addCell(createCell("Reubicación", true, celeste, 1, 1, TextAlignment.LEFT));
                tablaH.addCell(createCell(aptitud != null ? aptitud.getReubicacion() : "", false, null, 8, 1,TextAlignment.LEFT));
                document.add(tablaH);
}
                // Sección I: RECOMENDACIONES Y/O TRATAMIENTO
{
                Table tablaI = new Table(anchoCols).setWidth(tablaAncho);
                tablaI.setBorder(bordeExterno);
                tablaI.setMarginBottom(8);
                tablaI.addCell(createCell("I. RECOMENDACIONES Y/O TRATAMIENTO", true, lila, 1, 1,TextAlignment.LEFT));
                 String recomendacionesStr = "";
                if (recomendacionesList != null && !recomendacionesList.isEmpty()) {
                        for (Recomendaciones r : recomendacionesList) {
                            if (r.getDescripcion() != null && !r.getDescripcion().isEmpty()) {recomendacionesStr += r.getDescripcion() + " | ";
                            }                        
                        }
                }
                tablaI.addCell(createCellParagraph(new Paragraph()
                        .add(new Text("Descripción: ").setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(recomendacionesStr.toString())),null, 1, 1, TextAlignment.LEFT));
                document.add(tablaI);
}
                // Sección J y K: Datos del profesional y firma del usuario
{
            float[] anchoColsP = {1f, 1f, 3f, 1f, 2.5f, 2.5f, 5f};
            Table tablaJK = new Table(anchoColsP).setWidth(tablaAncho);
            tablaJK.setBorder(bordeExterno);
            tablaJK.setMarginBottom(8);
            tablaJK.addCell(createCell("J. DATOS DEL PROFESIONAL", true, lila, 6, 1, TextAlignment.LEFT));
            tablaJK.addCell(createCell("K. FIRMA DEL USUARIO",true, lila, 1, 2, TextAlignment.LEFT));
            tablaJK.addCell(createCell("FECHA", true, verde, 1, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell("HORA", true, verde, 1, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell("NOMBRES Y APELLIDOS", true, verde, 1, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell("CÓDIGO", true, verde, 1, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell("FIRMA Y SELLO", true, verde, 2, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell(datosProfesional != null && datosProfesional.getFecha() != null? datosProfesional.getFecha().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell(datosProfesional != null && datosProfesional.getHora() != null? datosProfesional.getHora().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell(datosProfesional != null ? datosProfesional.getNombresApellidos() : "", false,null, 1, 1, TextAlignment.CENTER));
            tablaJK.addCell(createCell(datosProfesional != null ? datosProfesional.getCodigoProfesional() : "", false,null, 1, 1, TextAlignment.CENTER));
            Cell firmaCellP = createCell("", false, null, 2, 1, TextAlignment.LEFT, fontNormal, fontSize);
            firmaCellP.setHeight(60);
            firmaCellP.setNextRenderer(new MySignatureFieldEvent(firmaCellP, "firma_profesional", document.getPdfDocument()));
            tablaJK.addCell(firmaCellP);
            Cell firmaCell = createCell("", false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSize);
            firmaCell.setHeight(60); 
            firmaCell.setNextRenderer(new MySignatureFieldEvent(firmaCell, "firma_usuario", document.getPdfDocument()));
            tablaJK.addCell(firmaCell);
            document.add(tablaJK);
}
                document.close();
                // Guardar en carpeta Descargas
                String userHome = System.getProperty("user.home");
                String baseName = "HCU079Evaluación_Reintegro_"
                                + (empleado != null && empleado.getCedula() != null ? empleado.getCedula()
                                                : "");
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
                        System.out.println("PDF guardado exitosamente en " + rutaDescargas);
                } catch (Exception ex) {
                        System.out.println("Error al guardar el PDF: " + ex.getMessage());
                }
                // Devolver el PDF en base64
                String base64Pdf = java.util.Base64.getEncoder().encodeToString(out.toByteArray());
                return new DocumentoBase64Dto(base64Pdf);

        } catch (Exception e) {
                System.out.println("Error al generar el PDF de evaluación de reintegro: " + e.getMessage());
                e.printStackTrace();
                return null;
        }
}

}
