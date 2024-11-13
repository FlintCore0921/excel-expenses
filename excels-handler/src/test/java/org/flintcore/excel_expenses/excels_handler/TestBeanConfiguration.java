package org.flintcore.excel_expenses.excels_handler;

import org.flintcore.excel_expenses.excels_handler.config.BeanConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Deprecated
@TestConfiguration
@Import(BeanConfiguration.class)
@ComponentScan(basePackages = {
        "org.flintcore.excel_expenses.excels_handler"
})
public class TestBeanConfiguration {
}
