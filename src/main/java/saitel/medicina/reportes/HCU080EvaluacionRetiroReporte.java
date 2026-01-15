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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saitel.medicina.entity.*;
import saitel.medicina.event.MySignatureFieldEvent;
import saitel.medicina.repository.*;
import saitel.medicina.service.ImpLog.AntecedenteTrabajoServiceImpLog;
import saitel.medicina.service.ImpLog.AntecedentesServiceImpLog;
import saitel.medicina.service.ImpLog.DatosEmpleadoServiceImpLog;
import saitel.medicina.service.ImpLog.DatosProfesionalServiceImpLog;
import saitel.medicina.service.ImpLog.DetallesEvaluacionRetiroImpLog;
import saitel.medicina.service.ImpLog.DiagnosticoServiceImpLog;
import saitel.medicina.service.ImpLog.ExamenFisicoServiceImpLog;
import saitel.medicina.service.ImpLog.ExamenServiceImpLog;
import saitel.medicina.service.ImpLog.FactoresRTServiceImpLog;
import saitel.medicina.service.ImpLog.RecomendacionTratamientoServiceImpLog;
import saitel.medicina.service.ImpLog.SignoVitalServiceImpLog;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HCU080EvaluacionRetiroReporte {
        private final EvaluacionRepository evaluacionRepository;
        private final DatosEmpleadoServiceImpLog datosEmpleadoServiceImpLog;
        private final FactoresRTServiceImpLog factoresRTServiceImpLog;
        private final AntecedentesServiceImpLog antecedentesServiceImpLog;
        private final AntecedenteTrabajoServiceImpLog antecedentesTrabajoServiceImpLog;
        private final SignoVitalServiceImpLog signoVitalServiceImpLog;
        private final ExamenFisicoServiceImpLog examenFisicoServiceImpLog;
        private final ExamenServiceImpLog examenServiceImpLog;
        private final DiagnosticoServiceImpLog diagnosticoServiceImpLog;
        private final DetallesEvaluacionRetiroImpLog detallesEvaluacionRetiroImpLog;
        private final RecomendacionTratamientoServiceImpLog recomendacionTratamientoServiceImpLog;
        private final DatosProfesionalServiceImpLog datosProfesionalServiceImpLog;
        DeviceRgb gris_claro = new DeviceRgb(143, 140, 140);
        DeviceRgb celeste = new DeviceRgb(211, 244, 245);
        DeviceRgb verde = new DeviceRgb(226, 247, 210);
        DeviceRgb lila = new DeviceRgb(203, 208, 242);
        float tablaAncho = PageSize.A4.getWidth() - 20f;
        float bordeGrosor = 1f;
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
                cell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                return cell;
        }

        // Método para dibujar el pie de página
        private void drawFooter(PdfDocumentEvent docEvent) {
                PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage());
                Rectangle pageSize = docEvent.getPage().getPageSize();
                float y = pageSize.getBottom() + 15;
                try {
                        Canvas canvas = new Canvas(pdfCanvas, pageSize);
                        canvas.showTextAligned(new Paragraph("SNS-MSP / Form. HCU 080 / 2019")
                                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                        .setFontSize(7), pageSize.getLeft() + 20, y, TextAlignment.LEFT);
                        canvas.showTextAligned(new Paragraph("EVALUACIÓN - RETIRO")
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
                DatosEmpleados empleado = null;
                if (ficha != null && ficha.getIdEmpleado() != null) {
                        empleado = datosEmpleadoServiceImpLog.obtenerPorId(ficha.getIdEmpleado()).orElse(null);
                }

                FactoresRiesgoTrabajo factor = null;
                List<FactoresRiesgoTrabajo> factoresRTList = factoresRTServiceImpLog.findByIdEvaluacion(idEvaluacion);
                if (factoresRTList != null && !factoresRTList.isEmpty())
                        factor = factoresRTList.get(0);
                Antecedentes antecedente = null;
                List<Antecedentes> antecedentes = antecedentesServiceImpLog.findByIdEvaluacion(idEvaluacion);
                if (antecedentes != null && !antecedentes.isEmpty())
                        antecedente = antecedentes.get(0);
                AntecedentesTrabajo antecedenteTrabajo = null;
                List<AntecedentesTrabajo> antecedentesTrabajo = antecedentesTrabajoServiceImpLog
                        .findByIdEvaluacion(idEvaluacion);
                if (antecedentesTrabajo != null && !antecedentesTrabajo.isEmpty())antecedenteTrabajo = antecedentesTrabajo.get(0);
                List<Examenes> examenList = examenServiceImpLog.findByIdEvaluacion(idEvaluacion);
                SignoVital signoVital = null;
                List<SignoVital> signoVitalList = signoVitalServiceImpLog.findByIdEvaluacion(idEvaluacion);
                if (signoVitalList != null && !signoVitalList.isEmpty())
                        signoVital = signoVitalList.get(0);
                ExamenFisico examenFisico = null;
                List<ExamenFisico> examenFisicoList = examenFisicoServiceImpLog.findByIdEvaluacion(idEvaluacion);
                if (examenFisicoList != null && !examenFisicoList.isEmpty())
                        examenFisico = examenFisicoList.get(0);
                List<Diagnostico> diagnostico = diagnosticoServiceImpLog.findByIdEvaluacion(idEvaluacion);
                DetalleEvaluacionRetiro detalleEvaluacionRetiro = null;
                List<DetalleEvaluacionRetiro> detalleEvaluacionRetiroList = detallesEvaluacionRetiroImpLog
                        .findByIdEvaluacion(idEvaluacion);
                if (detalleEvaluacionRetiroList != null && !detalleEvaluacionRetiroList.isEmpty())
                        detalleEvaluacionRetiro = detalleEvaluacionRetiroList.get(0);
                List<Recomendaciones> recomendaciones = recomendacionTratamientoServiceImpLog
                        .findByIdEvaluacion(idEvaluacion);
                DatosProfesional datosProfesional = null;
                List<DatosProfesional> datosProfesionalList = datosProfesionalServiceImpLog
                        .findByIdEvaluacion(idEvaluacion);
                if (datosProfesionalList != null && !datosProfesionalList.isEmpty())
                        datosProfesional = datosProfesionalList.get(0);

                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        PdfWriter writer = new PdfWriter(out);
                        PdfDocument pdf = new PdfDocument(writer);
                        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, (IEventHandler) event -> {
                                drawFooter((PdfDocumentEvent) event);
                        });
                        Document document = new Document(pdf, PageSize.A4);
                        document.setMargins(20, 10, 20, 10);
                        // Título principal
                        Paragraph titulo = new Paragraph("HCU-080\nEVALUACIÓN DE RETIRO")
                                        .setFontSize(14)
                                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setBackgroundColor(null)
                                        .setMarginBottom(5);
                        document.add(titulo);

                        // Sección A: DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO
{
                        float[] anchoColsA = { 30f, 30f, 30f, 30f, 30f, 30f, 30f, 30f, 30f, 30f };
                        Table tablaA = new Table(anchoColsA).setWidth(tablaAncho);
                        tablaA.setBorder(bordeExterno);
                        tablaA.setMarginBottom(8);
                        // Título
                        tablaA.addCell(createCell("A. DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO", true, lila, 10, 1,TextAlignment.LEFT));
                        // Encabezados
                        tablaA.addCell(createCell("INSTITUCIÓN DEL SISTEMA O NOMBRE DE LA EMPRESA", true, verde, 2, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell("RUC", true, verde, 2, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("CIIU", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("ESTABLECIMIENTO DE SALUD", true, verde, 2, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("NÚMERO DE HISTORIA CLÍNICA", true, verde, 2, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell("NÚMERO DE ARCHIVO", true, verde, 1, 1, TextAlignment.CENTER));

                        // Datos
                        tablaA.addCell(createCell("SAITEL - " + (empleado != null ? empleado.getSucursal() : ""), false,null, 2, 1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("1091728857001", false, null, 2, 1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("J619.04", false, null, 1, 1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("DEPARTAMENTO MEDICO - SAITEL", false, null, 2, 1,TextAlignment.LEFT));
                        tablaA.addCell(createCell(empleado != null ? empleado.getCedula() : "", false, null, 2, 1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("00 - ", false, null, 1, 1, TextAlignment.CENTER));
                        // Encabezados 2
                        tablaA.addCell(createCell("PRIMER APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("SEGUNDO APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("PRIMER NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("SEGUNDO NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("SEXO", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("FECHA DE INICIO DE LABORES", true, verde, 1, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell("FECHA DE SALIDA", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("TIEMPO (meses)", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("PUESTO DE TRABAJO (CIUO)", true, verde, 2, 1, TextAlignment.CENTER));

                        // Datos
                        tablaA.addCell(createCell(empleado != null ? empleado.getPrimerApellido() : "", false, null, 1,1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getSegundoApellido() : "", false, null, 1,1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getPrimerNombre() : "", false, null, 1, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getSegundoNombre() : "", false, null, 1,1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getSexo() : "", false, null, 1, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null && empleado.getFechaIngreso() != null? empleado.getFechaIngreso().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null && empleado.getFechaSalida() != null? empleado.getFechaSalida().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
                        // Calcular tiempo laborado
                        String mesesLaborados = "";
                        if (empleado != null && empleado.getFechaIngreso() != null
                                        && empleado.getFechaSalida() != null) {
                                java.time.LocalDate inicio = ((java.util.Date) empleado.getFechaIngreso()).toInstant()
                                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                                java.time.LocalDate fin = ((java.util.Date) empleado.getFechaSalida()).toInstant()
                                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                                if (!fin.isBefore(inicio)) {
                                        java.time.Period periodo = java.time.Period.between(inicio, fin);
                                        int meses = periodo.getYears() * 12 + periodo.getMonths();
                                        mesesLaborados = String.valueOf(meses);
                                }
                        }
                        tablaA.addCell(createCell(mesesLaborados, false, null, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(evaluacion != null && evaluacion.getPuestoCiuo() != null ? evaluacion.getPuestoCiuo() : "",false, null, 2, 1, TextAlignment.CENTER));
                        // Encabezados 3
                        tablaA.addCell(createCell("ACTIVIDADES", true, verde, 4, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("FACTORES DE RIESGO", true, verde, 6, 1, TextAlignment.CENTER));
                        // Datos 3 - ACTIVIDADES Y FACTORES DE RIESGO
                        if (factor != null && factor.getActividades() != null
                                        && factor.getFactorRiesgoRetiro() != null) {
                                List<String> actividades = Arrays.asList(factor.getActividades().toString().split(","));
                                List<String> factoresRiesgo = Arrays
                                                .asList(factor.getFactorRiesgoRetiro().toString().split(","));
                                boolean hayDatos = false;
                                for (int i = 0; i < Math.max(actividades.size(), factoresRiesgo.size()); i++) {
                                        String actividad = i < actividades.size() ? actividades.get(i).trim() : "";
                                        String riesgo = i < factoresRiesgo.size() ? factoresRiesgo.get(i).trim() : "";
                                        if (!actividad.isEmpty() || !riesgo.isEmpty()) {
                                                hayDatos = true;
                                                tablaA.addCell(createCell(actividad, false, null, 4, 1,TextAlignment.CENTER));
                                                tablaA.addCell(createCell(riesgo, false, null, 6, 1,TextAlignment.CENTER));
                                        }
                                }
                                if (!hayDatos) {
                                        tablaA.addCell(createCell("N/A", false, null, 4, 1, TextAlignment.CENTER));
                                        tablaA.addCell(createCell("N/A", false, null, 6, 1, TextAlignment.CENTER));
                                }
                        } else {
                                tablaA.addCell(createCell("N/A", false, null, 4, 1, TextAlignment.CENTER));
                                tablaA.addCell(createCell("N/A", false, null, 6, 1, TextAlignment.CENTER));
                        }
                        document.add(tablaA);
}
                        // Sección B: ANTECEDENTES PERSONALES
{
                        float[] anchoColsB = { 90f, 75f, 25f, 20f, 100f, 25f, 20f, 40f, 50f };
                        Table tablaB = new Table(anchoColsB).setWidth(tablaAncho);
                        tablaB.setBorder(bordeExterno);
                        tablaB.setMarginBottom(8);
                        tablaB.addCell(createCell("B. ANTECEDENTES PERSONALES", true, lila, 9, 1, TextAlignment.LEFT));
                        tablaB.addCell(createCell("ANTECEDENTES CLÍNICOS Y QUIRÚRGICOS", true, verde, 9, 1,TextAlignment.LEFT));
                        if (antecedente != null && antecedente.getDescripcionClinicoQuirurgico() != null) {
                                Object descObj = antecedente.getDescripcionClinicoQuirurgico();
                                if (descObj instanceof java.util.List) {
                                        java.util.List<?> lista = (java.util.List<?>) descObj;
                                        if (!lista.isEmpty()) {
                                                for (Object desc : lista) {
                                                        tablaB.addCell(createCell(desc != null ? desc.toString() : "",false, null, 9, 1, TextAlignment.LEFT));
                                                }
                                        } else {
                                                tablaB.addCell(createCell(" ", false, null, 9, 1, TextAlignment.LEFT));
                                        }
                                } else {
                                        tablaB.addCell(createCell(descObj.toString(), false, null, 9, 1,TextAlignment.LEFT));
                                }
                        } else {
                                tablaB.addCell(createCell(" ", false, null, 9, 1, TextAlignment.LEFT));
                        }
                        // ACCIDENTES DE TRABAJO
                tablaB.addCell(createCell("ACCIDENTES DE TRABAJO (DESCRIPCIÓN)", true, verde, 9, 1,TextAlignment.LEFT));
                if (antecedentesTrabajo != null && !antecedentesTrabajo.isEmpty()) {
                 for (AntecedentesTrabajo accidente : antecedentesTrabajo) {
                        tablaB.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true, null, 2, 1, TextAlignment.LEFT, 5));
                        tablaB.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));tablaB.addCell(createSelectionCell(Boolean.TRUE.equals(accidente.getAccidenteCalificado()) ? "X": ""));
                        tablaB.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("ESPECIFICAR: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(accidente.getAccidenteEspecificar() != null? accidente.getAccidenteEspecificar().trim(): "")),null, 1, 1, TextAlignment.LEFT));
                        tablaB.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                        tablaB.addCell(createSelectionCell(Boolean.FALSE.equals(accidente.getAccidenteCalificado()) ? "X": ""));
                        tablaB.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("FECHA: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(accidente.getAccidenteFecha() != null? accidente.getAccidenteFecha().toString(): "")),null, 2, 1, TextAlignment.LEFT));
                        // Observaciones
                        tablaB.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(accidente.getAccidenteObservaciones() != null? accidente.getAccidenteObservaciones(): "")),null, 9, 1, TextAlignment.LEFT));
                                }
                        } else {
                        // Si no hay accidentes, dibujar la estructura vacía
                        tablaB.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true, null, 2, 1, TextAlignment.LEFT, 5));
                        tablaB.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));
                        tablaB.addCell(createSelectionCell(""));
                        tablaB.addCell(createCellParagraph(new Paragraph()
                               .add(new Text("ESPECIFICAR: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD))).add(new Text("")),null, 1, 1, TextAlignment.LEFT));
                                tablaB.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                tablaB.addCell(createSelectionCell(""));
                                tablaB.addCell(createCellParagraph(new Paragraph().add(new Text("FECHA: ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))).add(new Text("")),null, 2, 1, TextAlignment.LEFT));
                                tablaB.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text("")),null, 9, 1, TextAlignment.LEFT));
                        }
                        // Concatenar múltiples descripciones de accidente con ' | '
                        String descripcionConcat = "";
                        if (antecedenteTrabajo != null && antecedenteTrabajo.getAccidenteDescripcion() != null) {
                                Object descObj = antecedenteTrabajo.getAccidenteDescripcion();
                                if (descObj instanceof java.util.List) {
                                        java.util.List<?> descList = (java.util.List<?>) descObj;
                                        descripcionConcat = descList.stream()
                                        .filter(d -> d != null && !d.toString().trim().isEmpty())
                                        .map(Object::toString)
                                        .reduce((a, b) -> a + " | " + b)
                                        .orElse("");
                                } else {
                                        descripcionConcat = descObj.toString();
                                }
                        }
                        tablaB.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("Detallar aquí en caso se presuma de algún accidente de trabajo que no haya sido reportado o calificado:")
                                .setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD))
                                .setFontSize(5))
                                .add(new Text(descripcionConcat)), null, 9, 1, TextAlignment.LEFT));
                        // ENFERMEDADES PROFESIONALES
                        tablaB.addCell(createCell("ENFERMEDADES PROFESIONALES", true, verde, 9, 1, TextAlignment.LEFT));
                        if (antecedentesTrabajo != null && !antecedentesTrabajo.isEmpty()) {
                                for (AntecedentesTrabajo enfermedad : antecedentesTrabajo) {
                                        tablaB.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true,null, 2, 1, TextAlignment.LEFT, 5));
                                        tablaB.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                        tablaB.addCell(createSelectionCell(Boolean.TRUE.equals(enfermedad.getEnfermedadCalificada()) ? "X": ""));
                                        tablaB.addCell(createCellParagraph(new Paragraph()
                                                .add(new Text("ESPECIFICAR: ")
                                                .setFont(PdfFontFactory
                                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                                .add(new Text(enfermedad.getEnfermedadEspecificar() != null? enfermedad.getEnfermedadEspecificar().trim(): "").setUnderline()),null, 1, 1, TextAlignment.LEFT));
                                        tablaB.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                        tablaB.addCell(createSelectionCell(Boolean.FALSE.equals(enfermedad.getEnfermedadCalificada()) ? "X": ""));
                                        tablaB.addCell(createCellParagraph(new Paragraph()
                                                .add(new Text("FECHA: ").setFont(PdfFontFactory
                                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                                .add(new Text(enfermedad.getEnfermedadFecha() != null? enfermedad.getEnfermedadFecha().toString(): "")),null, 2, 1, TextAlignment.LEFT));
                                        // Observaciones
                                        tablaB.addCell(createCellParagraph(new Paragraph()
                                                .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                                .add(new Text(enfermedad.getEnfermedadObservaciones() != null? enfermedad.getEnfermedadObservaciones(): "")),null, 9, 1, TextAlignment.LEFT));
                                }
                        } else {
                                // Si no hay enfermedades, dibujar la estructura vacía
                                tablaB.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true, null, 2, 1, TextAlignment.LEFT, 5));
                                tablaB.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                tablaB.addCell(createSelectionCell(""));
                                tablaB.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("ESPECIFICAR: ").setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD))
                                        .setUnderline())
                                        .add(new Text("")),null, 1, 1, TextAlignment.LEFT));
                                tablaB.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                tablaB.addCell(createSelectionCell(""));
                                tablaB.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("FECHA: ").setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD)))
                                        .add(new Text("")),null, 2, 1, TextAlignment.LEFT));
                                tablaB.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD)))
                                        .add(new Text("")),null, 9, 1, TextAlignment.LEFT));
                        }
                        // Concatenar múltiples descripciones de enfermedad con ' | '
                        String enfermedadDescripcionConcat = "";
                        if (antecedentesTrabajo != null && !antecedentesTrabajo.isEmpty()) {
                                enfermedadDescripcionConcat = antecedentesTrabajo.stream()
                                        .map(e -> e.getEnfermedadDescripcion())
                                        .filter(d -> d != null && !d.trim().isEmpty())
                                        .reduce((a, b) -> a + " | " + b)
                                        .orElse("");
                        }
                        tablaB.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("Detallar aquí en caso de que se presuma alguna enfermedad relacionada con el trabajo que no haya sido reportada o calificada:")
                                        .setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD))
                                        .setFontSize(5))
                                        .add(new Text(enfermedadDescripcionConcat)),null, 9, 1, TextAlignment.LEFT));
                        document.add(tablaB);
}
                        // Sección C: CONSTANTES VITALES Y ANTROPOMETRÍA
{
                        float[] anchoColsC = { 64f, 64f, 64f, 64f, 64f, 64f, 64f, 64f, 63f };
                        Table tablaC = new Table(anchoColsC).setWidth(tablaAncho);
                        tablaC.setBorder(bordeExterno);
                        tablaC.setMarginBottom(8);
                        tablaC.addCell(createCell("C. CONSTANTES VITALES Y ANTROPOMETRÍA", true, lila, 9, 1,
                                        TextAlignment.LEFT));
                        tablaC.addCell(createCellWithFontSize("PRESIÓN ARTERIAL", true, celeste, 1, 1,
                                        TextAlignment.CENTER, 8));
                        tablaC.addCell(createCellWithFontSize("TEMPERATURA", true, celeste, 1, 1, TextAlignment.CENTER,
                                        8));
                        tablaC.addCell(createCellWithFontSize("FRECUENCIA CARDIACA", true, celeste, 1, 1,
                                        TextAlignment.CENTER, 8));
                        tablaC.addCell(createCellWithFontSize("SATURACIÓN DE OXÍGENO", true, celeste, 1, 1,
                                        TextAlignment.CENTER, 8));
                        tablaC.addCell(createCellWithFontSize("FRECUENCIA RESPIRATORIA", true, celeste, 1, 1,
                                        TextAlignment.CENTER, 8));
                        tablaC.addCell(createCellWithFontSize("PESO", true, celeste, 1, 1, TextAlignment.CENTER, 8));
                        tablaC.addCell(createCellWithFontSize("TALLA", true, celeste, 1, 1, TextAlignment.CENTER, 8));
                        tablaC.addCell(createCellWithFontSize("ÍNDICE DE MASA CORPORAL", true, celeste, 1, 1,
                                        TextAlignment.CENTER, 8));
                        tablaC.addCell(createCellWithFontSize("PERÍMETRO ABDOMINAL", true, celeste, 1, 1,
                                        TextAlignment.CENTER, 8));
                        // Datos
                        String presionArterial = "";
                        if (signoVital != null && signoVital.getPresionArterialA() != null&& signoVital.getPresionArterialB() != null) {presionArterial = signoVital.getPresionArterialA() + "/"+ signoVital.getPresionArterialB();
                        }
                        tablaC.addCell(createCell(presionArterial, false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getTemperatura() != null? String.valueOf(signoVital.getTemperatura()): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getPerimetroAbdominal() != null? String.valueOf(signoVital.getPerimetroAbdominal()): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getSaturacionOxg() != null? String.valueOf(signoVital.getSaturacionOxg()): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getFrecuenciaRespiratoria() != null? String.valueOf(signoVital.getFrecuenciaRespiratoria()): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getPeso() != null? String.valueOf(signoVital.getPeso()): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getTalla() != null? String.valueOf(signoVital.getTalla()): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getMasaCorporal() != null? String.valueOf(signoVital.getMasaCorporal()): "", false, null, 1, 1, TextAlignment.CENTER));
                        tablaC.addCell(createCell(signoVital != null && signoVital.getPerimetroAbdominal() != null? String.valueOf(signoVital.getPerimetroAbdominal()): "", false, null, 1, 1, TextAlignment.CENTER));
                        document.add(tablaC);
}
                        // Sección D: EXAMEN FISICO REGIONAL
{
                        float anchoNumero = 18f;
                        float anchoLetra = 20f;
                        float anchoX = 12f;
                        float[] anchoColsD = {anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX};
                        Table tablaD = new Table(anchoColsD).setWidth(tablaAncho);
                        tablaD.setBorder(bordeExterno);
                        tablaD.setMarginBottom(8);
                        tablaD.setKeepTogether(false);
                        tablaD.addCell(createCell("D. EXAMEN FÍSICO REGIONAL", true, lila, 15, 1, TextAlignment.LEFT));
                        tablaD.addCell(createCell("REGIONES", true, verde, 15, 1, TextAlignment.LEFT));
                        Cell celdaPiel = new Cell(3, 1);
                        Paragraph pPiel = new Paragraph("1. Piel").setFontSize(6).setRotationAngle(Math.PI / 2);
                        celdaPiel.add(pPiel);
                        celdaPiel.setBackgroundColor(celeste);
                        celdaPiel.setTextAlignment(TextAlignment.CENTER);
                        celdaPiel.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                        celdaPiel.setHeight(30f);
                        tablaD.addCell(celdaPiel);
                        tablaD.addCell(createCell("a. Cicatrices", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCicatrices()) ? "X" : ""));
                        Cell celdaOido = new Cell(3, 1);
                        Paragraph pOido = new Paragraph("3. Oído").setFontSize(6).setRotationAngle(Math.PI / 2);
                        celdaOido.add(pOido);
                        celdaOido.setBackgroundColor(celeste);
                        celdaOido.setTextAlignment(TextAlignment.CENTER);
                        celdaOido.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                        celdaOido.setHeight(30f);
                        tablaD.addCell(celdaOido);
                        tablaD.addCell(createCell("a. C. auditivo externo", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getConductoAuditivoExterno()) ? "X" : ""));
                        Cell celdanariz = new Cell(4, 1);
                        Paragraph pNariz = new Paragraph("5. Nariz").setFontSize(6).setRotationAngle(Math.PI / 2);
                        celdanariz.add(pNariz);
                        celdanariz.setBackgroundColor(celeste);
                        celdanariz.setTextAlignment(TextAlignment.CENTER);
                        celdanariz.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                        celdanariz.setHeight(30f);
                        tablaD.addCell(celdanariz);
                        tablaD.addCell(createCell("a. Tabique", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTabique()) ? "X" : ""));
                        Cell celtoraxx = new Cell(2, 1);
                        Paragraph pToraxx = new Paragraph("8. Tórax").setFontSize(6).setRotationAngle(Math.PI / 2);
                        celtoraxx.add(pToraxx);
                        celtoraxx.setBackgroundColor(celeste);
                        celtoraxx.setTextAlignment(TextAlignment.CENTER);
                        celtoraxx.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                        celtoraxx.setHeight(30f);
                        tablaD.addCell(celtoraxx);
                        tablaD.addCell(createCell("a. Pulmones", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPulmones()) ? "X" : ""));
                        Cell celpelvis = new Cell(2, 1);
                        Paragraph pPelvis = new Paragraph("11. Pelvis").setFontSize(6).setRotationAngle(Math.PI / 2);
                        celpelvis.add(pPelvis);
                        celpelvis.setBackgroundColor(celeste);
                        celpelvis.setTextAlignment(TextAlignment.CENTER);
                        celpelvis.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                        celpelvis.setHeight(30f);
                        tablaD.addCell(celpelvis);
                        tablaD.addCell(createCell("a.Pelvis", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPelvis()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Tatuajes", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTatuajes()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Pabellón", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPabellon()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Cornetes", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCornetes()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Parrilla Costal", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParrillaCostal()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Genitales", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getGenitales()) ? "X" : ""));
                        tablaD.addCell(createCell("c. Piel y Faneras", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPielFaneras()) ? "X" : ""));
                        tablaD.addCell(createCell("c. Tímpanos", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTimpanos()) ? "X" : ""));
                        tablaD.addCell(createCell("c. Mucosas", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMucosas()) ? "X" : ""));
                        Cell celabdomen = new Cell(2, 1);
                        Paragraph pAbdomen = new Paragraph("9. Abdomen").setFontSize(6).setRotationAngle(Math.PI / 2);
                        celabdomen.add(pAbdomen);
                        celabdomen.setBackgroundColor(celeste);
                        celabdomen.setTextAlignment(TextAlignment.RIGHT);
                        celabdomen.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                        celabdomen.setHeight(30f);
                        tablaD.addCell(celabdomen);
                        tablaD.addCell(createCell("a.  Vísceras", false, celeste, 1, 1, TextAlignment.LEFT));
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getVisceras()) ? "X" : ""));
                        Cell celextremidades = new Cell(3, 1); Paragraph pExtremidades = new Paragraph("12. Extremidades").setFontSize(6).setRotationAngle(Math.PI / 2); 
                        celextremidades.add(pExtremidades); celextremidades.setBackgroundColor(celeste); celextremidades.setTextAlignment(TextAlignment.RIGHT); 
                        celextremidades.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); celextremidades.setHeight(30f); 
                        tablaD.addCell(celextremidades);
                        tablaD.addCell(createCell("a. Vascular", false, celeste, 1, 1, TextAlignment.LEFT)); System.out.println("Vascular: " + (examenFisico != null ? examenFisico.getVascular() : "null")); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getVascular()) ? "X" : ""));
                        Cell celojos = new Cell(5, 1); Paragraph pOjosParagraph = new Paragraph("2. Ojos").setFontSize(6).setRotationAngle(Math.PI / 2); celojos.add(pOjosParagraph); 
                        celojos.setBackgroundColor(celeste); celojos.setTextAlignment(TextAlignment.CENTER); 
                        celojos.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); celojos.setHeight(30f); 
                        tablaD.addCell(celojos);
                        tablaD.addCell(createCell("a. Párpados", false, celeste, 1, 1, TextAlignment.LEFT)); System.out.println("Parpados: " + (examenFisico != null ? examenFisico.getParpados() : "null")); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParpados()) ? "X" : ""));
                        Cell celorofaringe = new Cell(5, 1); Paragraph pOrofaringe = new Paragraph("4. Oro Faringe").setFontSize(6).setRotationAngle(Math.PI / 2); celorofaringe.add(pOrofaringe); celorofaringe.setBackgroundColor(celeste); celorofaringe.setTextAlignment(TextAlignment.CENTER); 
                        celorofaringe.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); 
                        celorofaringe.setHeight(30f); 
                        tablaD.addCell(celorofaringe);
                        tablaD.addCell(createCell("a. Labios", false, celeste, 1, 1, TextAlignment.LEFT)); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getLabios()) ? "X" : ""));
                        tablaD.addCell(createCell("d. Senos paranasales", false, celeste, 1, 1, TextAlignment.LEFT)); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getSenosParanasales()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Pared abdominal", false, celeste, 1, 1, TextAlignment.LEFT)); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParedAbdominal()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Miembros superiores", false, celeste, 1, 1, TextAlignment.LEFT)); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMiembrosSuperiores()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Conjuntivas", false, celeste, 1, 1, TextAlignment.LEFT)); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getConjuntivas()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Lengua", false, celeste, 1, 1, TextAlignment.LEFT)); 
                        tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getLengua()) ? "X" : ""));
                        Cell celcuello = new Cell(2, 1); Paragraph pcuello = new Paragraph("6. Cuello").setFontSize(6).setRotationAngle(Math.PI / 2); celcuello.add(pcuello); celcuello.setBackgroundColor(celeste); celcuello.setTextAlignment(TextAlignment.CENTER); 
                        celcuello.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); celcuello.setHeight(30f); tablaD.addCell(celcuello);
                        tablaD.addCell(createCell("a. Tiroides / masas", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTiroidesMasas()) ? "X" : ""));
                        Cell celcolumna = new Cell(4, 1); Paragraph pcolumna = new Paragraph("10. Columna").setFontSize(6).setRotationAngle(Math.PI / 2); celcolumna.add(pcolumna); celcolumna.setBackgroundColor(celeste); celcolumna.setTextAlignment(TextAlignment.CENTER); 
                        celcolumna.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); celcolumna.setHeight(30f); tablaD.addCell(celcolumna);
                        tablaD.addCell(createCell("a. Flexibilidad", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFlexibilidad()) ? "X" : ""));
                        tablaD.addCell(createCell("c. Miembros inferiores", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMiembrosInferiores()) ? "X" : ""));
                        tablaD.addCell(createCell("c.Pupilas", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPupilas()) ? "X" : ""));
                        tablaD.addCell(createCell("c. Faringe", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFaringe()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Movilidad", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMovilidadCuello()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Desviación", false, celeste, 1, 2, TextAlignment.LEFT)); Cell celdaDesviacionResp = new Cell(2, 1); celdaDesviacionResp.add(new Paragraph(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDesviacion()) ? "X" : "")); celdaDesviacionResp.setTextAlignment(TextAlignment.CENTER); celdaDesviacionResp.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); tablaD.addCell(celdaDesviacionResp);
                        Cell celneurologico = new Cell(4, 1); Paragraph pneurologico = new Paragraph("13. Neurologico").setFontSize(6).setRotationAngle(Math.PI / 2); celneurologico.add(pneurologico); celneurologico.setBackgroundColor(celeste); celneurologico.setTextAlignment(TextAlignment.CENTER); celneurologico.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); celneurologico.setHeight(30f); tablaD.addCell(celneurologico);
                        tablaD.addCell(createCell("a. Fuerza", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFuerza()) ? "X" : ""));
                        tablaD.addCell(createCell("d. Córnea", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCornea()) ? "X" : ""));
                        tablaD.addCell(createCell("d. Amígdalas", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getAmigdalas()) ? "X" : ""));
                        Cell celtorax = new Cell(2, 1); Paragraph ptorax = new Paragraph("7. Torax").setFontSize(6).setRotationAngle(Math.PI / 2); celtorax.add(ptorax); celtorax.setBackgroundColor(celeste); celtorax.setTextAlignment(TextAlignment.CENTER); celtorax.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE); celtorax.setHeight(30f); tablaD.addCell(celtorax);
                        tablaD.addCell(createCell("a. Mamas", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMamas()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Sensibilidad", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getSensibilidad()) ? "X" : ""));
                        tablaD.addCell(createCell("e. Motilidad", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMotilidadOcular()) ? "X" : ""));
                        tablaD.addCell(createCell("e. Dentadura", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDentadura()) ? "X" : ""));
                        tablaD.addCell(createCell("b. Corazón", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCorazon()) ? "X" : ""));
                        tablaD.addCell(createCell("c. Dolor", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDolor()) ? "X" : ""));
                        tablaD.addCell(createCell("c. Marcha", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMarcha()) ? "X" : ""));
                        tablaD.addCell(createCellWithFontSize("CP = CON EVIDENCIA DE PATOLOGÍA: MARCAR 'X' Y DESCRIBIR EN LA SIGUIENTE SECCIÓN", false, null, 6, 1, TextAlignment.LEFT, 6));
                        tablaD.addCell(createCellWithFontSize("SP = SIN EVIDENCIA DE PATOLOGÍA: MARCAR 'X' Y NO DESCRIBIR", false, null, 6, 1, TextAlignment.LEFT, 6));
                        tablaD.addCell(createCell("d. Reflejos", false, celeste, 1, 1, TextAlignment.LEFT)); tablaD.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getReflejos()) ? "X" : ""));
                        tablaD.addCell(createCellParagraph(new Paragraph().add(new Text("Observaciones: ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))).add(new Text(examenFisico != null && examenFisico.getObservaciones() != null ? examenFisico.getObservaciones() : "")), null, 15, 1, TextAlignment.LEFT));
                        document.add(tablaD);
}
                        // Sección E: RESULTADOS DE EXÁMENES (IMAGEN, LABORATORIO Y OTROS)
{
                        float[] anchoColsE = { 150f, 100f, 325f };
                        Table tablaE = new Table(anchoColsE).setWidth(tablaAncho);
                        tablaE.setBorder(bordeExterno);
                        tablaE.setMarginBottom(8);
                        // Título
                        tablaE.addCell(createCell("E. RESULTADOS DE EXÁMENES (IMAGEN, LABORATORIO Y OTROS)", true, lila,3, 1, TextAlignment.LEFT));
                        // Encabezados
                        tablaE.addCell(createCell("EXAMEN", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaE.addCell(createCell("FECHA\naaaa / mm / dd", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaE.addCell(createCell("RESULTADO", true, verde, 1, 1, TextAlignment.CENTER));
                        // Filas de exámenes
                        if (examenList != null && !examenList.isEmpty()) {
                                for (Examenes ex : examenList) {
                                tablaE.addCell(createCell(ex.getNombreExamen() != null ? ex.getNombreExamen(): "",false, null,1, 1, TextAlignment.LEFT));
                                tablaE.addCell(createCell(ex.getFechaExamen() != null? ex.getFechaExamen().toString(): "",false, null, 1, 1, TextAlignment.CENTER));
                                tablaE.addCell(createCell(ex.getResultado() != null ? ex.getResultado() : "",false, null, 1, 1,TextAlignment.LEFT));
                                }
                        }
                        // Fila de observaciones
                        String observaciones = "";
                        if (examenList != null && !examenList.isEmpty()) {
                                // Si hay observaciones en algún examen, las concatenamos
                                for (Examenes ex : examenList) {
                                        if (ex.getObservaciones() != null && !ex.getObservaciones().isEmpty()) {
                                                observaciones += ex.getObservaciones() + " | ";
                                        }
                                }
                        }
                        tablaE.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(observaciones.trim())),null, 3, 1, TextAlignment.LEFT));
                        document.add(tablaE);
}
                        // Sección F: DIAGNÓSTICO
{
                        float[] anchoColsF = { 30f, 400f, 80f, 40f, 40f };
                        Table tablaF = new Table(anchoColsF).setWidth(tablaAncho);
                        tablaF.setBorder(bordeExterno);
                        tablaF.setMarginBottom(8);
                        // Título y leyenda
                        tablaF.addCell(createCell("F. DIAGNÓSTICO", true, lila, 2, 1, TextAlignment.LEFT));
                        tablaF.addCell(createCell("PRE= PRESUNTIVO    DEF= DEFINITIVO", true, lila, 3, 1, TextAlignment.LEFT));
                        // Encabezados
                        tablaF.addCell(createCell("Nº", false, verde, 1, 1, TextAlignment.CENTER)); // Número
                        tablaF.addCell(createCell("Descripción", true, verde, 1, 1, TextAlignment.LEFT));
                        tablaF.addCell(createCell("CIE", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaF.addCell(createCell("PRE", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaF.addCell(createCell("DEF", true, verde, 1, 1, TextAlignment.CENTER));

                        if (diagnostico != null && !diagnostico.isEmpty()) {
                                for (int i = 0; i < diagnostico.size(); i++) {
                                        Diagnostico diag = diagnostico.get(i);
                                        String num = String.valueOf(i + 1);
                                        tablaF.addCell(createCell(num, false, verde, 1, 1, TextAlignment.CENTER));
                                        tablaF.addCell(createCell(diag.getDescripcion() != null ? diag.getDescripcion() : "",false, null, 1, 1, TextAlignment.LEFT));
                                        tablaF.addCell(createCell(diag.getCie() != null ? diag.getCie() : "", false,null, 1, 1, TextAlignment.CENTER));
                                        tablaF.addCell(createSelectionCell(Boolean.TRUE.equals(diag.getEsPresuntivo()) ? "X" : ""));
                                        tablaF.addCell(createSelectionCell(Boolean.TRUE.equals(diag.getEsDefinitivo()) ? "X" : ""));
                                }
                        }
                        document.add(tablaF);
}
                        // Sección G: EVALUACION MEDICA DE RETIRO
{
                        float[] anchoColsG = { 64f, 64f, 64f, 64f, 64f, 64f, 64f, 64f };
                        Table tablaG = new Table(anchoColsG).setWidth(tablaAncho);
                        tablaG.setBorder(bordeExterno);
                        tablaG.setMarginBottom(8);
                        tablaG.addCell(createCell("G. EVALUACIÓN MÉDICA DE RETIRO", true, lila, 8, 1,TextAlignment.LEFT));
                        // Encabezados con letra más pequeña
                        tablaG.addCell(createCellWithFontSize("SE REALIZÓ LA EVALUACIÓN   ", true, verde, 4, 1,
                                        TextAlignment.CENTER, 6));
                        tablaG.addCell(createCellWithFontSize("SI", true, verde, 1, 1, TextAlignment.CENTER, 6));
                        tablaG.addCell(createSelectionCell(detalleEvaluacionRetiro != null && Boolean.TRUE.equals(detalleEvaluacionRetiro.getRealizada()) ? "X": ""));
                        tablaG.addCell(createCellWithFontSize("NO", true, verde, 1, 1, TextAlignment.CENTER, 6));
                        tablaG.addCell(createSelectionCell(detalleEvaluacionRetiro != null && Boolean.TRUE.equals(detalleEvaluacionRetiro.getRealizada()) ? "X": ""));
                        StringBuilder detalleR = new StringBuilder();
                        if (detalleEvaluacionRetiroList != null && !detalleEvaluacionRetiroList.isEmpty()) {
                                for (DetalleEvaluacionRetiro d : detalleEvaluacionRetiroList) {detalleR.append(" | ").append(d.getObservaciones()).append("\n");
                                }
                        }
                        tablaG.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("Descripción: ").setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD)))
                                        .add(new Text(detalleR.toString()))
                                        .setBorderTop(Border.NO_BORDER), null, 8, 1, TextAlignment.LEFT));
                        document.add(tablaG);
}
                        // Sección H: RECOMENDACIONES Y/O TRATAMIENTO
{
                        float[] anchoColsH = { 575f };
                        Table tablaH = new Table(anchoColsH).setWidth(tablaAncho);
                        tablaH.setBorder(bordeExterno);
                        tablaH.setMarginBottom(8);
                        tablaH.addCell(createCell("H. RECOMENDACIONES Y/O TRATAMIENTO", true, lila, 1, 1,TextAlignment.LEFT));
                         String recomendacionesStr = "";
                        if (recomendaciones != null && !recomendaciones.isEmpty()) {
                        for (Recomendaciones r : recomendaciones) {
                            if (r.getDescripcion() != null && !r.getDescripcion().isEmpty()) {recomendacionesStr += r.getDescripcion() + " | ";
                            }                        
                        }
                        }
                        tablaH.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("Descripción: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(recomendacionesStr.toString())),null, 1, 1, TextAlignment.LEFT));
                        document.add(tablaH);
                        Paragraph parrafoInfo2 = new Paragraph("CERTIFICO QUE LO ANTERIORMENTE EXPRESADO EN RELACIÓN A MI ESTADO DE SALUD ES VERDAD. SE ME HA INFORMADO MI ESTADO ACTUAL DE SALUD Y LAS RECOMENDACIONES PERTINENTES.")
                                .setFontSize(8)
                                .setMarginTop(2);
                        document.add(parrafoInfo2);
}
                        // Sección I y J: Datos del profesional y firma del usuario
{
            float[] anchoColsIJ = {1f, 1f, 3f, 1f, 2.5f, 2.5f, 5f};
            Table tablaIJ = new Table(anchoColsIJ).setWidth(tablaAncho);
            tablaIJ.setBorder(bordeExterno);
            tablaIJ.setMarginBottom(8);
            tablaIJ.addCell(createCell("I. DATOS DEL PROFESIONAL", true, lila, 6, 1, TextAlignment.LEFT));
            tablaIJ.addCell(createCell("J. FIRMA DEL USUARIO",true, lila, 1, 2, TextAlignment.LEFT));
            tablaIJ.addCell(createCell("FECHA", true, verde, 1, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell("HORA", true, verde, 1, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell("NOMBRES Y APELLIDOS", true, verde, 1, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell("CÓDIGO", true, verde, 1, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell("FIRMA Y SELLO", true, verde, 2, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell(datosProfesional != null && datosProfesional.getFecha() != null? datosProfesional.getFecha().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell(datosProfesional != null && datosProfesional.getHora() != null? datosProfesional.getHora().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell(datosProfesional != null ? datosProfesional.getNombresApellidos() : "", false,null, 1, 1, TextAlignment.CENTER));
            tablaIJ.addCell(createCell(datosProfesional != null ? datosProfesional.getCodigoProfesional() : "", false,null, 1, 1, TextAlignment.CENTER));
            Cell firmaCellP = createCell("", false, null, 2, 1, TextAlignment.LEFT, fontNormal, fontSize);
            firmaCellP.setHeight(60);
            firmaCellP.setNextRenderer(new MySignatureFieldEvent(firmaCellP, "firma_profesional", document.getPdfDocument()));
            tablaIJ.addCell(firmaCellP);
            Cell firmaCell = createCell("", false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSize);
            firmaCell.setHeight(60); 
            firmaCell.setNextRenderer(new MySignatureFieldEvent(firmaCell, "firma_usuario", document.getPdfDocument()));
            tablaIJ.addCell(firmaCell);
            document.add(tablaIJ);
}
                        document.close();
                        // Guardar en carpeta Descargas
                        String userHome = System.getProperty("user.home");
                        String baseName = "HCU079Evaluación_Retiro_"+ (empleado != null && empleado.getCedula() != null ? empleado.getCedula(): "");
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
