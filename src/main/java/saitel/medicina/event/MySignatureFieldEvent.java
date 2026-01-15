package saitel.medicina.event;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

public class MySignatureFieldEvent extends CellRenderer {
    private final String fieldName;
    private final PdfDocument pdfDoc;

    public MySignatureFieldEvent(Cell modelElement, String fieldName, PdfDocument pdfDoc) {
        super(modelElement);
        this.fieldName = fieldName;
        this.pdfDoc = pdfDoc;
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);
        Rectangle rect = getOccupiedAreaBBox();
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        // Crear anotación visual (widget)
    PdfWidgetAnnotation widget = new PdfWidgetAnnotation(rect);
        PdfPage page = pdfDoc.getPage(getOccupiedArea().getPageNumber());
        widget.setPage(page);
        widget.setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT);

        // Crear el campo de firma y asociar el widget
        PdfSignatureFormField signatureField = PdfSignatureFormField.createSignature(pdfDoc);
        signatureField.setFieldName(fieldName);
        signatureField.addKid(widget);

        // Añadir el campo al formulario
        form.addField(signatureField);
    }
}