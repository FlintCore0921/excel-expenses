package org.flintcore.excel_expenses.files.business;

import org.flintcore.excel_expenses.serializable.SerializableFileCreator;
import org.flintcore.excel_expenses.serializable.SerializableFileFinder;
import org.flintcore.excel_expenses.serializable.SerializeReader;
import org.flintcore.excel_expenses.serializable.SerializeWriter;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = {
                LocalBusinessSerializeFileManager.class,
                SerializeWriter.class,
                SerializableFileCreator.class,
                SerializableFileFinder.class,
                SerializeReader.class
        }
)
class BusinessSerializeFileManagerTest {
    @Autowired
    LocalBusinessSerializeFileManager localBusinessFileManager;

    @Test
    void shouldMarkCorrectPath() {
        assertNotNull(localBusinessFileManager);
        ArrayList<LocalBusiness> objects = new ArrayList<>();
        LocalBusiness localBusiness = new LocalBusiness("Local", "place");
        objects.add(localBusiness);

        SerialListHolder<LocalBusiness> listHolder = SerialListHolder.from(objects);

        localBusinessFileManager.updateDataSet(listHolder);
        List<LocalBusiness> dataList = localBusinessFileManager.getDataList();

        assertFalse(dataList.isEmpty());
        assertTrue(dataList.contains(localBusiness));
    }
}