package saitel.medicina.reportes.ReportesExcel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import saitel.medicina.dto.DocumentoBase64Dto;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.Evaluacion;
import saitel.medicina.repository.EvaluacionRepository;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.service.MotivoConsultaService;
import saitel.medicina.entity.MotivoConsulta;

@Service
@RequiredArgsConstructor
public class ReporteEvaluaciones {
    private final EvaluacionRepository evaluacionRepository ;
    private final DatosEmpleadoService datosEmpleadoService;
    private final MotivoConsultaService motivoConsultaService;

    public DocumentoBase64Dto generarReporteEvaluaciones(LocalDate fechaInicio, LocalDate fechaFin) {
    List<Evaluacion> evaluaciones = evaluacionRepository.findByFechaAtencionBetween(fechaInicio, fechaFin);

    try (Workbook workbook = new XSSFWorkbook();
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        Sheet sheet = workbook.createSheet("Reporte de Evaluaciones");

        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Row titleRow = sheet.createRow(3);
        Cell titleCell = titleRow.createCell(0);
        String titulo = "Reporte de Evaluaciones del " +
                fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " al " +
                fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        titleCell.setCellValue(titulo);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

        // Encabezados
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);

        Row headerRow = sheet.createRow(5);
        String[] headers = {"Sucursal",
            "Paciente/Empleado", "Tipo de Formulario",
            "Motivo de Consulta", "Fecha de Atención"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Estilo de datos
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setWrapText(true);
        dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);

        int rowNum = 6;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Evaluacion eval : evaluaciones) {
            Row row = sheet.createRow(rowNum++);
            DatosEmpleados paciente = null;
            if (eval.getIdEmpleado() != null) {
                paciente = datosEmpleadoService.obtenerPorId(eval.getIdEmpleado()).orElse(null);
            }
            String sucursal = (paciente != null && paciente.getSucursal() != null ? paciente.getSucursal() : "");
            String nombrePaciente = (paciente != null ? paciente.getNombre() + " " + paciente.getApellido() : "");
            String nombreTipoEvaluacion = (eval.getTipoEvaluacion() != null ? eval.getTipoEvaluacion().getNombreEvaluacion() : "");
            // Obtener motivo de consulta
            String motivoConsulta = "";
            List<MotivoConsulta> motivos = motivoConsultaService.findByIdEvaluacion(eval.getId());
            if (motivos != null && !motivos.isEmpty()) {
                motivoConsulta = motivos.get(0).getMotivo();
            }
            String fechaAtencion = eval.getFecha() != null ? eval.getFecha().format(dateFormatter) : "";

            row.createCell(0).setCellValue(sucursal);
            row.createCell(1).setCellValue(nombrePaciente.trim());
            row.createCell(2).setCellValue(nombreTipoEvaluacion);
            row.createCell(3).setCellValue(motivoConsulta);
            row.createCell(4).setCellValue(fechaAtencion);

            for (int i = 0; i < headers.length; i++) {
                if (row.getCell(i) != null) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
        }

        workbook.write(outputStream);
        byte[] bytesExcel = outputStream.toByteArray();

        // Devolver solo el Base64 del archivo generado
        String base64 = Base64.getEncoder().encodeToString(bytesExcel).replaceAll("\\s+", "");
        return new DocumentoBase64Dto(base64);

    } catch (IOException e) {
        throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage(), e);
    }
}
}
