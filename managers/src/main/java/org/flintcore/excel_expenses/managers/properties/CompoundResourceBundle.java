package org.flintcore.excel_expenses.managers.properties;

import data.utils.NullableUtils;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Class to hold all bundles based on Bundles Enum key.
 * The class will be notified if user change or request a Locale change and override all data.
 */
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
        if (bundleDictionary.containsKey(bundle)) return;

        final String bundlePath = bundle.location;

        this.bundleDictionary.putIfAbsent(bundle, loadBundle(bundlePath));
    }

    private ResourceBundle loadBundle(String bundleNamePathCompleted) {
        return ResourceBundle.getBundle(bundleNamePathCompleted);
    }

    private void onInitDictionary() {
        NullableUtils.executeIsNull(this.bundleDictionary, () -> {
            this.bundleDictionary = new EnumMap<>(Bundles.class);
            registerBundles(Bundles.defaultBundles());
        });
    }

    @Override
    @Nullable
    protected Object handleGetObject(@NonNull String key) {
        if(Objects.isNull(this.bundleDictionary)) return null;

        return this.bundleDictionary.values().stream()
                .filter(bd -> bd.containsKey(key))
                .findFirst()
                .map(res -> res.getObject(key))
                .orElse(null);
    }

    @Override
    public boolean containsKey(@NonNull String key) {
        return Objects.nonNull(this.bundleDictionary) && this.bundleDictionary.values().stream()
                .anyMatch(bd -> bd.containsKey(key));
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

    public static String getBundlePathStr(@NonNull String... paths) {
        return Path.of("bundles", paths).toString();
    }

    @AllArgsConstructor
    @Getter
    public enum Bundles {
        GENERAL(getBundlePathStr("general_messages")),
        ACTIONS(getBundlePathStr("actions_messages"));

        private final String location;

        public static Bundles[] defaultBundles() {
            return new Bundles[]{GENERAL, ACTIONS};
        }
    }
}
