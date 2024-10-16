package org.flintcore.excel_expenses.files.business;

import org.flintcore.excel_expenses.files.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(
        classes = {
                LocalBusinessFileManager.class,
                SerializeWriter.class,
                SerializableFileCreator.class,
                SerializableFileFinder.class,
                SerializeReader.class
        }
)
class BusinessFileManagerTest {
    @Autowired
    LocalBusinessFileManager localBusinessFileManager;


    @Test
    void shouldMarkCorrectPath() {
        assertNotNull(localBusinessFileManager);

        localBusinessFileManager.updateDataSet(new ArrayList<>());
        localBusinessFileManager.getDataList();
    }
}