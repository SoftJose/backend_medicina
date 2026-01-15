package saitel.medicina.reportes;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import lombok.RequiredArgsConstructor;
import saitel.medicina.dto.DocumentoBase64Dto;
import saitel.medicina.entity.*;
import saitel.medicina.event.MySignatureFieldEvent;
import saitel.medicina.repository.EvaluacionRepository;
import saitel.medicina.service.ImpLog.*;

@Service
@RequiredArgsConstructor
public class HCU078EvaluacionPeriodicaReporte {
    private final EvaluacionRepository evaluacionRepository;
    private final DatosEmpleadoServiceImpLog datosEmpleadoServiceImpLog;
    private final MotivoConsultaServiceImpLog motivoConsultaServiceImpLog;
    private final AntecedenteFamiliarServiceImpLog antecedenteFamiliarServiceImpLog;
    private final EnfermedadActualServiceImpLog enfermedadActualServiceImpLog;
    private final AntecedenteTrabajoServiceImpLog antecedentesTrabajoServiceImpLog;
    private final AntecedentesServiceImpLog antecedentesServiceImpLog;
    private final SignoVitalServiceImpLog signoVitalServiceImpLog;
    private final ExamenFisicoServiceImpLog examenFisicoServiceImpLog;
    private final FactoresRTServiceImpLog factoresRTServiceImpLog;
    private final RevisionOrganosSistemasServiceImpLog revisionOrganosSistemasServiceImpLog;
    private final ExamenServiceImpLog examenServiceImpLog;
    private final DiagnosticoServiceImpLog diagnosticoServiceImpLog;
    private final AptitudMedicaServiceImpLog aptitudMedicaServiceImpLog;
    private final RecomendacionTratamientoServiceImpLog recomendacionTratamientoServiceImpLog;
    private final DatosProfesionalServiceImpLog datosProfesionalServiceImpLog;
        DeviceRgb gris_claro = new DeviceRgb(143, 140, 140);
        DeviceRgb celeste = new DeviceRgb(211, 244, 245);
        DeviceRgb verde = new DeviceRgb(226, 247, 210);
        DeviceRgb lila = new DeviceRgb(203, 208, 242);
        float tablaAncho = PageSize.A4.getWidth() - 20f;
        float bordeGrosor = 1f;
        float[] anchoCols = { 575f };
        SolidBorder bordeExterno = new SolidBorder(gris_claro, bordeGrosor);
        // Definición global de fuentes
        private PdfFont fontNormal;
        private PdfFont fontBold;
         private final float fontSize = 8f;
        private final float fontSizeM = 7f;
        private final float fontSizeS = 5f;

       // Método auxiliar para crear celdas con formato
    private Cell createCell(String texto, boolean bold, Color bgColor, int colspan,
            int rowspan, TextAlignment alignment) {
        return createCell(texto, bold, bgColor, colspan, rowspan, alignment, null, 6f);
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

    // Método para crear celda vertical
    private Cell createVerticalHeaderCell(String text, boolean isHeader, Color bgColor, int rowspan, int colspan, PdfFont font, float fontSize) {
        if (font == null) {
            try {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            } catch (Exception e) {
            }
        }
        Paragraph p = new Paragraph(text)
                .setFont(font)
                .setFontSize(fontSize)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setRotationAngle(Math.toRadians(90))
                .setMargin(0)
                .setPadding(0);
        Cell cell = new Cell(rowspan, colspan)
                .add(p)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMinHeight(70f)
                .setPadding(2f);
        if (isHeader && bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }
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
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            p.setFont(font);
        } catch (Exception e) {
            try {
                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                p.setFont(font);
            } catch (Exception ex) {
                // fallback: do nothing
            }
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
        PdfFont font = null;
        try {
            if (bold) {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            } else {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            }
        } catch (Exception e) {
        }
        if (font == null) {
            try {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            } catch (Exception e) {
            }
        }
        Paragraph p = new Paragraph(texto != null ? texto : "").setFontSize(fontSize);
        if (font != null) {
            p.setFont(font);
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
                        canvas.showTextAligned(new Paragraph("SNS-MSP / Form. HCU 078 / 2019")
                                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                        .setFontSize(7), pageSize.getLeft() + 20, y, TextAlignment.LEFT);
                        canvas.showTextAligned(new Paragraph("EVALUACIÓN - PERIODICA")
                                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                                        .setFontSize(7), pageSize.getRight() - 20, y, TextAlignment.RIGHT);
                        canvas.close();
                } catch (java.io.IOException e) {
                        e.printStackTrace();
                }
        }
         
        public DocumentoBase64Dto generarpdf(Integer idEvaluacion) {
        // Cargar todos los datos necesarios
         if (idEvaluacion == null) {
        throw new IllegalArgumentException("El idEvaluacion no puede ser nulo");
       }
        Optional<Evaluacion> evaluacionOpt = evaluacionRepository.findById(idEvaluacion);
                if (evaluacionOpt.isEmpty())
                throw new IllegalArgumentException("Evaluación no encontrada");
        Evaluacion evaluacion = evaluacionOpt.get();
        FichaSocial ficha = evaluacion.getFichaSocial();
        DatosEmpleados empleado = null;
        if (ficha != null && ficha.getIdEmpleado() != null) {empleado = datosEmpleadoServiceImpLog
                .obtenerPorId(ficha.getIdEmpleado())
                .orElse(null);
        }
        MotivoConsulta motivoConsulta = null;
        List<MotivoConsulta> motivoConsultaList = motivoConsultaServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (motivoConsultaList != null && !motivoConsultaList.isEmpty())motivoConsulta = motivoConsultaList.get(0);
        List<Antecedentes> antecedentes = antecedentesServiceImpLog.findByIdEvaluacion(idEvaluacion);
        Antecedentes antecedente = (antecedentes != null && !antecedentes.isEmpty()) ? antecedentes.get(0) : null;
        List<AntecedentesTrabajo> antecedentesTrabajo = antecedentesTrabajoServiceImpLog
                .findByIdEvaluacion(idEvaluacion);
        List<AntecedentesFamiliares> antecedentesFamiliaresList = antecedenteFamiliarServiceImpLog
                .findByIdEvaluacion(idEvaluacion);
         List<FactoresRiesgoTrabajo> factoresRT = factoresRTServiceImpLog.findByIdEvaluacion(idEvaluacion);
        EnfermedadActual enfermedadActual = null;
        List<EnfermedadActual> enfermedadActualList = enfermedadActualServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (enfermedadActualList != null && !enfermedadActualList.isEmpty())
            enfermedadActual = enfermedadActualList.get(0);
        List<RevisionOrganosSistemas> revisionOrganosSistemasList = revisionOrganosSistemasServiceImpLog
                .findByIdEvaluacion(idEvaluacion);
        SignoVital signoVital = null;
        List<SignoVital> signoVitalList = signoVitalServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (signoVitalList != null && !signoVitalList.isEmpty())signoVital = signoVitalList.get(0);
        ExamenFisico examenFisico = null;
        List<ExamenFisico> examenFisicoList = examenFisicoServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (examenFisicoList != null && !examenFisicoList.isEmpty())examenFisico = examenFisicoList.get(0);
        List<Examenes> examenList = examenServiceImpLog.findByIdEvaluacion(idEvaluacion);
        List<Diagnostico> diagnosticoList = diagnosticoServiceImpLog.findByIdEvaluacion(idEvaluacion);
        AptitudLaboral aptitud = aptitudMedicaServiceImpLog.findByIdEvaluacion(idEvaluacion).orElse(null);
        List<Recomendaciones> recomendacionesList = recomendacionTratamientoServiceImpLog
                .findByIdEvaluacion(idEvaluacion);
        DatosProfesional datosProfesional = null;
        List<DatosProfesional> datosProfesionalList = datosProfesionalServiceImpLog.findByIdEvaluacion(idEvaluacion);
        if (datosProfesionalList != null && !datosProfesionalList.isEmpty())datosProfesional = datosProfesionalList.get(0);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                PdfWriter writer = new PdfWriter(out);
                PdfDocument pdf = new PdfDocument(writer);
                pdf.addEventHandler(PdfDocumentEvent.END_PAGE, (IEventHandler) event -> {drawFooter((PdfDocumentEvent) event);});
                Document document = new Document(pdf, PageSize.A4);
                document.setMargins(20, 10, 20, 10);
                // Título principal
                Paragraph titulo = new Paragraph("HCU-078\nEVALUACIÓN PERIODICA")
                        .setFontSize(14)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(null)
                        .setMarginBottom(5);
                document.add(titulo);

        //Sección A: DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO
{
float[] anchoColsA = { 30f, 30f, 30f, 30f, 30f, 30f, 30f};
                        Table tablaA = new Table(anchoColsA).setWidth(tablaAncho);
                        tablaA.setBorder(bordeExterno);
                        tablaA.setMarginBottom(8);
                        // Título
                        tablaA.addCell(createCell("A. DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO", true, lila, 7, 1,TextAlignment.LEFT));
                        // Encabezados
                        tablaA.addCell(createCell("INSTITUCIÓN DEL SISTEMA O NOMBRE DE LA EMPRESA", true, verde, 2, 1,
                                        TextAlignment.CENTER));
                        tablaA.addCell(createCell("RUC", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("CIIU", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("ESTABLECIMIENTO DE SALUD", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("NÚMERO DE HISTORIA CLÍNICA", true, verde, 1, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell("NÚMERO DE ARCHIVO", true, verde, 1, 1, TextAlignment.CENTER));

                        // Datos
                        tablaA.addCell(createCell("SAITEL - " + (empleado != null ? empleado.getSucursal() : ""), false,null, 2, 1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("1091728857001", false, null, 1, 1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("J619.04", false, null, 1, 1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("DEPARTAMENTO MEDICO - SAITEL", false, null, 1, 1,TextAlignment.LEFT));
                        tablaA.addCell(createCell(empleado != null ? empleado.getCedula() : "", false, null, 1,1, TextAlignment.LEFT));
                        tablaA.addCell(createCell("00 - ", false, null, 1, 1, TextAlignment.CENTER));
                        // Encabezados 2
                        tablaA.addCell(createCell("PRIMER APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("SEGUNDO APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("PRIMER NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("SEGUNDO NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("SEXO", true, verde, 1, 1, TextAlignment.CENTER));
                        tablaA.addCell(createCell("PUESTO DE TRABAJO (CIUO)", true, verde, 2, 1, TextAlignment.CENTER));

                        // Datos2
                        tablaA.addCell(createCell(empleado != null ? empleado.getPrimerApellido() : "", false, null, 1,1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getSegundoApellido() : "", false, null, 1,1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getPrimerNombre() : "", false, null, 1, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getSegundoNombre() : "", false, null, 1,1, TextAlignment.CENTER));
                        tablaA.addCell(createCell(empleado != null ? empleado.getSexo() : "", false, null, 1, 1,TextAlignment.CENTER));
                        tablaA.addCell(createCell(evaluacion != null && evaluacion.getPuestoCiuo() != null ? evaluacion.getPuestoCiuo() : "",false, null, 2, 1, TextAlignment.CENTER));
                }
        //Sección B: MOTIVO CONSULTA
{
           float[] anchoColsB = { 575f };
            Table tablaB = new Table(anchoColsB).setWidth(tablaAncho);
            tablaB.setBorder(bordeExterno);
            tablaB.setMarginBottom(8);
            tablaB.addCell(createCell("B. MOTIVO DE CONSULTA", true, lila, 1, 1,TextAlignment.LEFT));
            tablaB.addCell(createCellParagraph(new Paragraph()
                    .add(new Text("Descripción: ")
                            .setFont(PdfFontFactory
                                    .createFont(StandardFonts.HELVETICA_BOLD)))
                    .add(new Text(motivoConsulta != null && motivoConsulta
                            .getMotivo() != null ? motivoConsulta.getMotivo() : "")),
                    null, 1, 1, TextAlignment.LEFT));
            document.add(tablaB);
}
        //Sección C: ANTECEDENTES PERSONALES
{
float[] anchoColsC = { 15F, 5F, 5F, 10F, 15F, 15F, 15F, 15F, 10F, 5F, 5F, 5F, 5F, 8F, 8F, 8F, 8F, 8F, 8F,8F };
            Table tablaC = new Table(anchoColsC).setWidth(tablaAncho);
            tablaC.setBorder(bordeExterno);
            tablaC.setMarginBottom(8);
            tablaC.addCell(createCell("C. ANTECEDENTES PERSONALES", true, lila, 20, 1, TextAlignment.LEFT));
            tablaC.addCell(createCell("ANTECEDENTES CLÍNICOS Y QUIRÚRGICOS", true, verde, 20, 1,TextAlignment.LEFT));
            if (antecedente != null && antecedente.getDescripcionClinicoQuirurgico() != null) {
                Object descObj = antecedente.getDescripcionClinicoQuirurgico();
                if (descObj instanceof java.util.List) {
                    java.util.List<?> lista = (java.util.List<?>) descObj;
                    if (!lista.isEmpty()) {
                        for (Object desc : lista) {
                            tablaC.addCell(createCell(desc != null ? desc.toString() : "",
                                    false, null, 20, 1, TextAlignment.LEFT));
                        }
                    } else {
                        tablaC.addCell(createCell(" ", false, null, 20, 1, TextAlignment.LEFT));
                    }
                } else {
                    tablaC.addCell(createCell(descObj.toString(), false, null, 20, 1,
                            TextAlignment.LEFT));
                }
            } else {
                tablaC.addCell(createCell(" ", false, null, 20, 1, TextAlignment.LEFT));
            }
            tablaC.addCell(createCell("HÁBITOS TÓXICOS", true, verde, 8, 1, TextAlignment.LEFT));
            tablaC.addCell(createCell("ESTILO DE VIDA", true, verde, 12, 1, TextAlignment.LEFT));
            tablaC.addCell(createCell("CONSUMOS NOCIVOS", true, celeste, 2, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("SI", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("NO", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("TIEMPO\n(años)", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("CANTIDAD", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("EX CONSUMIDOR", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("TIEMPO DE\n ABSTINENCIA(años)", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("ESTILO", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("SI", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("NO", true, celeste, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("¿CUÁL?", true, celeste, 7, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("TIEMPO / CANTIDAD", true, celeste, 2, 1, TextAlignment.CENTER));
            //Subencabezados Consumos Nocivos y datos
            tablaC.addCell(createCell("TABACO", true, null, 2, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.TRUE.equals(antecedente.getConsumoTabaco()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getConsumoTabaco()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getTiempoTabacoMeses() != null ? antecedente.getTiempoTabacoMeses().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getCantidadTabaco() != null ? antecedente.getCantidadTabaco().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getExConsumidorTabaco()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getAbstinenciaTabacoMeses() != null ? antecedente.getAbstinenciaTabacoMeses().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("ACTIVIDAD FÍSICA", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.TRUE.equals(antecedente.getActividadFisica()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getActividadFisica()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getDescripcionActividadFisica() != null ? antecedente.getDescripcionActividadFisica().toString() : "0", false, null, 7, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getActividadFisicaDiasSemana() != null ? antecedente.getActividadFisicaDiasSemana().toString() : "0", false, null, 2, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("ALCOHOL", true, null, 2, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.TRUE.equals(antecedente.getConsumoAlcohol()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getConsumoAlcohol()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getTiempoAlcoholMeses() != null ? antecedente.getTiempoAlcoholMeses().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getCantidadAlcohol() != null ? antecedente.getCantidadAlcohol().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getExConsumidorAlcohol()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getAbstinenciaAlcoholMeses() != null ? antecedente.getAbstinenciaAlcoholMeses().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell("MEDICACIÓN HABITUAL", true, null, 1, 2, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.TRUE.equals(antecedente.getMedicacionHabitual()) ? "X" : "", true, null, 1, 2, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getMedicacionHabitual()) ? "X" : "", true, null, 1, 2, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getDescripcionMedicacion() != null ? antecedente.getDescripcionMedicacion().toString() : "0", false, null, 7, 2, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getCantidadMedicacionUnidad() != null ? antecedente.getCantidadMedicacionUnidad().toString() : "0", false, null, 2, 2, TextAlignment.CENTER));
            tablaC.addCell(createCell("OTRAS DROGAS:", true, null, 2, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.TRUE.equals(antecedente.getConsumoOtrasDrogas()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getConsumoOtrasDrogas()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getTiempoOtrasDrogasMeses () != null ? antecedente.getTiempoOtrasDrogasMeses().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getCantidadOtrasDrogas() != null ? antecedente.getCantidadOtrasDrogas().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && Boolean.FALSE.equals(antecedente.getExConsumidorOtrasDrogas()) ? "X" : "", true, null, 1, 1, TextAlignment.CENTER));
            tablaC.addCell(createCell(antecedente != null && antecedente.getAbstinenciaOtrasDrogasMeses() != null ? antecedente.getAbstinenciaOtrasDrogasMeses().toString() : "0", false, null, 1, 1, TextAlignment.CENTER));
            //PONER INCIDENTES
            tablaC.addCell(createCell("INCIDENTES", true, verde, 20, 1,TextAlignment.LEFT));
            if (antecedente != null && antecedente.getIncidentes() != null) {
                Object descObj = antecedente.getIncidentes();
                if (descObj instanceof java.util.List) {
                    java.util.List<?> lista = (java.util.List<?>) descObj;
                    if (!lista.isEmpty()) {
                        for (Object desc : lista) {
                            tablaC.addCell(createCell(desc != null ? desc.toString() : "",false, null, 20, 1, TextAlignment.LEFT));
                        }
                    } else {
                        tablaC.addCell(createCell(" ", false, null, 20, 1, TextAlignment.LEFT));
                    }
                } else {
                    tablaC.addCell(createCell(descObj.toString(), false, null, 20, 1,TextAlignment.LEFT));
                }
            } else {
                tablaC.addCell(createCell(" ", false, null, 20, 1, TextAlignment.LEFT));}

            tablaC.addCell(createCell("ACCIDENTES DE TRABAJO (DESCRIPCIÓN)", true, verde, 20, 1,TextAlignment.LEFT));
                if (antecedentesTrabajo != null && !antecedentesTrabajo.isEmpty()) {
                 for (AntecedentesTrabajo accidente : antecedentesTrabajo) {
                        tablaC.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true, null, 2, 1, TextAlignment.LEFT, 5));
                        tablaC.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));
                        tablaC.addCell(createSelectionCell(Boolean.TRUE.equals(accidente.getAccidenteCalificado()) ? "X": ""));
                        tablaC.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("ESPECIFICAR: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(accidente.getAccidenteEspecificar() != null? accidente.getAccidenteEspecificar().trim(): "")),null, 1, 1, TextAlignment.LEFT));
                        tablaC.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                        tablaC.addCell(createSelectionCell(Boolean.FALSE.equals(accidente.getAccidenteCalificado()) ? "X": ""));
                        tablaC.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("FECHA: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(accidente.getAccidenteFecha() != null? accidente.getAccidenteFecha().toString(): "")),null, 2, 1, TextAlignment.LEFT));
                        // Observaciones
                        tablaC.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text(accidente.getAccidenteObservaciones() != null? accidente.getAccidenteObservaciones(): "")),null, 9, 1, TextAlignment.LEFT));
                                }
                        } else {
                        // Si no hay accidentes, dibujar la estructura vacía
                        tablaC.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true, null, 2, 1, TextAlignment.LEFT, 5));
                        tablaC.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));
                        tablaC.addCell(createSelectionCell(""));
                        tablaC.addCell(createCellParagraph(new Paragraph()
                               .add(new Text("ESPECIFICAR: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD))).add(new Text("")),null, 1, 1, TextAlignment.LEFT));
                                tablaC.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                tablaC.addCell(createSelectionCell(""));
                                tablaC.addCell(createCellParagraph(new Paragraph().add(new Text("FECHA: ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))).add(new Text("")),null, 2, 1, TextAlignment.LEFT));
                                tablaC.addCell(createCellParagraph(new Paragraph()
                                .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                .add(new Text("")),null, 9, 1, TextAlignment.LEFT));
                        }
                        // ENFERMEDADES PROFESIONALES
                        tablaC.addCell(createCell("ENFERMEDADES PROFESIONALES", true, verde, 9, 1, TextAlignment.LEFT));
                        if (antecedentesTrabajo != null && !antecedentesTrabajo.isEmpty()) {
                                for (AntecedentesTrabajo enfermedad : antecedentesTrabajo) {
                                        tablaC.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true,null, 2, 1, TextAlignment.LEFT, 5));
                                        tablaC.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                        tablaC.addCell(createSelectionCell(Boolean.TRUE.equals(enfermedad.getEnfermedadCalificada()) ? "X": ""));
                                        tablaC.addCell(createCellParagraph(new Paragraph()
                                                .add(new Text("ESPECIFICAR: ")
                                                .setFont(PdfFontFactory
                                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                                .add(new Text(enfermedad.getEnfermedadEspecificar() != null? enfermedad.getEnfermedadEspecificar().trim(): "").setUnderline()),null, 1, 1, TextAlignment.LEFT));
                                        tablaC.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                        tablaC.addCell(createSelectionCell(Boolean.FALSE.equals(enfermedad.getEnfermedadCalificada()) ? "X": ""));
                                        tablaC.addCell(createCellParagraph(new Paragraph()
                                                .add(new Text("FECHA: ").setFont(PdfFontFactory
                                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                                .add(new Text(enfermedad.getEnfermedadFecha() != null? enfermedad.getEnfermedadFecha().toString(): "")),null, 2, 1, TextAlignment.LEFT));
                                        // Observaciones
                                        tablaC.addCell(createCellParagraph(new Paragraph()
                                                .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                                .createFont(StandardFonts.HELVETICA_BOLD)))
                                                .add(new Text(enfermedad.getEnfermedadObservaciones() != null? enfermedad.getEnfermedadObservaciones(): "")),null, 9, 1, TextAlignment.LEFT));
                                }
                        } else {
                                // Si no hay enfermedades, dibujar la estructura vacía
                                tablaC.addCell(createCellWithFontSize("FUE CALIFICADO POR EL INSTITUTO DE SEGURIDAD SOCIAL CORRESPONDIENTE:",true, null, 2, 1, TextAlignment.LEFT, 5));
                                tablaC.addCell(createCellWithFontSize("SI", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                tablaC.addCell(createSelectionCell(""));
                                tablaC.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("ESPECIFICAR: ").setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD))
                                        .setUnderline())
                                        .add(new Text("")),null, 1, 1, TextAlignment.LEFT));
                                tablaC.addCell(createCellWithFontSize("NO", true, verde, 1, 1,TextAlignment.CENTER, 6));
                                tablaC.addCell(createSelectionCell(""));
                                tablaC.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("FECHA: ").setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD)))
                                        .add(new Text("")),null, 2, 1, TextAlignment.LEFT));
                                tablaC.addCell(createCellParagraph(new Paragraph()
                                        .add(new Text("Observaciones: ").setFont(PdfFontFactory
                                        .createFont(StandardFonts.HELVETICA_BOLD)))
                                        .add(new Text("")),null, 9, 1, TextAlignment.LEFT));
                        }
            document.add(tablaC);
}
        //Sección D: ANTECEDENTES FAMILIARES (DETALLAR PARENTESCO)
{
        float[] anchoColsD = {2f, 1f, 2f, 1f, 2f, 1f, 2f, 1f,2f, 1f, 2f, 1f, 2f, 1f, 2f, 1f};
        Table tablaD = new Table(anchoColsD)
            .setWidth(UnitValue.createPercentValue(100))
            .setBorder(bordeExterno);
        tablaD.setMarginBottom(8);
        tablaD.addCell(createCell("D: ANTECEDENTES FAMILIARES (DETALLAR PARENTESCO)", true, lila, 1, 1,TextAlignment.LEFT));

        // Lista de enfermedades
        String[] enfermedades = {
                "1. ENFERMEDAD CARDIO-VASCULAR",
                "2. ENFERMEDAD METABÓLICA",
                "3. ENFERMEDAD NEUROLÓGICA",
                "4. ENFERMEDAD ONCOLÓGICA",
                "5. ENFERMEDAD INFECCIOSA",
                "6. ENFERMEDAD HEREDITARIA / CONGÉNITA",
                "7. DISCAPACIDADES",
                "8. OTROS"
        };
        for (String enfermedad : enfermedades) {
            tablaD.addCell(createCell(enfermedad,true,verde,1,1,TextAlignment.LEFT,fontNormal,fontSizeM));boolean tieneEnfermedad = antecedentesFamiliaresList != null && !antecedentesFamiliaresList.isEmpty() &&antecedentesFamiliaresList.stream()
                .anyMatch(ant -> ant.getIdTipoEnfermedad() != null &&enfermedad.toUpperCase().contains(ant.getIdTipoEnfermedad().getNombre().toUpperCase()));
            tablaD.addCell(createCell(tieneEnfermedad ? "X" : "",false,null,1,1,TextAlignment.CENTER,fontNormal,fontSizeM));
        }
        StringBuilder descripcionesStr = new StringBuilder();
        if (antecedentesFamiliaresList != null) {
        for (AntecedentesFamiliares af : antecedentesFamiliaresList) {
        String desc = af != null ? af.getDescripcion() : null;
        if (desc != null && !desc.isEmpty()) {
            descripcionesStr.append(" | ").append(desc).append("\n");
        }}}
        tablaD.addCell(createCellParagraph(new Paragraph()
        .add(new Text("Descripción: ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
        .add(new Text(descripcionesStr.toString())),null, 16, 1, TextAlignment.LEFT));
        document.add(tablaD);
}
        //Sección E: FACTORES DE RIESGOS DEL PUESTO DE TRABAJO
{
        float[] anchoColsE1 = {3f, 4f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f  };
        Table tablaE1 = new Table(anchoColsE1)
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(bordeExterno);
                tablaE1.setMarginBottom(8);
        String[] riesgosFisicos = {
                "Temperatura alta", "Temperatura baja", "Radiación ionizante",
                "Radiación no ionizante", "Ruido", "Vibración","Iluminación","Ventilación","Fluido eléctrico","Otros ______"
        };
        String[] riesgosMecanicos = { "Atrapamiento entre máquinas","Atrapamiento entre superficies",
                "Atrapamiento entre objetos", "Caída de objetos","Caídas mismo nivel", "Caídas diferente nivel",
                "Contacto eléctrico", "Contacto con superficies de trabajos", 
                "Proyección de partículas – fragmentos","Proyección de fluidos", "Pinchazos", "Cortes", "Atropellamientos por vehículos", "Choques /colisión vehicular","Otros ______"
        };
        String[] riesgosQuimicos = {
                "Sólidos","Polvos", "Humos","Líquidos", "Vapores", "Aerosoles",
                "Neblinas", "Gaseosos",  "Otros ______"
        };
        //Encabezados principales
        tablaE1.addCell(createCell("E. FACTORES DE RIESGO DEL PUESTO DE TRABAJO ACTUAL", true, lila, 36, 1, TextAlignment.LEFT));
        tablaE1.addCell(createCell("PUESTO DE TRABAJO / ÁREA", true, verde, 1, 2, TextAlignment.CENTER, fontBold, fontSizeM));
        tablaE1.addCell(createCell("ACTIVIDADES", true, verde, 1, 2, TextAlignment.CENTER, fontBold, fontSizeM));
        // Encabezados principales que ocupan múltiples columnas
        tablaE1.addCell(createCell("FÍSICO", true, verde, 10, 1, TextAlignment.CENTER, fontBold, fontSizeM));
        tablaE1.addCell(createCell("MECÁNICO", true, verde, 15, 1, TextAlignment.CENTER, fontBold, fontSizeM));
        tablaE1.addCell(createCell("QUÍMICO", true, verde, 9, 1, TextAlignment.CENTER, fontBold, fontSizeM));
        // Encabezados secundarios - verticales
        for (String riesgo : riesgosFisicos) {
            tablaE1.addCell(createVerticalHeaderCell(riesgo, true, verde, 1, 1, fontNormal, fontSizeS));
        }
        for (String riesgo : riesgosMecanicos) {
            tablaE1.addCell(createVerticalHeaderCell(riesgo, true, verde, 1, 1, fontNormal, fontSizeS));
        }
        for (String riesgo : riesgosQuimicos) {
            tablaE1.addCell(createVerticalHeaderCell(riesgo, true, verde, 1, 1, fontNormal, fontSizeS));
        }
        // Datos
        if (factoresRT != null && !factoresRT.isEmpty()) {
            for (FactoresRiesgoTrabajo factor : factoresRT) {
                tablaE1.addCell(createCell(factor.getPuestoTrabajo() != null ? factor.getPuestoTrabajo() : "",false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(factor.getActividades() != null ? factor.getActividades() : "",false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
                // RIESGOS FÍSICOS
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoTempAlta()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoTempBaja()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoRadiacionIonizante()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoRadiacionNoIonizante()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoRuido()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoVibracion()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoIluminacion()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoVentilacion()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoFluidoElectrico()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell((factor.getRiesgoFisicoOtros() != null && !factor.getRiesgoFisicoOtros().toString().trim().isEmpty()) ? "X" : "",false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                // RIESGOS MECÁNICOS
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoAtrapamientoMaquinas()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoAtrapamientoSuperficies()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoAtrapamientoObjetos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoCaidaObjetos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoCaidaMismoNivel()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoCaidaDiferenteNivel()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoContactoPartesFluido()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoContactoSuperficiesTrabajo()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoProyeccionParticulas()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoProyeccionFluido()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoPinchazos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoCortes()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoAtropellamientoVehiculos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoChoqueVehicular()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell((factor.getRiesgoMecanicoOtros() != null && !factor.getRiesgoMecanicoOtros().toString().trim().isEmpty()) ? "X" : "",false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                // RIESGOS QUÍMICOS
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoSolidos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoPolvos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoHumos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoLiquidos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoVapores()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoAerosoles()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoNeblinas()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoGases()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell(factor.getRiesgoQuimicoOtros() != null && !factor.getRiesgoQuimicoOtros().trim().isEmpty() ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
            }
        } else {
            // Si no hay datos, mostrar filas vacías numeradas
            for (int i = 1; i <= 4; i++) {
                tablaE1.addCell(createCell(i + ".", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE1.addCell(createCell("", false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
                for (int j = 0; j < 5; j++) {
                    tablaE1.addCell(createCell("", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                }
            }
        }
        document.add(tablaE1);

        // SEGUNDA TABLA: BIOLÓGICO, ERGONÓMICO, PSICOSOCIAL
        float[] anchoColsE2 = {3f, 4f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 3f  };
        Table tablaE2 = new Table(anchoColsE2)
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(bordeExterno);
        tablaE2.setMarginBottom(8);
        String[] riesgosBiologicos = {
                "Virus", "Hongos", "Bacterias", "Parásitos", "Exposición a vectores",
                "Exposición animales selváticos", "Otros ______"
        };
        String[] riesgosErgonomicos = {
                "Manejo cargas", "Movimientos repetitivos", "Posturas forzadas",
                "Trabajo PVD", "Otros ______"
        };
        String[] riesgosPsicosociales = {
                "Monotonía del trabajo", "Sobrecarga Laboral", "Minuciosidad de la tarea", "Alta responsabilidad",
                "Autonomía en la forma de las decisiones", "Supervisión y estilos de dirección deficiente", "Conflicto rol", "Falta de claridad en las funciones",
                "Incorrecta distribución del trabajo", "Turnos rotativos", "Relaciones interpersonales", "Inestabilidad laboral", "Otros ______"
        };
        // Encabezados principales
        tablaE2.addCell(createCell("PUESTO DE TRABAJO / ÁREA", true, verde, 1, 2, TextAlignment.CENTER));
        tablaE2.addCell(createCell("ACTIVIDADES", true, verde, 1, 2, TextAlignment.CENTER));

        tablaE2.addCell(createCell("BIOLÓGICO", true, verde, 7, 1, TextAlignment.CENTER));
        tablaE2.addCell(createCell("ERGONÓMICO", true, verde, 5, 1, TextAlignment.CENTER));
        tablaE2.addCell(createCell("PSICOSOCIAL", true, verde, 13, 1, TextAlignment.CENTER));
        tablaE2.addCell(createCell("MEDIDAS PREVENTIVAS", true, verde, 1, 2, TextAlignment.CENTER));
        // Encabezados secundarios - verticales
        for (String riesgo : riesgosBiologicos) {
            tablaE2.addCell(createVerticalHeaderCell(riesgo, true, verde, 1, 1, fontNormal, fontSizeS));
        }
        for (String riesgo : riesgosErgonomicos) {
            tablaE2.addCell(createVerticalHeaderCell(riesgo, true, verde, 1, 1, fontNormal, fontSizeS));
        }
        for (String riesgo : riesgosPsicosociales) {
            tablaE2.addCell(createVerticalHeaderCell(riesgo, true, verde, 1, 1, fontNormal, fontSizeS));
        }
        // Datos
        if (factoresRT != null && !factoresRT.isEmpty()) {
            for (FactoresRiesgoTrabajo factor : factoresRT) {
                tablaE2.addCell(createCell(factor.getPuestoTrabajo() != null ? factor.getPuestoTrabajo() : "",false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(factor.getActividades() != null ? factor.getActividades() : "",false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
                // RIESGOS BIOLÓGICOS
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoVirus()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoHongos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoBacterias()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoParasitos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoExposicionVector()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoExposicionAnimales()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(factor.getRiesgoBiologicoOtros() != null && !factor.getRiesgoBiologicoOtros().trim().isEmpty() ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                // RIESGOS ERGONÓMICOS
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoManejoCargas()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoMovimientosRepetitivos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoPosturasForzadas()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoTrabajoPvd()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(factor.getRiesgoErgonomicoOtros() != null && !factor.getRiesgoErgonomicoOtros().trim().isEmpty() ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                // RIESGOS PSICOSOCIALES
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoMonotonia()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoCantidadTarea()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoResponsabilidad()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoAltaExigencia()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoSupervisionAutoridad()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoConflictoRol()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoDefinicionRol()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoFaltaAutonomia()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoInversionTrabajo()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoTurnos()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoRelacionesInterpersonales()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(Boolean.TRUE.equals(factor.getRiesgoInestabilidadLaboral()) ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell(factor.getRiesgoPsicosocialOtros() != null && !factor.getRiesgoPsicosocialOtros().trim().isEmpty() ? "X" : "", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                // MEDIDAS PREVENTIVAS
                tablaE2.addCell(createCell(factor.getMedidasPreventivas() != null ? factor.getMedidasPreventivas() : "",false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
            }
        } else {
            // Si no hay datos, mostrar filas vacías numeradas
            for (int i = 1; i <= 4; i++) {
                tablaE2.addCell(createCell(i + ".", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                tablaE2.addCell(createCell("", false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
                // 25 celdas vacías para los riesgos (7+5+13)
                for (int j = 0; j < 25; j++) {
                    tablaE2.addCell(createCell("", false, null, 1, 1, TextAlignment.CENTER, fontNormal, fontSizeM));
                }
                // Celda vacía para medidas preventivas
                tablaE2.addCell(createCell("", false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSizeM));
            }
        }
        document.add(tablaE2);
}
        //Sección F: ENFERMEDAD ACTUAL
{
        Table tablaF = new Table(anchoCols).setWidth(tablaAncho);
                tablaF.setBorder(bordeExterno);
                tablaF.setMarginBottom(8);
                tablaF.addCell(createCell("F. ENFERMEDAD ACTUAL", true, lila, 1, 1, TextAlignment.LEFT));
                tablaF.addCell(createCellParagraph(new Paragraph()
                        .add(new Text("Descripción: ")
                        .setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(enfermedadActual != null&& enfermedadActual.getDescripcion() != null? enfermedadActual.getDescripcion(): "")),null, 1, 1, TextAlignment.LEFT));
                document.add(tablaF);
}
        //Sección G: REVISIÓN DE ORGANOS Y SISTEMAS
{
        float[] anchoColsG = {2f, 0.5f, 2f, 0.5f, 2f, 0.5f, 2f, 0.5f, 2f, 0.5f};
        Table tablaG = new Table(anchoColsG)
        .setWidth(UnitValue.createPercentValue(100)).setBorder(bordeExterno);
        tablaG.addCell(createCell("G. REVISIÓN ACTUAL DE ÓRGANOS Y SISTEMAS",true,lila,10,1,TextAlignment.LEFT));
        tablaG.addCell(createCell("1. PIEL - ANEXOS", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getPielAnexos()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("3. RESPIRATORIO", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getRespiratorio()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("5. DIGESTIVO", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getDigestivo()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("7. MÚSCULO ESQUELÉTICO", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getMusculoEsqueletico()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("9. HEMOLINFÁTICO", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getHemoLinfatico()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("2. ÓRGANOS DE LOS SENTIDOS", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getOrganosSentidos()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("4. CARDIO-VASCULAR", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getCardioVascular()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("6. GENITO-URINARIO", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getGenitoUrinario()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("8. ENDOCRINO", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getEndocrino()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        tablaG.addCell(createCell("10. NERVIOSO", true, verde, 1, 1, TextAlignment.LEFT));
        tablaG.addCell(createCell(Boolean.TRUE.equals(revisionOrganosSistemasList.get(0).getNervioso()) ? "X" : "☐", false, null, 1, 1, TextAlignment.CENTER));
        StringBuilder descripciones = new StringBuilder();
        if (revisionOrganosSistemasList != null) {
         for (RevisionOrganosSistemas ros : revisionOrganosSistemasList) {
        String desc = ros != null ? ros.getDescripcion() : null;
        if (desc != null && !desc.isEmpty()) {
            descripciones.append(" | ").append(desc).append("\n");
        }}}
        tablaG.addCell(createCellParagraph(new Paragraph()
        .add(new Text("Descripción: ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
        .add(new Text(descripciones.toString())),null, 10, 1, TextAlignment.LEFT));
        document.add(tablaG);
}
        //Sección H: CONSTANTES VITALES Y ANTROPOMETRÍA
{
        float[] anchoColsH = { 64f, 64f, 64f, 64f, 64f, 64f, 64f, 64f, 63f };
                Table tablaH = new Table(anchoColsH).setWidth(tablaAncho);
                tablaH.setBorder(bordeExterno);
                tablaH.setMarginBottom(8);
                tablaH.addCell(createCell("H. CONSTANTES VITALES Y ANTROPOMETRÍA", true, lila, 9, 1,TextAlignment.LEFT));
                tablaH.addCell(createCellWithFontSize("PRESIÓN ARTERIAL", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaH.addCell(createCellWithFontSize("TEMPERATURA", true, celeste, 1, 1, TextAlignment.CENTER,8));
                tablaH.addCell(createCellWithFontSize("FRECUENCIA CARDIACA", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaH.addCell(createCellWithFontSize("SATURACIÓN DE OXÍGENO", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaH.addCell(createCellWithFontSize("FRECUENCIA RESPIRATORIA", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaH.addCell(createCellWithFontSize("PESO", true, celeste, 1, 1, TextAlignment.CENTER, 8));
                tablaH.addCell(createCellWithFontSize("TALLA", true, celeste, 1, 1, TextAlignment.CENTER, 8));
                tablaH.addCell(createCellWithFontSize("ÍNDICE DE MASA CORPORAL", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                tablaH.addCell(createCellWithFontSize("PERÍMETRO ABDOMINAL", true, celeste, 1, 1,TextAlignment.CENTER, 8));
                String presionArterial = "";
                if (signoVital != null && signoVital.getPresionArterialA() != null&& signoVital.getPresionArterialB() != null) {
                        presionArterial = signoVital.getPresionArterialA() + "/"+ signoVital.getPresionArterialB();
                }
                tablaH.addCell(createCell(presionArterial, false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getTemperatura() != null? String.valueOf(signoVital.getTemperatura()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getPerimetroAbdominal() != null? String.valueOf(signoVital.getPerimetroAbdominal()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getSaturacionOxg() != null? String.valueOf(signoVital.getSaturacionOxg()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getFrecuenciaRespiratoria() != null? String.valueOf(signoVital.getFrecuenciaRespiratoria()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getPeso() != null? String.valueOf(signoVital.getPeso()): "",false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getTalla() != null? String.valueOf(signoVital.getTalla()): "",false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getMasaCorporal() != null? String.valueOf(signoVital.getMasaCorporal()): "", false, null, 1, 1, TextAlignment.CENTER));
                tablaH.addCell(createCell(signoVital != null && signoVital.getPerimetroAbdominal() != null? String.valueOf(signoVital.getPerimetroAbdominal()): "", false, null, 1, 1, TextAlignment.CENTER));
                document.add(tablaH);
}
        //Sección I: EXAMEN FÍSICO
{
        float anchoNumero = 18f;
                float anchoLetra = 20f;
                float anchoX = 12f;
                float[] anchoColsI = {anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX, anchoNumero, anchoLetra, anchoX};
                Table tablaI = new Table(anchoColsI).setWidth(tablaAncho);
                tablaI.setBorder(bordeExterno);
                tablaI.setMarginBottom(8);
                // Título
                tablaI.addCell(createCell("I. EXAMEN FÍSICO REGIONAL", true, lila, 15, 1, TextAlignment.LEFT));
                tablaI.addCell(createCell("REGIONES", true, verde, 15, 1, TextAlignment.LEFT));
                Cell celdaPiel = new Cell(3, 1);
                Paragraph pPiel = new Paragraph("1. Piel").setFontSize(6).setRotationAngle(Math.PI / 2);
                celdaPiel.add(pPiel);
                celdaPiel.setBackgroundColor(celeste);
                celdaPiel.setTextAlignment(TextAlignment.CENTER);
                celdaPiel.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celdaPiel.setHeight(30f);
                tablaI.addCell(celdaPiel);
                tablaI.addCell(createCell("a. Cicatrices", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCicatrices()) ? "X" : ""));
                Cell celdaOido = new Cell(3, 1);
                Paragraph pOido = new Paragraph("3. Oído").setFontSize(6).setRotationAngle(Math.PI / 2);
                celdaOido.add(pOido);
                celdaOido.setBackgroundColor(celeste);
                celdaOido.setTextAlignment(TextAlignment.CENTER);
                celdaOido.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celdaOido.setHeight(30f);
                tablaI.addCell(celdaOido);
                tablaI.addCell(createCell("a. C. auditivo externo", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getConductoAuditivoExterno()) ? "X" : ""));
                Cell celdanariz = new Cell(4, 1); // 4 filas, 1 columna
                Paragraph pNariz = new Paragraph("5. Nariz").setFontSize(6).setRotationAngle(Math.PI / 2);
                celdanariz.add(pNariz);
                celdanariz.setBackgroundColor(celeste);
                celdanariz.setTextAlignment(TextAlignment.CENTER);
                celdanariz.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celdanariz.setHeight(30f);
                tablaI.addCell(celdanariz);
                tablaI.addCell(createCell("a. Tabique", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTabique()) ? "X" : ""));
                Cell celtoraxx = new Cell(2, 1); // 4 filas, 1 columna
                Paragraph pToraxx = new Paragraph("8. Tórax").setFontSize(6).setRotationAngle(Math.PI / 2);
                celtoraxx.add(pToraxx);
                celtoraxx.setBackgroundColor(celeste);
                celtoraxx.setTextAlignment(TextAlignment.CENTER);
                celtoraxx.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celtoraxx.setHeight(30f);
                tablaI.addCell(celtoraxx);
                tablaI.addCell(createCell("a. Pulmones", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPulmones()) ? "X" : ""));
                Cell celpelvis = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph pPelvis = new Paragraph("11. Pelvis").setFontSize(6).setRotationAngle(Math.PI / 2);
                celpelvis.add(pPelvis);
                celpelvis.setBackgroundColor(celeste);
                celpelvis.setTextAlignment(TextAlignment.CENTER);
                celpelvis.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celpelvis.setHeight(30f);
                tablaI.addCell(celpelvis);
                tablaI.addCell(createCell("a.Pelvis", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPelvis()) ? "X" : ""));
                tablaI.addCell(createCell("b. Tatuajes", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTatuajes()) ? "X" : ""));
                tablaI.addCell(createCell("b. Pabellón", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPabellon()) ? "X" : ""));
                tablaI.addCell(createCell("b. Cornetes", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCornetes()) ? "X" : ""));
                tablaI.addCell(createCell("b. Parrilla Costal", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParrillaCostal()) ? "X" : ""));
                tablaI.addCell(createCell("b. Genitales", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getGenitales()) ? "X" : ""));
                tablaI.addCell(createCell("c. Piel y Faneras", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPielFaneras()) ? "X" : ""));
                tablaI.addCell(createCell("c. Tímpanos", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTimpanos()) ? "X" : ""));
                tablaI.addCell(createCell("c. Mucosas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMucosas()) ? "X" : ""));
                Cell celabdomen = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph pAbdomen = new Paragraph("9. Abdomen").setFontSize(6).setRotationAngle(Math.PI / 2);
                celabdomen.add(pAbdomen);
                celabdomen.setBackgroundColor(celeste);
                celabdomen.setTextAlignment(TextAlignment.RIGHT);
                celabdomen.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celabdomen.setHeight(30f);
                tablaI.addCell(celabdomen);
                tablaI.addCell(createCell("a.  Vísceras", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getVisceras()) ? "X" : ""));
                Cell celextremidades = new Cell(3, 1); // 3 filas, 1 columna
                Paragraph pExtremidades = new Paragraph("12. Extremidades").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celextremidades.add(pExtremidades);
                celextremidades.setBackgroundColor(celeste);
                celextremidades.setTextAlignment(TextAlignment.RIGHT);
                celextremidades.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celextremidades.setHeight(30f);
                tablaI.addCell(celextremidades);
                tablaI.addCell(createCell("a. Vascular", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getVascular()) ? "X" : ""));
                Cell celojos = new Cell(5, 1); // 5 filas, 1 columna
                Paragraph pOjosParagraph = new Paragraph("2. Ojos").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celojos.add(pOjosParagraph);
                celojos.setBackgroundColor(celeste);
                celojos.setTextAlignment(TextAlignment.CENTER);
                celojos.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celojos.setHeight(30f);
                tablaI.addCell(celojos);
                tablaI.addCell(createCell("a. Párpados", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParpados()) ? "X" : ""));
                Cell celorofaringe = new Cell(5, 1); // 5 filas, 1 columna
                Paragraph pOrofaringe = new Paragraph("4. Oro Faringe").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celorofaringe.add(pOrofaringe);
                celorofaringe.setBackgroundColor(celeste);
                celorofaringe.setTextAlignment(TextAlignment.CENTER);
                celorofaringe.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celorofaringe.setHeight(30f);
                tablaI.addCell(celorofaringe);
                tablaI.addCell(createCell("a. Labios", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getLabios()) ? "X" : ""));
                tablaI.addCell(createCell("d. Senos paranasales", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getSenosParanasales()) ? "X" : ""));
                tablaI.addCell(createCell("b. Pared abdominal", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getParedAbdominal()) ? "X" : ""));
                tablaI.addCell(createCell("b. Miembros superiores", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMiembrosSuperiores()) ? "X" : ""));
                tablaI.addCell(createCell("b. Conjuntivas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getConjuntivas()) ? "X" : ""));
                tablaI.addCell(createCell("b. Lengua", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getLengua()) ? "X" : ""));
                Cell celcuello = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph pcuello = new Paragraph("6. Cuello").setFontSize(6).setRotationAngle(Math.PI / 2);
                celcuello.add(pcuello);
                celcuello.setBackgroundColor(celeste);
                celcuello.setTextAlignment(TextAlignment.CENTER);
                celcuello.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celcuello.setHeight(30f);
                tablaI.addCell(celcuello);
                tablaI.addCell(createCell("a. Tiroides / masas", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getTiroidesMasas()) ? "X" : ""));
                Cell celcolumna = new Cell(4, 1); // 4 filas, 1 columna
                Paragraph pcolumna = new Paragraph("10. Columna").setFontSize(6).setRotationAngle(Math.PI / 2);
                celcolumna.add(pcolumna);
                celcolumna.setBackgroundColor(celeste);
                celcolumna.setTextAlignment(TextAlignment.CENTER);
                celcolumna.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celcolumna.setHeight(30f);
                tablaI.addCell(celcolumna);
                tablaI.addCell(createCell("a. Flexibilidad", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFlexibilidad()) ? "X" : ""));
                tablaI.addCell(createCell("c. Miembros inferiores", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMiembrosInferiores()) ? "X" : ""));
                tablaI.addCell(createCell("c.Pupilas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getPupilas()) ? "X" : ""));
                tablaI.addCell(createCell("c. Faringe", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFaringe()) ? "X" : ""));
                tablaI.addCell(createCell("b. Movilidad", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMovilidadCuello()) ? "X" : ""));
                tablaI.addCell(createCell("b. Desviación", false, celeste, 1, 2, TextAlignment.LEFT));
                Cell celdaDesviacionResp = new Cell(2, 1);
                celdaDesviacionResp.add(new Paragraph(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDesviacion()) ? "X" : ""));
                celdaDesviacionResp.setTextAlignment(TextAlignment.CENTER);
                celdaDesviacionResp.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                tablaI.addCell(celdaDesviacionResp);
                Cell celneurologico = new Cell(4, 1); // 4 filas, 1 columna
                Paragraph pneurologico = new Paragraph("13. Neurologico").setFontSize(6)
                        .setRotationAngle(Math.PI / 2);
                celneurologico.add(pneurologico);
                celneurologico.setBackgroundColor(celeste);
                celneurologico.setTextAlignment(TextAlignment.CENTER);
                celneurologico.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celneurologico.setHeight(30f);
                tablaI.addCell(celneurologico);
                tablaI.addCell(createCell("a. Fuerza", false, celeste, 1, 1,TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getFuerza()) ? "X" : ""));
                tablaI.addCell(createCell("d. Córnea", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCornea()) ? "X" : ""));
                tablaI.addCell(createCell("d. Amígdalas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getAmigdalas()) ? "X" : ""));
                Cell celtorax = new Cell(2, 1); // 2 filas, 1 columna
                Paragraph ptorax = new Paragraph("7. Torax").setFontSize(6).setRotationAngle(Math.PI / 2);
                celtorax.add(ptorax);
                celtorax.setBackgroundColor(celeste);
                celtorax.setTextAlignment(TextAlignment.CENTER);
                celtorax.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                celtorax.setHeight(30f);
                tablaI.addCell(celtorax);
                tablaI.addCell(createCell("a. Mamas", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMamas()) ? "X" : ""));
                tablaI.addCell(createCell("b. Sensibilidad", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getSensibilidad()) ? "X" : ""));
                tablaI.addCell(createCell("e. Motilidad", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMotilidadOcular()) ? "X" : ""));
                tablaI.addCell(createCell("e. Dentadura", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDentadura()) ? "X" : ""));
                tablaI.addCell(createCell("b. Corazón", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getCorazon()) ? "X" : ""));
                tablaI.addCell(createCell("c. Dolor", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getDolor()) ? "X" : ""));
                tablaI.addCell(createCell("c. Marcha", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getMarcha()) ? "X" : ""));
                tablaI.addCell(createCellWithFontSize("CP = CON EVIDENCIA DE PATOLOGÍA: MARCAR 'X' Y DESCRIBIR EN LA SIGUIENTE SECCIÓN",false, null, 6, 1, TextAlignment.LEFT, 6));
                tablaI.addCell(createCellWithFontSize("SP = SIN EVIDENCIA DE PATOLOGÍA: MARCAR 'X' Y NO DESCRIBIR",false, null, 6, 1, TextAlignment.LEFT, 6));
                tablaI.addCell(createCell("d. Reflejos", false, celeste, 1, 1, TextAlignment.LEFT));
                tablaI.addCell(createSelectionCell(examenFisico != null && Boolean.TRUE.equals(examenFisico.getReflejos()) ? "X" : ""));
                tablaI.addCell(
                createCellParagraph(new Paragraph()
                        .add(new Text("Observaciones: ")
                        .setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(examenFisico != null&& examenFisico.getObservaciones() != null? examenFisico.getObservaciones(): "")),null, 15, 1, TextAlignment.LEFT));
                document.add(tablaI);
}
        //Sección J: RESULTADOS DE EXÁMENES GENERALES Y ESPECÍFICOS DE ACUERDO AL RIESGO Y PUESTO DE TRABAJO (IMAGEN, LABORATORIO, OTROS Y OTROS)
{
        float[] anchoColsJ = { 150f, 100f, 325f };
                Table tablaJ = new Table(anchoColsJ).setWidth(tablaAncho);
                tablaJ.setBorder(bordeExterno);
                tablaJ.setMarginBottom(8);
                tablaJ.addCell(createCell("J: RESULTADOS DE LABORATORIO Y ESPECIFICOS DE ACUERDO AL PUESTO DE TRABAJO (IMAGENES, LABORATORIOS, OTROS)", true, lila,3, 1,TextAlignment.LEFT));
                // Encabezado
                tablaJ.addCell(createCell("EXAMEN", true, verde, 1, 1, TextAlignment.CENTER));
                tablaJ.addCell(createCell("FECHA\naaaa / mm / dd", true, verde, 1, 1, TextAlignment.CENTER));
                tablaJ.addCell(createCell("RESULTADO", true, verde, 1, 1, TextAlignment.CENTER));
                // Filas de exámenes
                if (examenList != null && !examenList.isEmpty()) {
                        for (Examenes ex : examenList) {
                                tablaJ.addCell(createCell(ex.getNombreExamen() != null ? ex.getNombreExamen(): "",false, null,1, 1, TextAlignment.LEFT));
                                tablaJ.addCell(createCell(
                                                ex.getFechaExamen() != null? ex.getFechaExamen().toString(): "",false, null, 1, 1, TextAlignment.CENTER));
                                tablaJ.addCell(createCell(ex.getResultado() != null ? ex.getResultado() : "",false, null, 1, 1,TextAlignment.LEFT));
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
                tablaJ.addCell(createCellParagraph( new Paragraph()
                        .add(new Text("Observaciones: ").setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(observaciones.trim())),null, 3, 1, TextAlignment.LEFT));
                document.add(tablaJ);
}
        //Sección K: DIAGNÓSTICO
{
        float[] anchoColsK = { 30f, 400f, 80f, 40f, 40f };
                Table tablaK = new Table(anchoColsK).setWidth(tablaAncho);
                tablaK.setBorder(bordeExterno);
                tablaK.setMarginBottom(8);
                tablaK.addCell(createCell("K. DIAGNÓSTICO", true, lila, 2, 1, TextAlignment.LEFT));
                tablaK.addCell(createCell("PRE= PRESUNTIVO    DEF= DEFINITIVO", true, lila, 3, 1, TextAlignment.LEFT));
                tablaK.addCell(createCell("Nº", false, verde, 1, 1, TextAlignment.CENTER)); // Número
                tablaK.addCell(createCell("Descripción", true, verde, 1, 1, TextAlignment.LEFT));
                tablaK.addCell(createCell("CIE", true, verde, 1, 1, TextAlignment.CENTER));
                tablaK.addCell(createCell("PRE", true, verde, 1, 1, TextAlignment.CENTER));
                tablaK.addCell(createCell("DEF", true, verde, 1, 1, TextAlignment.CENTER));
                if (diagnosticoList != null && !diagnosticoList.isEmpty()) {
                        for (int i = 0; i < diagnosticoList.size(); i++) {
                        Diagnostico diag = diagnosticoList.get(i);
                        String num = String.valueOf(i + 1);
                        tablaK.addCell(createCell(num, false, verde, 1, 1, TextAlignment.CENTER));
                        tablaK.addCell(createCell(diag.getDescripcion() != null ? diag.getDescripcion() : "",false, null, 1, 1, TextAlignment.LEFT));
                        tablaK.addCell(createCell(diag.getCie() != null ? diag.getCie() : "",false, null, 1, 1, TextAlignment.CENTER));
                        tablaK.addCell(createSelectionCell(Boolean.TRUE.equals(diag.getEsPresuntivo()) ? "X" : ""));
                        tablaK.addCell(createSelectionCell(Boolean.TRUE.equals(diag.getEsDefinitivo()) ? "X" : ""));
                        }
                }
                document.add(tablaK);
}
        //Sección L: APTITUD MÉDICA PARA EL TRABAJO
{
        float[] anchoColsL = { 120f, 30f, 120f, 30f, 120f, 30f, 120f, 30f };
            Table tablaL = new Table(anchoColsL).setWidth(tablaAncho);
            tablaL.setBorder(bordeExterno);
            tablaL.setMarginBottom(8);
            tablaL.addCell(createCell("L. APTITUD MÉDICA PARA EL TRABAJO", true, lila, 8, 1,TextAlignment.LEFT));
            String resultado = aptitud != null ? aptitud.getResultadoAptitud() : "";
                tablaL.addCell(createCell("APTO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaL.addCell(createSelectionCell("APTO".equals(resultado) ? "X" : ""));
                tablaL.addCell(createCell("APTO EN OBSERVACIÓN", true, verde, 1, 1, TextAlignment.CENTER));
                tablaL.addCell(createSelectionCell("APTO EN OBSERVACIÓN".equals(resultado) ? "X" : ""));
                tablaL.addCell(createCell("APTO CON LIMITACIONES", true, verde, 1, 1, TextAlignment.CENTER));
                tablaL.addCell(createSelectionCell("APTO CON LIMITACIONES".equals(resultado) ? "X" : ""));
                tablaL.addCell(createCell("NO APTO", true, verde, 1, 1, TextAlignment.CENTER));
                tablaL.addCell(createSelectionCell("NO APTO".equals(resultado) ? "X" : ""));
                tablaL.addCell(createCell("Observación", true, celeste, 1, 1, TextAlignment.LEFT));
                tablaL.addCell(createCell(aptitud != null ? aptitud.getDetalleObservaciones() : "", false, null,8, 1, TextAlignment.LEFT));
                tablaL.addCell(createCell("Limitación", true, celeste, 1, 1, TextAlignment.LEFT));
                tablaL.addCell(createCell(aptitud != null ? aptitud.getLimitacion() : "", false, null, 8, 1,TextAlignment.LEFT));
                document.add(tablaL);
}
        //Sección M: RECOMENDACIONES Y/O TRATAMIENTO
{
        Table tablaM = new Table(anchoCols).setWidth(tablaAncho);
                tablaM.setBorder(bordeExterno);
                tablaM.setMarginBottom(8);
                tablaM.addCell(createCell("M. RECOMENDACIONES Y/O TRATAMIENTO", true, lila, 1, 1,TextAlignment.LEFT));
                 String recomendacionesStr = "";
                if (recomendacionesList != null && !recomendacionesList.isEmpty()) {
                        for (Recomendaciones r : recomendacionesList) {
                            if (r.getDescripcion() != null && !r.getDescripcion().isEmpty()) {recomendacionesStr += r.getDescripcion() + " | ";
                            }                        
                        }
                }
                tablaM.addCell(createCellParagraph(new Paragraph()
                        .add(new Text("Descripción: ").setFont(PdfFontFactory
                        .createFont(StandardFonts.HELVETICA_BOLD)))
                        .add(new Text(recomendacionesStr.toString())),null, 1, 1, TextAlignment.LEFT));
                document.add(tablaM);
}
        //Sección N: DATOS DEL PROFESIONAL Y O: FIRMA DEL USUARIO
{
        float[] anchoColsNO = {1f, 1f, 3f, 1f, 2.5f, 2.5f, 5f};
            Table tablaNO = new Table(anchoColsNO).setWidth(tablaAncho);
            tablaNO.setBorder(bordeExterno);
            tablaNO.setMarginBottom(8);
            tablaNO.addCell(createCell("N. DATOS DEL PROFESIONAL", true, lila, 6, 1, TextAlignment.LEFT));
            tablaNO.addCell(createCell("O. FIRMA DEL USUARIO",true, lila, 1, 2, TextAlignment.LEFT));
            tablaNO.addCell(createCell("FECHA", true, verde, 1, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell("HORA", true, verde, 1, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell("NOMBRES Y APELLIDOS", true, verde, 1, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell("CÓDIGO", true, verde, 1, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell("FIRMA Y SELLO", true, verde, 2, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell(datosProfesional != null && datosProfesional.getFecha() != null? datosProfesional.getFecha().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell(datosProfesional != null && datosProfesional.getHora() != null? datosProfesional.getHora().toString(): "", false, null, 1, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell(datosProfesional != null ? datosProfesional.getNombresApellidos() : "", false,null, 1, 1, TextAlignment.CENTER));
            tablaNO.addCell(createCell(datosProfesional != null ? datosProfesional.getCodigoProfesional() : "", false,null, 1, 1, TextAlignment.CENTER));
            Cell firmaCellP = createCell("", false, null, 2, 1, TextAlignment.LEFT, fontNormal, fontSize);
            firmaCellP.setHeight(60);
            firmaCellP.setNextRenderer(new MySignatureFieldEvent(firmaCellP, "firma_profesional", document.getPdfDocument()));
            tablaNO.addCell(firmaCellP);
            Cell firmaCell = createCell("", false, null, 1, 1, TextAlignment.LEFT, fontNormal, fontSize);
            firmaCell.setHeight(60); 
            firmaCell.setNextRenderer(new MySignatureFieldEvent(firmaCell, "firma_usuario", document.getPdfDocument()));
            tablaNO.addCell(firmaCell);
            document.add(tablaNO);
}

  document.close();

        //Generar PDF y guardarlo en carpeta Descargas
        String userHome = System.getProperty("user.home");
        String baseName = "HCU079Evaluación_Retiro_"+ (empleado != null && empleado.getCedula() != null ? empleado.getCedula(): "");
        String extension = ".pdf";
        String downloadsDir = userHome + "/Downloads/";
        String rutaDescargas = downloadsDir + baseName + extension;
        int contador = 1;
        java.io.File archivo = new java.io.File(rutaDescargas);
                        while (archivo.exists()) {rutaDescargas = downloadsDir + baseName + "(" + contador + ")" + extension;
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
        




