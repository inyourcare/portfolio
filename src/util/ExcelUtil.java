package util;

import com.nomad.zaksim.msg.AppException;
import com.nomad.zaksim.msg.ExceptionCode;
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelUtil {

    /**
     * 아무 리스트든지 엑셀 파일로 생성 가능
     *
     * @param objs
     * @param <T>
     */
    public static <T> void listToFile(List<T> objs, FileOutputStream fileoutputstream) throws Exception {

        // 2019-08-02 엑셀 파일 경로 설정 필요
//        String xlsxPath = "";
        try {
            List<String> fieldNames = getFieldNameList(objs.get(0));
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("sheet1");
            XSSFRow row = null;
            XSSFCell cell = null;
            int rowNum = 0;
            int cellNum = 0;
            row = sheet.createRow(rowNum++);
            // row 0 -> 컬럼 명
            for (String fieldName : fieldNames) {
                cell = row.createCell(cellNum++);
                cell.setCellValue(String.valueOf(fieldName));
            }
            // row 1 ~ -> 바디
            for (T obj : objs) {
                cellNum = 0;
                row = sheet.createRow(rowNum++);
                for (String fieldName : fieldNames) {
                    cell = row.createCell(cellNum++);
                    cell.setCellValue(String.valueOf(getFieldValueAsString(obj, fieldName)));
                }
            }

            try {
//                FileOutputStream fileoutputstream = new FileOutputStream(xlsxPath);
                workbook.write(fileoutputstream);
//                fileoutputstream.close();
                System.out.println("엑셀파일생성성공");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
    }

    public static List<String> getFieldNameList(Object beanObj) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = beanObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public static String getFieldValueAsString(Object beanObj, String fieldName) throws Exception {
        // Property Descriptor
        PropertyDescriptor pd = new PropertyDescriptor(fieldName, beanObj.getClass());
        Method getterMtd = pd.getReadMethod();

        Object value = getterMtd.invoke(beanObj);
        String cellValue = value != null ? String.valueOf(value) : "";

        return cellValue;
    }

    /**
     * 컬럼 순서만 바로 되어 있다면 바로 객체화 할 수 있다.
     *
     * @param multipartFile
     * @param rowFunc
     * @param <T>
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
//    public static <T> List<T> readFileToSendCouponList(final MultipartFile multipartFile,
//                                                       final BiFunction<Integer, Row, T> rowFunc, int id) throws IOException, InvalidFormatException {
//
//        final Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
//        final Sheet sheet = workbook.getSheetAt(0);
//        final int rowCount = sheet.getPhysicalNumberOfRows();
//
//        return IntStream
//                .range(0, rowCount)
//                .mapToObj(rowIndex -> rowFunc.apply(id, sheet.getRow(rowIndex)))
//                .collect(Collectors.toList());
//    }
//
//    public static <T> List<T> readFileToFirstInCouponList(final MultipartFile multipartFile,
//                                                       final BiFunction<Integer, Row, T> rowFunc, int id) throws IOException, InvalidFormatException {
//
//        final Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
//        final Sheet sheet = workbook.getSheetAt(0);
//        final int rowCount = sheet.getPhysicalNumberOfRows();
//
//        return IntStream
//                .range(0, rowCount)
//                .mapToObj(rowIndex -> rowFunc.apply(id, sheet.getRow(rowIndex)))
//                .collect(Collectors.toList());
//    }
    public static <T> List<T> readFileToEntityList(final MultipartFile multipartFile,
                                                   final BiFunction<Integer, Row, T> rowFunc, int id) throws IOException, InvalidFormatException {

        final Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
        final Sheet sheet = workbook.getSheetAt(0);
        final int rowCount = sheet.getPhysicalNumberOfRows();

        return IntStream
                .range(0, rowCount)
                .mapToObj(rowIndex -> rowFunc.apply(id, sheet.getRow(rowIndex)))
                .collect(Collectors.toList());

    }

    public static <T> List<T> readFileToEntityList(final MultipartFile multipartFile,
                                                   final Function<Row, T> rowFunc) throws IOException, InvalidFormatException {

        final Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
        final Sheet sheet = workbook.getSheetAt(0);
        final int rowCount = sheet.getPhysicalNumberOfRows();

        return IntStream
                .range(0, rowCount)
                .mapToObj(rowIndex -> rowFunc.apply(sheet.getRow(rowIndex)))
                .collect(Collectors.toList());

    }


    /**
     * 첫번째 행이 컬럼명일때 쓸 수 있다.
     *
     * @param multipartFile
     * @return
     * @throws InvalidFormatException
     */
    public static List<Map<String, Object>> readElsx(MultipartFile multipartFile) throws InvalidFormatException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> rowMap = null;
        try {
            verifyFileExtension(multipartFile);
//            XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
//            Workbook workbook = multipartFileToWorkbook(multipartFile);

            int rowindex = 0;
            //시트 수 (첫번째에만 존재하므로 0을 준다)
            Sheet sheet = workbook.getSheetAt(0);

            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();
            //첫째행을 컬럼명으로 생각하고 읽는다.
            Row row = sheet.getRow(0);
            List<String> keys = null;
            if (row != null) {
                keys = getCollumnValueFromRow(row);
            }
            for (rowindex = 1; rowindex < rows; rowindex++) {
                //행을읽는다
                row = sheet.getRow(rowindex);
                List<String> value = getCollumnValueFromRow(row);
                rowMap = new HashMap<>();
                int i = 0;
                for (i = 0; i < keys.size(); i++) {
                    rowMap.put(keys.get(i), value.get(i));
                }
                result.add(rowMap);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static List<String> getCollumnValueFromRow(Row row) {
        List<String> result = new ArrayList<>();
        if (row != null) {
            //셀의 수
            int cells = row.getPhysicalNumberOfCells();
            int columnindex = 0;
            for (columnindex = 0; columnindex <= cells; columnindex++) {
                //셀값을 읽는다
                Cell cell = row.getCell(columnindex);
                String value = "";
                //셀이 빈값일경우를 위한 널체크
                if (cell == null) {
                    continue;
                } else {
                    //타입별로 내용 읽기
                    CellType cellType = cell.getCellTypeEnum();
                    if (cellType == CellType.FORMULA) {
                        value = cell.getCellFormula();
                    } else if (cellType == CellType.NUMERIC) {
                        value = cell.getNumericCellValue() + "";
                    } else if (cellType == CellType.STRING) {
                        value = cell.getStringCellValue() + "";
                    } else if (cellType == CellType.BLANK) {
                        value = cell.getBooleanCellValue() + "";
                    } else if (cellType == CellType.ERROR) {
                        value = cell.getErrorCellValue() + "";
                    }
                }
                result.add(value);
            }
        }
        return result;
    }

    @Getter
    public enum ExcelConfig {
        XLS("xls"),
        XLSX("xlsx");
        private String code;

        ExcelConfig(String code) {
            this.code = code;
        }
    }

    private static Workbook multipartFileToWorkbook(MultipartFile multipartFile)
            throws IOException {
        if (isExcelXls(multipartFile.getOriginalFilename())) {
            return new HSSFWorkbook(multipartFile.getInputStream());
        } else {
            return new XSSFWorkbook(multipartFile.getInputStream());
        }
    }

    private static void verifyFileExtension(MultipartFile multipartFile) throws InvalidFormatException {
        if (!isExcelExtension(multipartFile.getOriginalFilename())) {
            throw new InvalidFormatException(ExceptionCode.NOT_EXCEL_EXTENSION.getMessage());
        }
    }

    private static boolean isExcelExtension(String fileName) {
        return fileName.endsWith(ExcelConfig.XLS.getCode()) || fileName.endsWith(ExcelConfig.XLSX.getCode());
    }

    private static boolean isExcelXls(String fileName) {
        return fileName.endsWith(ExcelConfig.XLS.getCode());
    }

    public static boolean isExcelXlsx(String fileName) {
        return fileName.endsWith(ExcelConfig.XLSX.getCode());
    }
}
