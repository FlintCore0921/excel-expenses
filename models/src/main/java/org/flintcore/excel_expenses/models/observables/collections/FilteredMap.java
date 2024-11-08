package org.flintcore.excel_expenses.models.observables.collections;

import data.utils.NullableUtils;
import data.utils.collections.ICollectionUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilteredMap<K, V> implements ObservableMap<K, V> {
    @Getter
    private final ObservableMap<K, V> sourceMap;
    private ObservableMap<K, V> filterMap;
    private final ReadOnlyObjectWrapper<Predicate<Entry<K, V>>> predicateProperty;

    public FilteredMap(@NonNull final ObservableMap<K, V> mapProperty,
                       @NonNull final ObservableMap<K, V> initialValue,
                       Predicate<Entry<K, V>> predicate) {
        this.sourceMap = mapProperty;
        this.filterMap = FXCollections.observableMap(initialValue);
        this.predicateProperty = new ReadOnlyObjectWrapper<>(this, "predicate", predicate);
        applyListeners();
    }

    public FilteredMap(@NonNull ObservableMap<K, V> mapProperty, Predicate<Entry<K, V>> predicate) {
        this.sourceMap = mapProperty;
        this.predicateProperty = new ReadOnlyObjectWrapper<>(this, "predicate", predicate);
        applyListeners();
    }

    public FilteredMap(@NonNull ObservableMap<K, V> mapProperty) {
        this(mapProperty, null);
    }


    public ObservableMap<K, V> filteredProperty() {
        if (Objects.isNull(this.filterMap)) {
            this.filterMap = FXCollections.observableHashMap();
        }

        return this.filterMap;
    }

    public void filterByKey(Predicate<K> predicate) {
        this.predicateProperty.set(entry -> predicate.test(entry.getKey()));
    }

    public void filterByValue(Predicate<V> predicate) {
        this.predicateProperty.set(entry -> predicate.test(entry.getValue()));
    }

    public void filterBy(Predicate<Entry<K, V>> entryPredicate) {
        this.predicateProperty.set(entryPredicate);
    }

    public ReadOnlyObjectProperty<Predicate<Entry<K, V>>> predicateProperty() {
        return this.predicateProperty.getReadOnlyProperty();
    }

    @Override
    public void addListener(MapChangeListener<? super K, ? super V> listener) {
        this.filteredProperty().addListener(listener);
    }

    @Override
    public void removeListener(MapChangeListener<? super K, ? super V> listener) {
        this.filteredProperty().addListener(listener);
    }

    @Override
    public int size() {
        return (Objects.isNull(this.filterMap)) ? 0 : this.filterMap.size();
    }

    @Override
    public boolean isEmpty() {
        return Objects.isNull(this.filterMap) || this.filterMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.filteredProperty().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.filteredProperty().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.filteredProperty().get(key);
    }

    @Override
    public V put(K key, V value) {
        return this.filteredProperty().put(key, value);
    }

    @Override
    public V remove(Object key) {
        return this.filteredProperty().remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        NullableUtils.executeNonNull(m, this.filteredProperty()::putAll);
    }

    @Override
    public void clear() {
        if (Objects.isNull(this.filterMap)) return;
        this.filterMap.clear();
    }

    @Override
    @NonNull
    public Set<K> keySet() {
        return this.filteredProperty().keySet();
    }

    @Override
    @NonNull
    public Collection<V> values() {
        return this.filteredProperty().values();
    }

    @Override
    @NonNull
    public Set<Entry<K, V>> entrySet() {
        return this.filteredProperty().entrySet();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        this.filteredProperty().removeListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        this.filteredProperty().removeListener(listener);
    }

    protected void applyListeners() {
        this.sourceMap.addListener((MapChangeListener<? super K, ? super V>) changes -> {
            if (changes.wasRemoved()) {
                this.filteredProperty().put(changes.getKey(), changes.getValueRemoved());
            }

            if (changes.wasAdded()) {
                this.filteredProperty().put(changes.getKey(), changes.getValueAdded());
            }
        });

        // If predicate changes, trigger remove or
        this.predicateProperty().when(Bindings.isNotEmpty(this.sourceMap))
                .subscribe(checker -> reEvaluateValues());
    }

    private void reEvaluateValues() {
        Predicate<Entry<K, V>> predicate = this.predicateProperty().getValue();
        ObservableMap<K, V> filterList = this.filteredProperty();

        if(Objects.isNull(predicate)) return;

        Set<K> keysToRemove = filterList.entrySet().stream()
                .filter(predicate.negate())
                .map(Entry::getKey)
                .collect(Collectors.toSet());

        // Remove entries that don't match predicate
        keysToRemove.forEach(filterList::remove);

        // Add missing entries that match predicate
        sourceMap.entrySet().stream()
                .filter(predicate)
                .filter(ICollectionUtils.Filter.notContains(filterList.entrySet()))
                .forEach(entry -> filterList.put(entry.getKey(), entry.getValue()));
    }
}
