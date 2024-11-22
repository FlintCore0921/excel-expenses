package org.flintcore.excel_expenses.managers.properties;

import data.utils.NullableUtils;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;

@Component
public class CompoundResourceBundle extends ResourceBundle {
    @Deprecated
    private static final String PROPERTIES_SUFFIX = ".properties";

    private Map<Bundles, ResourceBundle> bundleDictionary;

    public void registerBundles(Bundles... bundles) {
        if (Objects.isNull(bundles)) return;
        Arrays.stream(bundles).forEach(this::registerBundle);
    }

    public void registerBundle(@NonNull Bundles bundle) {
        onInitDictionary();
        final String bundleNamePathCompleted = bundle.location;
        this.bundleDictionary.putIfAbsent(bundle, loadBundle(bundleNamePathCompleted));
    }

    private ResourceBundle loadBundle(String bundleNamePathCompleted) {
        return ResourceBundle.getBundle(bundleNamePathCompleted);
    }

    private void onInitDictionary() {
        NullableUtils.executeIsNull(this.bundleDictionary,
                () -> {
                    this.bundleDictionary = new EnumMap<>(Bundles.class);
                    registerBundles(Bundles.getDefaultBundles());
                }
        );
    }

    @Override
    @Nullable
    protected Object handleGetObject(@NonNull String key) {
        return this.bundleDictionary.values().stream()
                .filter(bd -> bd.containsKey(key))
                .findFirst()
                .map(res -> res.getObject(key))
                .orElse(null);
    }

    @Override
    @NonNull
    public Enumeration<String> getKeys() {
        List<String> results = this.bundleDictionary.values().stream()
                .map(res -> res.getKeys().asIterator())
                .flatMap(res -> StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(res, Spliterator.ORDERED), false)
                )
                .toList();

        return Collections.enumeration(results);
    }

    @AllArgsConstructor
    @Getter
    public enum Bundles {
        GENERAL("fxBundles/messages/general_messages"),
        LOCAL_MESSAGES("fxBundles/messages/local_messages"),
        EXPENSE_CREATE_FORM("fxBundles/messages/expenses_create_form"),
        LOCAL_RECEIPT("fxBundles/messages/local_receipt_messages");

        private final String location;

        public static Bundles[] getDefaultBundles() {
            return new Bundles[]{GENERAL};
        }
    }
}
