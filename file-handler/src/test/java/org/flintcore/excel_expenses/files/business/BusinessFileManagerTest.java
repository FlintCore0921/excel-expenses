package org.flintcore.excel_expenses.files.business;

import org.flintcore.excel_expenses.files.SerializableFileCreator;
import org.flintcore.excel_expenses.files.SerializableFileFinder;
import org.flintcore.excel_expenses.files.SerializeReader;
import org.flintcore.excel_expenses.files.SerializeWriter;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
        ArrayList<LocalBusiness> objects = new ArrayList<>();
        LocalBusiness localBusiness = new LocalBusiness("Local", "place");
        objects.add(localBusiness);

        SerialListHolder<LocalBusiness> listHolder = new SerialListHolder<>(objects);

        localBusinessFileManager.updateDataSet(listHolder);
        List<LocalBusiness> dataList = localBusinessFileManager.getDataList();

        assertFalse(dataList.isEmpty());
        assertTrue(dataList.contains(localBusiness));
    }
}