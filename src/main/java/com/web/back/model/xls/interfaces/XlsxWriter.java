package com.web.back.model.xls.interfaces;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface XlsxWriter {

    <T> void write(List<T> data, ByteArrayOutputStream bos, String[] columnTitles, Workbook workbook);

    <T> void appendSheet(List<T> data, ByteArrayOutputStream bos, String[] columnTitles, Workbook workbook, String sheetName);
}
