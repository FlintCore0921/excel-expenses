package org.flintcore.excel_expenses.excels_handler.config;

import org.flintcore.excelib.commons.executors.DefaultThreadPoolHolder;
import org.flintcore.excelib.commons.executors.ThreadPoolHandler;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
// Add this to recognize out of scope projects.
@ComponentScan(basePackages = {
        "org.flintcore.excelib.services"
})
public class BeanConfiguration {

//
//    @Bean
//    @Qualifier("excelServiceHandler")
//    public XSSFFileService excelServiceHandler(
//            final ThreadPoolHandler<ThreadPoolExecutor> executorHandler
//    ) {
//        return new XSSFFileService(executorHandler);
//    }
//
//    @Bean
//    @Qualifier("excelThreadPool")
//    public ThreadPoolHandler<ThreadPoolExecutor> excelThreadPoolHandler() {
//        return new DefaultThreadPoolHolder();
//    }
}
