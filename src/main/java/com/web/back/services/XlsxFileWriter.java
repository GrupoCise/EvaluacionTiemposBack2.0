package com.web.back.services;

import com.web.back.model.xls.XlsxField;
import com.web.back.model.xls.interfaces.XlsxCompositeField;
import com.web.back.model.xls.interfaces.XlsxSheet;
import com.web.back.model.xls.interfaces.XlsxSingleField;
import com.web.back.model.xls.interfaces.XlsxWriter;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class XlsxFileWriter implements XlsxWriter {

    private static final Logger logger = LoggerFactory.getLogger(XlsxFileWriter.class);

    @Override
    public <T> void write(List<T> data, ByteArrayOutputStream bos, String[] columnTitles, Workbook workbook) {
        if (data.isEmpty()) {
            logger.error("No data received to write Xls file..");
            return;
        }

        long start = System.currentTimeMillis();

        try {
            XlsxSheet annotation = data.get(0).getClass().getAnnotation(XlsxSheet.class);
            String sheetName = annotation.value();
            Sheet sheet = createSheetWithTitles(sheetName, columnTitles, workbook);
            writeDataToSheet(data, sheet, workbook);
            autoSizeColumns(sheet, columnTitles.length);
            workbook.write(bos);
            logger.info("Xls file generated in [{}] seconds", processTime(start));
        } catch (Exception e) {
            logger.info("Xls file write failed", e);
        }
    }

    @Override
    public <T> void appendSheet(List<T> data, ByteArrayOutputStream bos, String[] columnTitles, Workbook workbook, String sheetName) {
        if (data.isEmpty()) {
            return;
        }
        try {
            Sheet sheet = createSheetWithTitles(sheetName, columnTitles, workbook);
            writeDataToSheet(data, sheet, workbook);
            autoSizeColumns(sheet, columnTitles.length);
        } catch (Exception e) {
            logger.info("Xls file write failed", e);
        }
    }

    private <T> Sheet createSheetWithTitles(String sheetName, String[] columnTitles, Workbook workbook) {
        Font boldFont = getBoldFont(workbook);
        CellStyle headerStyle = getLeftAlignedCellStyle(workbook, boldFont);

        Sheet sheet = workbook.createSheet(sheetName);

        Row mainRow = sheet.createRow(0);
        for (int i = 0; i < columnTitles.length; i++) {
            Cell columnTitleCell = mainRow.createCell(i);
            columnTitleCell.setCellStyle(headerStyle);
            columnTitleCell.setCellValue(columnTitles[i]);
        }
        return sheet;
    }

    private <T> void writeDataToSheet(List<T> data, Sheet sheet, Workbook workbook) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<XlsxField> xlsColumnFields = getFieldNamesForClass(data.get(0).getClass());
        int tempRowNo = 1;

        for (T record : data) {
            tempRowNo = writeRecordToSheet(record, xlsColumnFields, sheet, tempRowNo, workbook);
        }
    }

    private <T> int writeRecordToSheet(T record, List<XlsxField> xlsColumnFields, Sheet sheet, int tempRowNo, Workbook workbook) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int recordBeginRowNo = tempRowNo;
        Row mainRow = sheet.createRow(tempRowNo++);
        boolean isRowNoToDecrease = false;

        for (XlsxField xlsColumnField : xlsColumnFields) {
            if (!xlsColumnField.isAnArray() && !xlsColumnField.isComposite()) {
                writeSingleFieldRow(mainRow, xlsColumnField, record, workbook);
                if (isNextColumnAnArray(xlsColumnFields, xlsColumnField, record)) {
                    isRowNoToDecrease = true;
                    tempRowNo = recordBeginRowNo + 1;
                }
            } else if (xlsColumnField.isAnArray() && !xlsColumnField.isComposite()) {
                tempRowNo = writeArrayFieldRows(record, xlsColumnField, sheet, tempRowNo, workbook, mainRow, isRowNoToDecrease);
                isRowNoToDecrease = false;
            } else if (xlsColumnField.isAnArray()) {
                tempRowNo = writeCompositeFieldRows(record, xlsColumnField, sheet, tempRowNo, workbook, mainRow, isRowNoToDecrease);
                isRowNoToDecrease = false;
            }
        }
        return tempRowNo;
    }

    private <T> int writeArrayFieldRows(T record, XlsxField xlsColumnField, Sheet sheet, int tempRowNo, Workbook workbook, Row mainRow, boolean isRowNoToDecrease) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method xlsMethod = getMethod(record.getClass(), xlsColumnField);
        List<Object> objValueList = (List<Object>) xlsMethod.invoke(record);
        boolean isFirstValue = true;

        for (Object objectValue : objValueList) {
            Row childRow = isFirstValue ? mainRow : getOrCreateNextRow(sheet, tempRowNo++);
            writeArrayFieldRow(childRow, xlsColumnField, objectValue, workbook);
            isFirstValue = false;
        }
        return tempRowNo;
    }

    private <T> int writeCompositeFieldRows(T record, XlsxField xlsColumnField, Sheet sheet, int tempRowNo, Workbook workbook, Row mainRow, boolean isRowNoToDecrease) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method xlsMethod = getMethod(record.getClass(), xlsColumnField);
        List<Object> objValueList = (List<Object>) xlsMethod.invoke(record);
        boolean isFirstRow = true;

        for (Object objectValue : objValueList) {
            Row childRow = isFirstRow ? mainRow : getOrCreateNextRow(sheet, tempRowNo++);
            List<XlsxField> xlsCompositeColumnFields = getFieldNamesForClass(objectValue.getClass());
            for (XlsxField xlsCompositeColumnField : xlsCompositeColumnFields) {
                writeCompositeFieldRow(objectValue, xlsCompositeColumnField, childRow, workbook);
            }
            isFirstRow = false;
        }
        return tempRowNo;
    }

    private void writeCompositeFieldRow(Object objectValue, XlsxField xlsCompositeColumnField, Row childRow, Workbook workbook) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method nestedCompositeXlsMethod = getMethod(objectValue.getClass(), xlsCompositeColumnField);
        Object nestedCompositeValue = nestedCompositeXlsMethod.invoke(objectValue);
        Cell compositeNewCell = childRow.createCell(xlsCompositeColumnField.getCellIndex());
        setCellValue(compositeNewCell, nestedCompositeValue, workbook);
    }

    private void writeArrayFieldRow(Row childRow, XlsxField xlsColumnField, Object objectValue, Workbook workbook) {
        Cell newCell = childRow.createCell(xlsColumnField.getCellIndex());
        setCellValue(newCell, objectValue, workbook);
    }

    private <T> void writeSingleFieldRow(Row mainRow, XlsxField xlsColumnField, T record, Workbook workbook) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Cell newCell = mainRow.createCell(xlsColumnField.getCellIndex());
        Method xlsMethod = getMethod(record.getClass(), xlsColumnField);
        Object xlsObjValue = xlsMethod.invoke(record);
        setCellValue(newCell, xlsObjValue, workbook);
    }

    private <T> boolean isNextColumnAnArray(List<XlsxField> xlsColumnFields, XlsxField xlsColumnField, T record) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int fieldsSize = xlsColumnFields.size();
        if (xlsColumnFields.indexOf(xlsColumnField) < (fieldsSize - 1)) {
            XlsxField nextXlsColumnField = xlsColumnFields.get(xlsColumnFields.indexOf(xlsColumnField) + 1);
            if (nextXlsColumnField.isAnArray()) {
                Method nestedXlsMethod = getMethod(record.getClass(), nextXlsColumnField);
                List<Object> nestedObjValueList = (List<Object>) nestedXlsMethod.invoke(record);
                return nestedObjValueList.size() > 1;
            }
        }
        return xlsColumnFields.indexOf(xlsColumnField) == (fieldsSize - 1);
    }

    private void setCellValue(Cell cell, Object objValue, Workbook workbook) {
        CellStyle currencyStyle = setCurrencyCellStyle(workbook);
        CellStyle centerAlignedStyle = getCenterAlignedCellStyle(workbook);
        CellStyle genericStyle = getLeftAlignedCellStyle(workbook, getGenericFont(workbook));
        Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);

        if (objValue != null) {
            if (objValue instanceof String) {
                String cellValue = (String) objValue;
                cell.setCellStyle(genericStyle);
                if (cellValue.contains("https://") || cellValue.contains("http://")) {
                    link.setAddress(cellValue);
                    cell.setCellValue(cellValue);
                    cell.setHyperlink(link);
                } else {
                    cell.setCellValue(cellValue);
                }
            } else if (objValue instanceof Long) {
                cell.setCellValue((Long) objValue);
            } else if (objValue instanceof Integer) {
                cell.setCellValue((Integer) objValue);
            } else if (objValue instanceof Double) {
                cell.setCellStyle(currencyStyle);
                cell.setCellValue((Double) objValue);
            } else if (objValue instanceof Boolean) {
                cell.setCellStyle(centerAlignedStyle);
                cell.setCellValue((Boolean) objValue ? 1 : 0);
            }
        }
    }

    private static List<XlsxField> getFieldNamesForClass(Class<?> clazz) {
        List<XlsxField> xlsColumnFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            XlsxField xlsColumnField = new XlsxField();
            if (Collection.class.isAssignableFrom(field.getType())) {
                xlsColumnField.setAnArray(true);
                XlsxCompositeField xlsCompositeField = field.getAnnotation(XlsxCompositeField.class);
                if (xlsCompositeField != null) {
                    xlsColumnField.setCellIndexFrom(xlsCompositeField.from());
                    xlsColumnField.setCellIndexTo(xlsCompositeField.to());
                    xlsColumnField.setComposite(true);
                } else {
                    XlsxSingleField xlsField = field.getAnnotation(XlsxSingleField.class);
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                }
            } else {
                XlsxSingleField xlsField = field.getAnnotation(XlsxSingleField.class);
                xlsColumnField.setAnArray(false);
                if (xlsField != null) {
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                    xlsColumnField.setComposite(false);
                }
            }
            xlsColumnField.setFieldName(field.getName());
            xlsColumnFields.add(xlsColumnField);
        }
        return xlsColumnFields;
    }

    private static String capitalize(String s) {
        if (s.isEmpty())
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private <T> int getMaxListSize(T record, List<XlsxField> xlsColumnFields, Class<?> aClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Integer> listSizes = new ArrayList<>();
        for (XlsxField xlsColumnField : xlsColumnFields) {
            if (xlsColumnField.isAnArray()) {
                Method method = getMethod(aClass, xlsColumnField);
                List<Object> objects = (List<Object>) method.invoke(record);
                if (objects.size() > 1) {
                    listSizes.add(objects.size());
                }
            }
        }
        return listSizes.isEmpty() ? 1 : Collections.max(listSizes);
    }

    private Method getMethod(Class<?> clazz, XlsxField xlsColumnField) throws NoSuchMethodException {
        try {
            return clazz.getMethod("get" + capitalize(xlsColumnField.getFieldName()));
        } catch (NoSuchMethodException nme) {
            return clazz.getMethod(xlsColumnField.getFieldName());
        }
    }

    private long processTime(long start) {
        return (System.currentTimeMillis() - start) / 1000;
    }

    private void autoSizeColumns(Sheet sheet, int noOfColumns) {
        for (int i = 0; i < noOfColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private Row getOrCreateNextRow(Sheet sheet, int rowNo) {
        Row row = sheet.getRow(rowNo);
        return row != null ? row : sheet.createRow(rowNo);
    }

    private CellStyle setCurrencyCellStyle(Workbook workbook) {
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setWrapText(true);
        DataFormat df = workbook.createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("#0.00"));
        return currencyStyle;
    }

    private Font getBoldFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    private Font getGenericFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    private CellStyle getCenterAlignedCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }

    private CellStyle getLeftAlignedCellStyle(Workbook workbook, Font font) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }
}
