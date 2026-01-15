package saitel.medicina.reportes.ReportesExcel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import saitel.medicina.entity.CitaMedica;
import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.DepartamentoTipo;

import saitel.medicina.repository.CitaMedicaRepository;
import saitel.medicina.service.DatosEmpleadoService;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saitel.medicina.dto.*;

@Service
@RequiredArgsConstructor
public class ReporteCitas {
    private final CitaMedicaRepository citaMedicaRepository;
    private final DatosEmpleadoService datosEmpleadoService;

     public DocumentoBase64Dto generarReporteCitas(LocalDate fechaInicio, LocalDate fechaFin, String departamento) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }
        

        DepartamentoTipo deptoEnum = DepartamentoTipo.fromCode(departamento);
        List<CitaMedica> citas = citaMedicaRepository.findByFechaCitaBetween(fechaInicio, fechaFin, deptoEnum.getNombreDB());

        System.out.println("citas: " + citas);

        Map<Integer, DatosEmpleados> empleadosMap = new HashMap<>();
        for (CitaMedica cita : citas) {
            if (!empleadosMap.containsKey(cita.getIdEmpleado())) {
                datosEmpleadoService.obtenerPorId(cita.getIdEmpleado())
                        .ifPresent(empleado -> empleadosMap.put(empleado.getIdEmpleado(), empleado));
            }
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte de Citas Agendadas");

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            Row titleRow = sheet.createRow(3);
            Cell titleCell = titleRow.createCell(0);
            String titulo = "Reporte de Citas Médicas del " +
                    fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " al " +
                    fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            titleCell.setCellValue(titulo);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));
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
            String[] headers = {"Sucursal", "Paciente/Empleado", "Profesional",
                    "Motivo de Consulta", "Fecha de Consulta", "Hora de Consulta",
                    "Estado"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setWrapText(true);
            dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);

            int rowNum = 6;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (CitaMedica cita : citas) {
                DatosEmpleados empleado = empleadosMap.get(cita.getIdEmpleado());
                if (empleado != null) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(empleado.getSucursal() != null ? empleado.getSucursal() : "");
                    String nombreCompleto = (empleado.getNombre() != null ? empleado.getNombre() + " " : "") +
                            (empleado.getApellido() != null ? empleado.getApellido() : "");
                    row.createCell(1).setCellValue(nombreCompleto.trim());
                    row.createCell(2).setCellValue(cita.getTipoProfesional() != null ? cita.getTipoProfesional() : "");
                    row.createCell(3).setCellValue(cita.getMotivoConsulta() != null ? cita.getMotivoConsulta() : "");
                    row.createCell(4).setCellValue(cita.getFechaCita() != null ? cita.getFechaCita().format(dateFormatter) : "");
                    row.createCell(5).setCellValue(cita.getHoraCita() != null ? cita.getHoraCita().format(timeFormatter) : "");
                    row.createCell(6).setCellValue(cita.getEstado() != null ? cita.getEstado() : "");

                    for (int i = 0; i < headers.length; i++) {
                        if (row.getCell(i) != null) {
                            row.getCell(i).setCellStyle(dataStyle);
                        }
                    }
                }
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
            }


            for (int i = 0; i <= rowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    row.setHeight((short) -1);
                }
            }

            workbook.write(outputStream);
            byte[] bytesExcel = outputStream.toByteArray();

           String base64 = Base64.getEncoder().encodeToString(bytesExcel).replaceAll("\\s+", ""); 
           return new DocumentoBase64Dto(base64);

        } catch (IOException e) {
            throw new RuntimeException("Error al generar o guardar el archivo Excel: " + e.getMessage(), e);
        }
    }
}

