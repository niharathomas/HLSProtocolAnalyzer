package HLSProtocolAnalyzer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ExcelResultWriter {
	public int recordNumber = 0;
	public int errorNumber = 0;
	HSSFWorkbook workbook;
	HSSFSheet worksheet;

	public void createExcelFile() {

		try {
			FileOutputStream fileOut = new FileOutputStream("Results.xls");
			workbook = new HSSFWorkbook();
			worksheet = workbook.createSheet("Results");

			// index from 0,0... cell A1 is cell(0,0)
			HSSFRow row1 = worksheet.createRow(0);
			HSSFCell cellA1 = row1.createCell(0);
			cellA1.setCellValue("Error #");

			HSSFCell cellB1 = row1.createCell(1);
			cellB1.setCellValue("Error Type");

			HSSFCell cellC1 = row1.createCell(2);
			cellC1.setCellValue("File Name");

			HSSFCell cellD1 = row1.createCell(3);
			cellD1.setCellValue("Error Details");

			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeNewRecord(String errorType, String filename, String errorDetails) {
		System.out.println("Writing record");
		System.out.println(errorType);
		System.out.println(filename);
		System.out.println(errorDetails);
		recordNumber++;
		errorNumber++;
		try {
			FileOutputStream fileOut = new FileOutputStream("Results.xls");
			HSSFRow row = worksheet.createRow(recordNumber);
			HSSFCell cellA = row.createCell(0);
			cellA.setCellValue(errorNumber);

			HSSFCell cellB = row.createCell(1);
			cellB.setCellValue(errorType);

			HSSFCell cellC = row.createCell(2);
			cellC.setCellValue(filename);

			HSSFCell cellD = row.createCell(3);
			cellD.setCellValue(errorDetails);
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
