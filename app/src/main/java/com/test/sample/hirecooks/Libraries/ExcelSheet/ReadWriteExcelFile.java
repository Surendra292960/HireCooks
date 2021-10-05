package com.test.sample.hirecooks.Libraries.ExcelSheet;
import android.os.Environment;
import com.test.sample.hirecooks.Models.FirmUsers.Firmuser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ReadWriteExcelFile {

	public static boolean generateFirmUserReport(List<Firmuser> firmuserList) {
		boolean success = false;
		try {
			File sd = Environment.getExternalStorageDirectory();
			String csvFile = "FirmUser.xls";
			File directory = new File(sd.getAbsolutePath()+"/HireCook");
			if (!directory.exists() && !directory.isDirectory()) {
				directory.mkdirs();
			}
			File file = new File(directory, csvFile );
			String sheetName = "Firm User List";
			WorkbookSettings wbSettings = new WorkbookSettings();
			wbSettings.setLocale(new Locale("en", "EN"));
			WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
			WritableSheet sheet = workbook.createSheet(sheetName, 0);

			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 20);
			sheet.setColumnView(8, 20);
			sheet.setColumnView(9, 20);
			sheet.setColumnView(10, 20);
			sheet.setColumnView(11, 20);
			sheet.setColumnView(12, 20);
			sheet.setColumnView(13, 20);
			sheet.setColumnView(14, 20);
			sheet.setColumnView(15, 20);

			WritableCellFormat cellFormats = new WritableCellFormat();
			cellFormats.setAlignment(Alignment.CENTRE);

			WritableCellFormat summaryCellYellow = new WritableCellFormat();
			summaryCellYellow.setAlignment(Alignment.CENTRE);
			summaryCellYellow.setBackground(Colour.YELLOW);
			summaryCellYellow.setBorder(Border.ALL,jxl.format.BorderLineStyle.THIN, Colour.BLACK);

			WritableFont titleFont = new WritableFont(WritableFont.TAHOMA, 12, WritableFont.BOLD);
			WritableCellFormat titleformat = new WritableCellFormat(titleFont);
			titleformat.setAlignment( Alignment.CENTRE );
			//sheet.setRowView(0, 38*20);
			int heightInPoints = 38 * 20;
			sheet.setRowView(1, heightInPoints );
			//sheet.addCell(new Label(0, 0, "Firm User List",titleformat));
			sheet.addCell(new Label(0, 1, "# S.No",titleformat));
			sheet.addCell(new Label(1, 1, "ID",titleformat));
			sheet.addCell(new Label(2, 1, "Name",titleformat));
			sheet.addCell(new Label(3, 1, "User Type",titleformat));
			sheet.addCell(new Label(4, 1, "Login Date",titleformat));
			sheet.addCell(new Label(5, 1, "Logout Date",titleformat));
			sheet.addCell(new Label(6, 1, "Login Lattitude",titleformat));
			sheet.addCell(new Label(7, 1, "Login Longintude",titleformat));
			sheet.addCell(new Label(8, 1, "Login Lattitude",titleformat));
			sheet.addCell(new Label(9, 1, "Logout Longintude",titleformat));
			sheet.addCell(new Label(10, 1, "Login Address",titleformat));
			sheet.addCell(new Label(11, 1, "Logout Address",titleformat));
			sheet.addCell(new Label(12, 1, "Firm Id",titleformat));
			sheet.addCell(new Label(13, 1, "Status",titleformat));
			sheet.addCell(new Label(14, 1, "User Id",titleformat));
			sheet.addCell(new Label(15, 1, "Created At",titleformat));

			for (int i=0;i<firmuserList.size(); i++) {
				sheet.setRowView(i+2, heightInPoints );
				sheet.addCell(new Label(0, i+2, String.valueOf( i ),cellFormats));
				sheet.addCell(new Label(1, i+2, String.valueOf( firmuserList.get( i ).getId() ),cellFormats));
				sheet.addCell(new Label(2, i+2, firmuserList.get( i ).getName(),cellFormats));
				sheet.addCell(new Label(3, i+2, firmuserList.get( i ).getUserType(),cellFormats));
				sheet.addCell(new Label(4, i+2, firmuserList.get( i ).getSigninDate(),cellFormats));
				sheet.addCell(new Label(5, i+2, firmuserList.get( i ).getSignoutDate(),cellFormats));
				sheet.addCell(new Label(6, i+2, firmuserList.get( i ).getSigninLat(),cellFormats));
				sheet.addCell(new Label(7, i+2, firmuserList.get( i ).getSigninLng(),cellFormats));
				sheet.addCell(new Label(8, i+2, firmuserList.get( i ).getSignoutLat(),cellFormats));
				sheet.addCell(new Label(9, i+2, firmuserList.get( i ).getSignoutLng(),cellFormats));
				sheet.addCell(new Label(10, i+2, firmuserList.get( i ).getSigninAddress(),cellFormats));
				sheet.addCell(new Label(11, i+2, firmuserList.get( i ).getSignoutAddress(),cellFormats));
				sheet.addCell(new Label(12, i+2, firmuserList.get( i ).getFirmId(),cellFormats));
				sheet.addCell(new Label(13, i+2, firmuserList.get( i ).getStatus(),cellFormats));
				sheet.addCell(new Label(14, i+2, String.valueOf( firmuserList.get( i ).getUserId() ),cellFormats));
				sheet.addCell(new Label(15, i+2, firmuserList.get( i ).getCreatedAt(),cellFormats));
			}

			workbook.write();
			success = true;
			workbook.close();
		} catch (WriteException | IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean generateRecivedOrdersReport(List<Firmuser> firmuserList) {
		boolean success = false;
		try {
			File sd = Environment.getExternalStorageDirectory();
			String csvFile = "FirmUser.xls";
			File directory = new File(sd.getAbsolutePath()+"/HireCook");
			if (!directory.exists() && !directory.isDirectory()) {
				directory.mkdirs();
			}
			File file = new File(directory, csvFile );
			String sheetName = "Firm User List";
			WorkbookSettings wbSettings = new WorkbookSettings();
			wbSettings.setLocale(new Locale("en", "EN"));
			WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
			WritableSheet sheet = workbook.createSheet(sheetName, 0);

			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 20);
			sheet.setColumnView(8, 20);
			sheet.setColumnView(9, 20);
			sheet.setColumnView(10, 20);
			sheet.setColumnView(11, 20);
			sheet.setColumnView(12, 20);
			sheet.setColumnView(13, 20);
			sheet.setColumnView(14, 20);

			WritableCellFormat cellFormats = new WritableCellFormat();
			cellFormats.setAlignment(Alignment.CENTRE);

			WritableCellFormat summaryCellYellow = new WritableCellFormat();
			summaryCellYellow.setAlignment(Alignment.CENTRE);
			summaryCellYellow.setBackground(Colour.YELLOW);
			summaryCellYellow.setBorder(Border.ALL,jxl.format.BorderLineStyle.THIN, Colour.BLACK);

			WritableFont titleFont = new WritableFont(WritableFont.TAHOMA, 12, WritableFont.BOLD);
			WritableCellFormat titleformat = new WritableCellFormat(titleFont);
			titleformat.setAlignment( Alignment.CENTRE );
			//sheet.setRowView(0, 38*20);
			int heightInPoints = 38 * 20;
			sheet.setRowView(1, heightInPoints );
			//sheet.addCell(new Label(0, 0, "Firm User List",titleformat));
			sheet.addCell(new Label(0, 1, "# S.No",titleformat));
			sheet.addCell(new Label(1, 1, "ID",titleformat));
			sheet.addCell(new Label(2, 1, "Name",titleformat));
			sheet.addCell(new Label(3, 1, "User Type",titleformat));
			sheet.addCell(new Label(4, 1, "Login Date",titleformat));
			sheet.addCell(new Label(5, 1, "Login Lattitude",titleformat));
			sheet.addCell(new Label(6, 1, "Login Longintude",titleformat));
			sheet.addCell(new Label(7, 1, "Login Address",titleformat));
			sheet.addCell(new Label(8, 1, "Logout Lattitude",titleformat));
			sheet.addCell(new Label(9, 1, "Logout Longintude",titleformat));
			sheet.addCell(new Label(10, 1, "Logout Address",titleformat));
			sheet.addCell(new Label(11, 1, "Firm Id",titleformat));
			sheet.addCell(new Label(12, 1, "Status",titleformat));
			sheet.addCell(new Label(13, 1, "User Id",titleformat));
			sheet.addCell(new Label(14, 1, "Created At",titleformat));

			for (int i=0;i<firmuserList.size(); i++) {
				sheet.setRowView(i+2, heightInPoints );
				sheet.addCell(new Label(0, i+2, String.valueOf( i ),cellFormats));
				sheet.addCell(new Label(1, i+2, String.valueOf( firmuserList.get( i ).getId() ),cellFormats));
				sheet.addCell(new Label(2, i+2, firmuserList.get( i ).getName(),cellFormats));
				sheet.addCell(new Label(3, i+2, firmuserList.get( i ).getUserType(),cellFormats));
				sheet.addCell(new Label(4, i+2, firmuserList.get( i ).getSigninDate(),cellFormats));
				sheet.addCell(new Label(5, i+2, firmuserList.get( i ).getSigninLat(),cellFormats));
				sheet.addCell(new Label(6, i+2, firmuserList.get( i ).getSigninLng(),cellFormats));
				sheet.addCell(new Label(7, i+2, firmuserList.get( i ).getSigninAddress(),cellFormats));
				sheet.addCell(new Label(8, i+2, firmuserList.get( i ).getSignoutLat(),cellFormats));
				sheet.addCell(new Label(9, i+2, firmuserList.get( i ).getSignoutLng(),cellFormats));
				sheet.addCell(new Label(10, i+2, firmuserList.get( i ).getSignoutAddress(),cellFormats));
				sheet.addCell(new Label(11, i+2, firmuserList.get( i ).getFirmId(),cellFormats));
				sheet.addCell(new Label(12, i+2, firmuserList.get( i ).getStatus(),cellFormats));
				sheet.addCell(new Label(13, i+2, String.valueOf( firmuserList.get( i ).getUserId() ),cellFormats));
				sheet.addCell(new Label(14, i+2, firmuserList.get( i ).getCreatedAt(),cellFormats));
			}

			workbook.write();
			success = true;
			workbook.close();
		} catch (WriteException | IOException e) {
			e.printStackTrace();
		}
		return success;
	}
}