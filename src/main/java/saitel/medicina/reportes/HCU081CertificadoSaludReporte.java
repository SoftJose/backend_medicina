package saitel.medicina.reportes;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.layout.Canvas;

import saitel.medicina.dto.DocumentoBase64Dto;
import saitel.medicina.entity.*;
import saitel.medicina.event.MySignatureFieldEvent;
import saitel.medicina.repository.*;
import saitel.medicina.service.ImpLog.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HCU081CertificadoSaludReporte {
	private final EvaluacionRepository evaluacionRepository;
	private final DatosEmpleadoServiceImpLog datosEmpleadoServiceImpLog;
	private final AptitudMedicaServiceImpLog aptitudMedicaServiceImpLog;
	private final CondicionRetiroServiceImpLog condicionesRetiroServiceImpLog;
	private final RecomendacionTratamientoServiceImpLog recomendacionTratamientoServiceImpLog;
	private final DatosGeneralesServiceImpLog datosGeneralesServiceImpLog;
	private final DatosProfesionalServiceImpLog datosProfesionalServiceImpLog;

	DeviceRgb gris_claro = new DeviceRgb(143, 140, 140);
	DeviceRgb verde = new DeviceRgb(226, 247, 210);
	DeviceRgb lila = new DeviceRgb(203, 208, 242);
		float tablaAncho = PageSize.A4.getWidth() - 20f;
        float bordeGrosor = 1f;
        private final float fontSize = 8f;
        private PdfFont fontNormal;
	SolidBorder bordeExterno = new SolidBorder(gris_claro, bordeGrosor);

	// Método auxiliar para crear celdas de selección (checkbox)
	private Cell createSelectionCell(String check) {
		Cell cell = new Cell(1, 1);
		cell.setTextAlignment(TextAlignment.CENTER);
		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		cell.setHeight(10f);
		Paragraph p = new Paragraph(check != null ? check : "");
		try {
			p.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
		} catch (Exception e) {
		}
		p.setFontColor(new DeviceRgb(0, 0, 0)); // negro
		p.setFontSize(8);
		cell.add(p);
		return cell;
	}

	// Método auxiliar para crear celdas con formato
	private Cell createCell(String texto, boolean bold, Color bgColor, int colspan, int rowspan,
			TextAlignment alignment) {
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
		cell.setFontSize(10);
		return cell;
	}

	// Método para dibujar el pie de página
	private void drawFooter(PdfDocumentEvent docEvent) {
		PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage());
		Rectangle pageSize = docEvent.getPage().getPageSize();
		float y = pageSize.getBottom() + 15;
		try {
			Canvas canvas = new Canvas(pdfCanvas, pageSize);
			canvas.showTextAligned(new Paragraph("SNS-MSP / Form. CERT. 081 / 2019")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontSize(7), pageSize.getLeft() + 20, y, TextAlignment.LEFT);
			canvas.showTextAligned(new Paragraph("CERTIFICADO DE SALUD EN EL TRABAJO")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontSize(7), pageSize.getRight() - 20, y, TextAlignment.RIGHT);
			canvas.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
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
	
	public DocumentoBase64Dto generarPdf(Integer idEvaluacion) throws Exception {
		 if (idEvaluacion == null) {
        throw new IllegalArgumentException("El idEvaluacion no puede ser nulo");
        }
		Optional<Evaluacion> evaluacionOpt = evaluacionRepository.findById(idEvaluacion);
		if (evaluacionOpt.isEmpty())
			throw new IllegalArgumentException("Evaluación no encontrada");
		Evaluacion evaluacion = evaluacionOpt.get();
		FichaSocial ficha = evaluacion.getFichaSocial();
		DatosEmpleados empleado = datosEmpleadoServiceImpLog.obtenerPorId(ficha.getIdEmpleado()).orElse(null);
		AptitudLaboral aptitud = aptitudMedicaServiceImpLog.findByIdEvaluacion(idEvaluacion).orElse(null);

		List<CondicionesRetiro> condiciones = condicionesRetiroServiceImpLog.findByIdEvaluacion(idEvaluacion);
		List<Recomendaciones> recomendaciones = recomendacionTratamientoServiceImpLog.findByIdEvaluacion(idEvaluacion);
		List<DatosGeneralesCertificados> datosGeneralesList = datosGeneralesServiceImpLog
				.findByEvaluacion(idEvaluacion);
		DatosGeneralesCertificados datosGenerales = (datosGeneralesList != null && !datosGeneralesList.isEmpty())
				? datosGeneralesList.get(0)
				: null;
		DatosProfesional datosProfesional = null;
		List<DatosProfesional> datosProfesionalList = datosProfesionalServiceImpLog.findByIdEvaluacion(idEvaluacion);
		if (datosProfesionalList != null && !datosProfesionalList.isEmpty())
			datosProfesional = datosProfesionalList.get(0);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(baos);
		PdfDocument pdf = new PdfDocument(writer);
		pdf.addEventHandler(PdfDocumentEvent.END_PAGE, (IEventHandler) event -> {
			drawFooter((PdfDocumentEvent) event);
		});
		Document document = new Document(pdf, PageSize.A4);
		document.setMargins(20, 10, 20, 10); // margen inferior aumentado

		// Título principal
		Paragraph titulo = new Paragraph("HCU-081\nCERTIFICADO DE SALUD EN EL TRABAJO")
				.setFontSize(14)
				.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
				.setTextAlignment(TextAlignment.CENTER)
				.setBackgroundColor(null)
				.setMarginBottom(5);
		document.add(titulo);
		// Sección A: DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO
{
		float[] anchoColsA = { 120f, 80f, 80f, 120f, 80f, 80f };
		Table tablaA = new Table(anchoColsA).setWidth(PageSize.A4.getWidth() - 20f);
		tablaA.setBorder(bordeExterno);
		tablaA.setMarginBottom(8);
		tablaA.addCell(
				createCell("A. DATOS DEL ESTABLECIMIENTO - EMPRESA Y USUARIO", true, lila, 6, 1, TextAlignment.LEFT));
		tablaA.addCell(
				createCell("INSTITUCIÓN DEL SISTEMA O NOMBRE DE LA EMPRESA", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("RUC", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("CIIU", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("ESTABLECIMIENTO DE SALUD", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("NÚMERO DE HISTORIA CLÍNICA", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("NÚMERO DE ARCHIVO", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("SAITEL - " + empleado != null ? empleado.getSucursal() : "", false, null, 1, 1,
				TextAlignment.LEFT));
		tablaA.addCell(createCell("1091728857001", false, null, 1, 1, TextAlignment.LEFT));
		tablaA.addCell(createCell("J619.04", false, null, 1, 1, TextAlignment.LEFT));
		tablaA.addCell(createCell("DEPARTAMENTO MEDICO - SAITEL", false, null, 1, 1, TextAlignment.LEFT));
		tablaA.addCell(createCell(empleado != null ? empleado.getCedula() : "", false, null, 1, 1,
				TextAlignment.LEFT));
		tablaA.addCell(createCell("00 - ", false, null, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("PRIMER APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("SEGUNDO APELLIDO", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("PRIMER NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("SEGUNDO NOMBRE", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("SEXO", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell("CARGO / OCUPACIÓN", true, verde, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell(empleado != null ? empleado.getPrimerApellido() : "", false, null, 1, 1,
				TextAlignment.CENTER));
		tablaA.addCell(createCell(empleado != null ? empleado.getSegundoApellido() : "", false, null, 1, 1,
				TextAlignment.CENTER));
		tablaA.addCell(createCell(empleado != null ? empleado.getPrimerNombre() : "", false, null, 1, 1,
				TextAlignment.CENTER));
		tablaA.addCell(createCell(empleado != null ? empleado.getSegundoNombre() : "", false, null, 1, 1,
				TextAlignment.CENTER));
		tablaA.addCell(createCell(empleado != null ? empleado.getSexo() : "", false, null, 1, 1, TextAlignment.CENTER));
		tablaA.addCell(createCell(empleado != null ? empleado.getCargo() : "", false, null, 1, 1, TextAlignment.LEFT));
		document.add(tablaA);
}
		// Sección B: DATOS GENERALES 
{
		float[] anchoColsB = { 120f, 120f, 60f, 60f, 60f, 60f, 60f, 60f, 60f };
		Table tablaB = new Table(anchoColsB).setWidth(PageSize.A4.getWidth() - 20f);
		tablaB.setBorder(bordeExterno);
		tablaB.setMarginBottom(8);
		tablaB.addCell(createCell("B. DATOS GENERALES", true, lila, 9, 1, TextAlignment.LEFT));
		tablaB.addCell(createCell("FECHA DE EMISIÓN", true, verde, 1, 1, TextAlignment.LEFT));
		tablaB.addCell(createCell(datosGenerales != null && datosGenerales.getFechaEmision() != null
				? datosGenerales.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				: "", false, null, 8, 1, TextAlignment.LEFT));
		tablaB.addCell(createCell("EVALUACIÓN", true, verde, 1, 1, TextAlignment.LEFT));
		String tipoEval = datosGenerales != null ? datosGenerales.getTipoEvaluacion() : "";
		tablaB.addCell(createCell("INGRESO", false, null, 1, 1, TextAlignment.CENTER));
		tablaB.addCell(createSelectionCell("INGRESO".equalsIgnoreCase(tipoEval) ? "✔" : ""));
		tablaB.addCell(createCell("PERIODICO", false, null, 1, 1, TextAlignment.CENTER));
		tablaB.addCell(createSelectionCell("PERIODICO".equalsIgnoreCase(tipoEval) ? "✔" : ""));
		tablaB.addCell(createCell("REINTEGRO", false, null, 1, 1, TextAlignment.CENTER));
		tablaB.addCell(createSelectionCell("REINTEGRO".equalsIgnoreCase(tipoEval) ? "✔" : ""));
		tablaB.addCell(createCell("SALIDA", false, null, 1, 1, TextAlignment.CENTER));
		tablaB.addCell(createSelectionCell("SALIDA".equalsIgnoreCase(tipoEval) ? "✔" : ""));
		document.add(tablaB);
}
		// Sección C: CONCEPTO PARA APTITUD LABORAL 
{
		float[] anchoColsC = { 80f, 20f, 80f, 20f, 110f, 20f, 80f, 20f };
		Table tablaC = new Table(anchoColsC).setWidth(PageSize.A4.getWidth() - 20f);
		tablaC.setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(100, 100, 100), 2f));
		tablaC.setMarginBottom(8);
		tablaC.addCell(createCell("C. CONCEPTO PARA APTITUD LABORAL", true, lila, 8, 1, TextAlignment.LEFT));
		tablaC.addCell(createCell(
				"Después de la valoración médica ocupacional se certifica que la persona en mención, es calificada como:",
				false, null, 8, 1, TextAlignment.LEFT));

		String resultado = aptitud != null ? aptitud.getResultadoAptitud() : "";
		tablaC.addCell(createCell("APTO", true, verde, 1, 1, TextAlignment.CENTER));
		tablaC.addCell(createSelectionCell(resultado.equals("APTO") ? "✔" : ""));
		tablaC.addCell(createCell("APTO EN OBSERVACIÓN", true, verde, 1, 1, TextAlignment.CENTER));
		tablaC.addCell(createSelectionCell(resultado.equals("APTO EN OBSERVACIÓN") ? "✔" : ""));
		tablaC.addCell(createCell("APTO CON LIMITACIONES", true, verde, 1, 1, TextAlignment.CENTER));
		tablaC.addCell(createSelectionCell(resultado.equals("APTO CON LIMITACIONES") ? "✔" : ""));
		tablaC.addCell(createCell("NO APTO", true, verde, 1, 1, TextAlignment.CENTER));
		tablaC.addCell(createSelectionCell(resultado.equals("NO APTO") ? "✔" : ""));

		tablaC.addCell(createCell("DETALLE DE OBSERVACIONES:", true, null, 8, 1, TextAlignment.LEFT));
		tablaC.addCell(createCell(aptitud != null ? aptitud.getDetalleObservaciones() : "", false, null, 8, 1,
				TextAlignment.LEFT));
		document.add(tablaC);
}
		// Sección D: CONDICIONES DE SALUD AL MOMENTO DEL RETIRO
{
		float[] anchoColsD = { 180f, 30f, 30f, 20f, 30f, 20f };
		Table tablaD = new Table(anchoColsD).setWidth(PageSize.A4.getWidth() - 20f);
		tablaD.setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(100, 100, 100), 2f));
		tablaD.setMarginBottom(8);
		tablaD.addCell(
				createCell("D. CONDICIONES DE SALUD AL MOMENTO DEL RETIRO", true, lila, 6, 1, TextAlignment.LEFT));
		tablaD.addCell(createCell(
				"Después de la valoración médica ocupacional se certifica las condiciones de salud al momento del retiro:",
				false, null, 2, 1, TextAlignment.LEFT));
		String resultadoCond = condiciones != null && !condiciones.isEmpty()
				? condiciones.get(0).getResultadoCondicion()
				: "";
		tablaD.addCell(createCell("SATISFACTORIO", true, verde, 1, 1, TextAlignment.LEFT));
		tablaD.addCell(createSelectionCell(resultadoCond.equals("SATISFACTORIO") ? "✔" : ""));
		tablaD.addCell(createCell("NO SATISFACTORIO", true, verde, 1, 1, TextAlignment.LEFT));
		tablaD.addCell(createSelectionCell(resultadoCond.equals("NO SATISFACTORIO") ? "✔" : ""));
		tablaD.addCell(createCell("OBSERVACIONES RELACIONADAS CON LAS CONDICIONES DE SALUD AL MOMENTO DEL RETIRO:",
				true, null, 6, 1, TextAlignment.LEFT));
		tablaD.addCell(createCell(
				condiciones != null && !condiciones.isEmpty() ? condiciones.get(0).getObservacionesRetiro() : "", false,
				null, 6, 1, TextAlignment.LEFT));
		document.add(tablaD);
}
		// Sección E: RECOMENDACIONES
{
		float[] anchoColsE = { 720f };
		Table tablaE = new Table(anchoColsE).setWidth(PageSize.A4.getWidth() - 20f);
		tablaE.setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(100, 100, 100), 2f));
		tablaE.setMarginBottom(8);
		tablaE.addCell(createCell("E. RECOMENDACIONES", true, lila, 1, 1, TextAlignment.LEFT));
		 String recomendacionesStr = "";
                if (recomendaciones != null && !recomendaciones.isEmpty()) {
                        for (Recomendaciones r : recomendaciones) {
                            if (r.getDescripcion() != null && !r.getDescripcion().isEmpty()) {recomendacionesStr += r.getDescripcion() + " | ";
                            }                        
                        }
                }
		tablaE.addCell(createCell(recomendacionesStr.toString(), false, null, 1, 3, TextAlignment.LEFT));
		document.add(tablaE);
		// Texto informativo fuera de las tablas
		Paragraph parrafoInfo1 = new Paragraph(
				"Con este documento certifico que el trabajador se ha sometido a la evaluación médica requerida para (el ingreso /la ejecución/ el reintegro y retiro) al puesto laboral y se ha informado sobre los riesgos relacionados con el trabajo emitiendo recomendaciones relacionadas con su estado de salud.")
				.setFontSize(10)
				.setBackgroundColor(verde)
				.setMarginTop(5);
		document.add(parrafoInfo1);
		Paragraph parrafoInfo2 = new Paragraph(
				"La presente certificación se expide con base en la historia ocupacional del usuario (a), la cual tiene carácter de confidencial.")
				.setFontSize(10)
				.setMarginTop(2);
		document.add(parrafoInfo2);
}
		// Sección F y G: DATOS DEL PROFESIONAL DE SALUD Y FIRMA DEL USUARIO
{
		 float[] anchoColsIJ = { 10f, 1f, 2.5f, 2.5f, 5f};
            Table tablaIJ = new Table(anchoColsIJ).setWidth(tablaAncho);
            tablaIJ.setBorder(bordeExterno);
            tablaIJ.setMarginBottom(8);
            tablaIJ.addCell(createCell("F. DATOS DEL PROFESIONAL", true, lila, 4, 1, TextAlignment.LEFT));
            tablaIJ.addCell(createCell("G. FIRMA DEL USUARIO",true, lila, 1, 2, TextAlignment.LEFT));
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
		String baseName = "certificado_salud_" + (empleado != null ? empleado.getCedula() : "");
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
			System.out.println("Certificado de salud guardado exitosamente en " + rutaDescargas);
		} catch (Exception ex) {
			System.out.println("Error al guardar el PDF de certificado de salud: " + ex.getMessage());
		}
		// Devolver el PDF en base64
		String base64Pdf = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
		return new DocumentoBase64Dto(base64Pdf);

	}
}
