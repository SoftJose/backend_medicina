package saitel.medicina.rest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import saitel.medicina.entity.DatosEmpleados;
import saitel.medicina.entity.DatosProfesional;
import saitel.medicina.entity.Evaluacion;
import saitel.medicina.filter.HeaderFilter;
import saitel.medicina.reportes.HCU077EvaluacionPreocupacionalReporte;
import saitel.medicina.reportes.HCU078EvaluacionPeriodicaReporte;
import saitel.medicina.reportes.HCU079EvaluacionReintegroReporte;
import saitel.medicina.reportes.HCU080EvaluacionRetiroReporte;
import saitel.medicina.reportes.HCU081CertificadoSaludReporte;
import saitel.medicina.service.EvaluacionService;
import saitel.medicina.service.RecetaService;
import saitel.medicina.service.ImpLog.FirmaDocumentoService;
import saitel.medicina.service.DatosEmpleadoService;
import saitel.medicina.service.DatosProfesionalService;
import saitel.medicina.entity.MotivoConsulta;
import saitel.medicina.dto.*;
import saitel.medicina.reportes.ReportesExcel.ReporteEvaluaciones;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import saitel.medicina.repository.EvaluacionRepository;
import saitel.medicina.repository.ExamenesRepository;
import saitel.medicina.service.MotivoConsultaService;
import saitel.medicina.service.ImpLog.DocumentoService;
import saitel.medicina.service.ImpLog.DocumentoConsultaService;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EvaluacionRest {

    private final EvaluacionService evaluacionService;
    private final HCU081CertificadoSaludReporte certificadoSaludReporte;
    private final HCU078EvaluacionPeriodicaReporte periodicaReporte;
    private final HCU077EvaluacionPreocupacionalReporte preocupacionalReporte;
    private final HCU080EvaluacionRetiroReporte retiroReporte;
    private final HCU079EvaluacionReintegroReporte reintegroReporte;
    private final FirmaDocumentoService firmaDocumentoService;
    private final DatosProfesionalService profesionalService;
    private final ReporteEvaluaciones reporteEvaluaciones;
    private final EvaluacionRepository evaluacionRepository;
    private final DatosEmpleadoService datosEmpleadoService;
    private final ExamenesRepository examenesRepository;
    private final RecetaService recetaService;
    private final MotivoConsultaService motivoConsultaService;
    private final DocumentoService documentoService;
    private final DocumentoConsultaService documentoConsultaService;
    
    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String departamento,
            @RequestParam String param,
            @RequestBody Evaluacion evaluacion) {

        try {
            Map<String, String> headers = HeaderFilter.getHeaders();
            String usuario = headers.get("usuario");
            evaluacion.setAliasUsuario(usuario);
            System.out.println("Usuario: " + usuario);
            Evaluacion nueva = evaluacionService.save(sucursal, departamento, param, evaluacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);

        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al guardar la evaluación."));
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Evaluacion>> listarTodos() {
        List<Evaluacion> lista = evaluacionService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evaluacion> obtenerPorId(@PathVariable Integer id) {
        return evaluacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @Validated @RequestBody Evaluacion evaluacion) {

        try {
            Map<String, String> headers = HeaderFilter.getHeaders();
            String usuario = headers.get("usuario");
            evaluacion.setAliasUsuario(usuario);
            Evaluacion actualizada = evaluacionService.update(id, evaluacion);
            return ResponseEntity.ok(actualizada);

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al actualizar la evaluación"));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Integer id) {
        return evaluacionService.findById(id)
                .map(existing -> {
                    evaluacionService.deleteById(existing.getId());
                    return ResponseEntity.ok(true);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

    @PutMapping("/firmar-empleado/{id}")
    public ResponseEntity<Evaluacion> firmarEmpleado(@PathVariable Integer id) {
        Evaluacion evaluacionFirmada = evaluacionService.firmarEmpleado(id);
        return ResponseEntity.ok(evaluacionFirmada);
    }

    @PostMapping("/pdf/{idEvaluacion}/firmar-profesional/{claveProfesional}")
    public ResponseEntity<DocumentoBase64Dto> generarFirmarProfesionalYGuardar(
            @PathVariable Integer idEvaluacion,
            @PathVariable String claveProfesional
    ) {
        try {
            Evaluacion eval = evaluacionService.findById(idEvaluacion)
                    .orElseThrow(() -> new EntityNotFoundException("Evaluación no encontrada"));

            String tipo = eval.getTipoEvaluacion() != null ? eval.getTipoEvaluacion().getNombreEvaluacion().toLowerCase() : "";
            DocumentoBase64Dto pdfGenerado;
            if (tipo.contains("certificado")) pdfGenerado = certificadoSaludReporte.generarPdf(idEvaluacion);
            else if (tipo.contains("reintegro")) pdfGenerado = reintegroReporte.generarpdf(idEvaluacion);
            else if (tipo.contains("retiro")) pdfGenerado = retiroReporte.generarpdf(idEvaluacion);
            else if (tipo.contains("preocupacional")) pdfGenerado = preocupacionalReporte.generarpdf(idEvaluacion);
            else if (tipo.contains("periodica")) pdfGenerado = periodicaReporte.generarpdf(idEvaluacion);
            else return ResponseEntity.badRequest().body(null);

            String base64 = pdfGenerado.getBase64();
            if (base64 == null || base64.isBlank()) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

            // Firma profesional
            List<DatosProfesional> profesionales = profesionalService.findByIdEvaluacion(idEvaluacion);
            DatosProfesional profesional = profesionales.isEmpty() ? null : profesionales.get(profesionales.size() - 1);
            if (profesional == null || !Boolean.TRUE.equals(profesional.getFirmaSello())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            if (claveProfesional == null || claveProfesional.isBlank()) return ResponseEntity.badRequest().body(null);

            DocumentoBase64Dto pdfFirmadoProfesional = firmaDocumentoService.firmarDocumento(
                    Base64.getDecoder().decode(base64),
                    claveProfesional,
                    profesional.getId(),
                    "firma_profesional"
            );

            // Guardar documento (un solo registro por evaluación + campoTabla)
            documentoService.guardarDocumento(
                    "tbl_evaluacion",
                    eval.getId().longValue(),
                    "pdf_evaluacion",
                    pdfFirmadoProfesional.getBase64(),
                    "MEDICINA",
                    "EVALUACIONES",
                    "pdf",
                    "evaluacion_" + idEvaluacion + "_firmaprof_"
            );

            return ResponseEntity.ok(pdfFirmadoProfesional);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
     @PostMapping("/pdf/{idEvaluacion}/firmar-empleado/{claveEmpleado}")
    public ResponseEntity<DocumentoBase64Dto> firmarEmpleadoYActualizar(
            @PathVariable Integer idEvaluacion,
            @PathVariable String claveEmpleado
    ) {
        try {
            Evaluacion eval = evaluacionService.findById(idEvaluacion)
                    .orElseThrow(() -> new EntityNotFoundException("Evaluación no encontrada"));
            if (claveEmpleado == null || claveEmpleado.isBlank()) return ResponseEntity.badRequest().body(null);

            String base = "MEDICINA";
            String catalogo = "EVALUACIONES";
            String tabla = "tbl_evaluacion";
            String campoTabla = "pdf_evaluacion";

            byte[] pdfBytes = documentoConsultaService
                    .consultar(base, catalogo, tabla, eval.getId().longValue(), campoTabla)
                    .map(DocumentoApiDto::getDocumento)
                    .orElse(null);

            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            DocumentoBase64Dto pdfFirmadoEmpleado = firmaDocumentoService.firmarDocumento(
                    pdfBytes,
                    claveEmpleado,
                    eval.getIdEmpleado(),
                    "firma_usuario"
            );

            documentoService.guardarDocumento(
                    tabla,
                    eval.getId().longValue(),
                    campoTabla,
                    pdfFirmadoEmpleado.getBase64(),
                    base,
                    catalogo,
                    "pdf",
                    "evaluacion_" + idEvaluacion + "_dual_"
            );

            // Marcar evaluación completa (dos firmas)
            eval.setEvaluacionCompleta(true);
            evaluacionRepository.save(eval);
            evaluacionService.firmarEmpleado(idEvaluacion);

            return ResponseEntity.ok(pdfFirmadoEmpleado);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
@GetMapping("/reporte/excel")
    public DocumentoBase64Dto generarReporteEvaluaciones(
        @RequestParam LocalDate fechaInicio,
        @RequestParam LocalDate fechaFin
    ) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        return reporteEvaluaciones.generarReporteEvaluaciones(fechaInicio, fechaFin);
    }

    @GetMapping("/filtrar")
    public List<Evaluacion> filtrarEvaluaciones(
        @RequestParam(required = false) String sucursal,
        @RequestParam(required = false) String paciente,
        @RequestParam(required = false) String tipoFormulario,
        @RequestParam(required = false) String motivoConsulta,
        @RequestParam(required = false) LocalDate fechaInicio,
        @RequestParam(required = false) LocalDate fechaFin
    ) {
        // Si no se envían fechas, usa semana actual
        if (fechaInicio == null || fechaFin == null) {
            LocalDate hoy = LocalDate.now();
            fechaInicio = hoy.with(java.time.DayOfWeek.MONDAY);
            fechaFin = hoy.with(java.time.DayOfWeek.SUNDAY);
        }
        return evaluacionRepository.filtrarEvaluaciones(
            sucursal,
            paciente,
            tipoFormulario,
            motivoConsulta,
            fechaInicio,
            fechaFin
        );
    }
    
    @GetMapping("/detalle/filtrar/{idEvaluacion}")
    public ResponseEntity<?> obtenerDetalleEvaluacion(@PathVariable("idEvaluacion") Integer idEvaluacion) {
        if (idEvaluacion == null) {
        return ResponseEntity.badRequest().body("El idEvaluacion no puede ser nulo");
        }
        return evaluacionRepository.findById(idEvaluacion)
                .map(evaluacion -> {
                    DatosEmpleados paciente = null;
                    if (evaluacion.getIdEmpleado() != null) {
                        paciente = datosEmpleadoService.obtenerPorId(evaluacion.getIdEmpleado()).orElse(null);
                    }
                    List<DatosProfesional> profesionales = profesionalService.findByIdEvaluacion(evaluacion.getId());
                    DatosProfesional profesional = profesionales.isEmpty() ? null : profesionales.get(profesionales.size() - 1);
                    List<?> examenes = examenesRepository.findByIdEvaluacion_Id(evaluacion.getId());
                    List<?> recetas = recetaService.findByIdEvaluacion(evaluacion.getId());

                    List<MotivoConsulta> motivos = motivoConsultaService.findByIdEvaluacion(evaluacion.getId());
                    String motivoConsulta = "";
                    if (motivos != null && !motivos.isEmpty()) {
                        motivoConsulta = motivos.get(0).getMotivo();
                    }
                    String tipoEvaluacion = evaluacion.getTipoEvaluacion() != null ? evaluacion.getTipoEvaluacion().getNombreEvaluacion() : "";
                    String fechaEvaluacion = "";
                    if (evaluacion.getFecha() != null) {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        fechaEvaluacion = evaluacion.getFecha().format(fmt);
                    }

                    String pacienteNombreCompleto = null;
                    if (paciente != null) {
                        String nombre = paciente.getNombre() != null ? paciente.getNombre().trim() : "";
                        String apellido = paciente.getApellido() != null ? paciente.getApellido().trim() : "";
                        pacienteNombreCompleto = (nombre + " " + apellido).trim();
                    }

                    Map<String, Object> detalle = Map.of(
                        "idEvaluacion", evaluacion.getId(),
                        "paciente", pacienteNombreCompleto,
                        "profesional", profesional,
                        "tipoEvaluacion", tipoEvaluacion,
                        "motivoConsulta", motivoConsulta,
                        "motivos", motivos,
                        "examenes", examenes,
                        "recetas", recetas,
                        "fechaEvaluacion", fechaEvaluacion
                    );
                    return ResponseEntity.ok(detalle);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}