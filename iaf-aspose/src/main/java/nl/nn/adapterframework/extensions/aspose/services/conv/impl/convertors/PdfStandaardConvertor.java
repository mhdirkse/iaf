package nl.nn.adapterframework.extensions.aspose.services.conv.impl.convertors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.tika.mime.MediaType;

import com.aspose.pdf.exceptions.InvalidPasswordException;

import nl.nn.adapterframework.extensions.aspose.ConversionOption;
import nl.nn.adapterframework.extensions.aspose.services.conv.CisConversionResult;
import nl.nn.adapterframework.util.Misc;

/**
 * Convertor for a pdf file (no conversion required).
 * 
 * @author <a href="mailto:gerard_van_der_hoorn@deltalloyd.nl">Gerard van der
 *         Hoorn</a> (d937275)
 */
public class PdfStandaardConvertor extends AbstractConvertor {

	PdfStandaardConvertor(String pdfOutputLocation) {
		// Give the supported media types.
		super(pdfOutputLocation, new MediaType("application", "pdf"));
	}

	// TODO GH als seperate pdf pdf uitpakken !!!
	@Override
	void convert(MediaType mediaType, File file, CisConversionResult result, ConversionOption conversionOption)
			throws Exception {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			byte[] array = Misc.streamToBytes(inputStream);
			InputStream inStream = new ByteArrayInputStream(array);
			result.setFileStream(inStream);
		}
		// result.setMetaData(new MetaData(getNumberOfPages(inputStream)));

		// TODO GH indien single pdf dan nog wel de cisConversieResult vullen inclusief
		// de eventuele attachments in pdf. In dat geval ook
		// pdfDocument.setPageMode(PageMode.UseAttachments) zetten.

		// Indien seperatepdf de attachments uitpakken (hergebruik de funct in de
		// mailConvertor, verplaats gezamelijke code naar AbstractConvertor).
	}

	@Override
	protected boolean isPasswordException(Exception e) {
		return e instanceof InvalidPasswordException;
	}

}