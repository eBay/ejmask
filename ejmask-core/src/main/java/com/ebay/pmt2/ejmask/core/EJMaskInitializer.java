package com.ebay.pmt2.ejmask.core;

import com.ebay.pmt2.ejmask.api.IContentProcessor;
import com.ebay.pmt2.ejmask.api.IFilter;
import com.ebay.pmt2.ejmask.api.IPatternBuilder;
import com.ebay.pmt2.ejmask.api.MaskingPattern;
import com.ebay.pmt2.ejmask.core.util.CommonUtils;
import com.ebay.pmt2.ejmask.core.util.LoggerUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The objective of this class it to wrap all complications in adding and
 * maintaining masking fields inside this class and keep the recurring jobs
 * simple. This class take cares handling.
 * <pre>
 * - duplicates filed names
 * - duplicates with different priorities.
 * - duplicates with different visibility configurations etc.
 * - multiple data types (json , xml , header etc.).
 * </pre>
 *
 * @author prakv
 */
public class EJMaskInitializer {

    /**
     * Add to filter group to EJMask. This is used if we need to configure without Auto Config.
     * these pattens will be added as it is to the filter chain without performing any de-duping logic.
     * Limit usage if not using auto config.
     *
     * @param order       as priority of this filter in chain
     * @param pattern     as value to be replaced
     * @param replacement value replaced with
     */
    public static synchronized void addMaskingPattern(int order, String pattern, String replacement) {
        EJMask.addFilter(order, pattern, replacement);
    }

    /**
     * Register the given MaskingPattern. once added we won't be
     * able to de register these MaskingPattern. These pattens will be added as it
     * is to the filter chain without performing any de-duping logic.
     * Limit usage of MaskingPattern for special cases.
     *
     * @param maskingPattern new value of MaskingPatterns
     */
    public static synchronized void addMaskingPattern(MaskingPattern maskingPattern) {
        EJMask.register(maskingPattern);
    }

    /**
     * Register the given list of MaskingPattern. once added we won't be
     * able to de register these MaskingPattern. These pattens will be added as it
     * is to the filter chain without performing any de-duping logic.
     * Limit usage of MaskingPattern for special cases.
     *
     * @param maskingPatterns new value of MaskingPatterns
     */
    public static synchronized void addMaskingPatterns(Collection<MaskingPattern> maskingPatterns) {
        if (CommonUtils.isNotEmpty(maskingPatterns)) {
            EJMask.register(maskingPatterns.toArray(new MaskingPattern[]{}));
        }
    }

    /**
     * Register the given list of IContentPreProcessor. once added we wont be
     * able to de register these IContentPreProcessors
     *
     * @param contentProcessors new value of contentProcessors
     */
    public static synchronized void addContentProcessors(Collection<IContentProcessor> contentProcessors) {
        for (IContentProcessor contentPreProcessor : CommonUtils.emptyIfNull(contentProcessors)) {
            EJMask.register(contentPreProcessor);
            LoggerUtil.info("data-filter-initializer", "processors", "adding " + contentPreProcessor.getName());
        }
    }

    /**
     * Register the given list of IFilter. once added we won't be able to de
     * register these IFilter
     *
     * @param filters as list of IFilter
     */
    public static synchronized void addFilters(Collection<IFilter> filters) {
        if (CommonUtils.isEmpty(filters)) {
            LoggerUtil.info("data-filter-initializer", "filter-pattern", "empty list of filters.");
            return;
        }
        List<Filter> filterGroups = removeDuplicatesAndBuildFilterGroups(filters);
        //time to update filter pattern
        for (Filter filter : filterGroups) {
            //avoid empty due to duplicate
            if (CommonUtils.isNotEmpty(filter.getFieldNames())) {
                final String[] fieldNames = toArray(filter.getFieldNames());
                final String pattern = filter.getBuilder().buildPattern(filter.getVisibleCharacters(), fieldNames);
                final String replacement = filter.getBuilder().buildReplacement(filter.getVisibleCharacters(), fieldNames);
                //add masking pattern to data masking utility
                addMaskingPattern(filter.getOrder(), pattern, replacement);
            }
        }
        addNonGroupedFilters(filters);
        //sort for order
        for (MaskingPattern filterPattern : EJMask.getMaskingPatterns()) {
            LoggerUtil.info("data-filter-initializer", "filter-pattern", filterPattern.toString());
        }
    }

    /**
     * Get list of active MaskingPattern
     *
     * @return list of configured MaskingPattern
     */
    public static List<MaskingPattern> getMaskingPatterns() {
        return EJMask.getMaskingPatterns();
    }

    /**
     * Get list of active list of IContentPreProcessor
     *
     * @return list of configured IContentPreProcessor
     */
    public static List<IContentProcessor> getContentPreProcessors() {
        return EJMask.getContentPreProcessors();
    }

    /**
     * Add non grouped filter
     *
     * @param filters as list of IFilter
     */
    private static void addNonGroupedFilters(Collection<IFilter> filters) {
        for (IFilter ifilter : filters) {
            final IPatternBuilder builder = getBuilder(ifilter.getPatternBuilder());
            if (!builder.isGroupable()) {
                final String pattern = builder.buildPattern(ifilter.getVisibleCharacters(), ifilter.getFieldNames());
                final String replacement = builder.buildReplacement(ifilter.getVisibleCharacters(), ifilter.getFieldNames());
                EJMask.addFilter(ifilter.getOrder(), pattern, replacement);
            }
        }
    }

    /**
     * Operation to remove all duplicates and build filter groups from which we
     * can create the final pattern.
     * <pre>
     * Note: This method is optimized for readability than runtime performance as this method executes only once at the time of server start up.
     *
     * @param filters as list of List
     * @return filter filters grouped with pattern, order and visible characters
     */
    private static List<Filter> removeDuplicatesAndBuildFilterGroups(@Nonnull Collection<IFilter> filters) {
        List<Filter> filterGroups = new LinkedList<>();
        Map<Class, Map<String, IFilter>> groupedByBuilder = groupByBuilderType(filters);
        for (Map.Entry<Class, Map<String, IFilter>> entry : groupedByBuilder.entrySet()) {
            filterGroups.addAll(groupByOrderAndVisibleCharacters(entry.getKey(), entry.getValue()));
        }
        return filterGroups;
    }

    /**
     * Operation to group by pattern builder. duplicates are not allowed with in
     * the same group. This method removes duplicates with more visible
     * characters and with higher order ( p1 is gt than p100)
     *
     * @param filters a non-nullable list of IFilter
     * @return Builder name to filter name group
     */
    @Nonnull
    private static Map<Class, Map<String, IFilter>> groupByBuilderType(@Nonnull Collection<IFilter> filters) {
        Map<Class, Map<String, IFilter>> builderGroup = new HashMap<>();
        for (IFilter currentFilter : filters) {
            if (CommonUtils.isNotAnEmptyArray(currentFilter.getFieldNames())) {
                //this operation is to avoid duplicate field names with in builder.
                Map<String, IFilter> groupByFieldName = getOrPutNewIfNull(builderGroup, currentFilter.getPatternBuilder());
                for (String fieldName : currentFilter.getFieldNames()) {
                    IFilter oldFilter = groupByFieldName.get(fieldName);
                    if ((oldFilter == null)
                            || //filter with lesser number of visible characters will be having higher precedence
                            (oldFilter.getVisibleCharacters() > currentFilter.getVisibleCharacters())
                            || //allow to override order of the number of visible characters are same.
                            ((oldFilter.getVisibleCharacters() == currentFilter.getVisibleCharacters()) && (oldFilter.getOrder() > currentFilter.getOrder()))) {
                        groupByFieldName.put(fieldName, currentFilter);
                    }
                }
            }
        }
        return builderGroup;
    }

    /**
     * Regroup the given collection of IFilter with builder types as key based
     * on order and visibleCharacters
     *
     * @param builderClass             type of builder
     * @param fieldNameToFilterMapping as fieldName to filter mapping
     * @return list of Filter
     */
    private static List<Filter> groupByOrderAndVisibleCharacters(Class<? extends IPatternBuilder> builderClass, Map<String, IFilter> fieldNameToFilterMapping) {
        final IPatternBuilder builder = getBuilder(builderClass);
        if (!builder.isGroupable()) {
            return Collections.emptyList();
        }
        Map<String, Filter> groupingMap = new HashMap<>();
        for (Map.Entry<String, IFilter> entry : fieldNameToFilterMapping.entrySet()) {
            IFilter ifilter = entry.getValue();
            String key = buildGroupKey(ifilter);
            if (!groupingMap.containsKey(key)) {
                groupingMap.put(key, new Filter(ifilter.getOrder(), ifilter.getVisibleCharacters(), builder));
            }
            groupingMap.get(key).add(entry.getKey());
        }
        return new ArrayList<>(groupingMap.values());
    }

    /**
     * Get value for the given key in case if the value is
     *
     * @param map as a Map to lookup
     * @param key as the lookup key
     * @return as value.
     */
    @Nonnull
    private static Map<String, IFilter> getOrPutNewIfNull(Map<Class, Map<String, IFilter>> map, Class key) {
        if (!map.containsKey(key)) {
            map.put(key, new HashMap<>());
        }
        return map.get(key);
    }

    /**
     * Create new instance of IPatternBuilder
     *
     * @param builder as class extends IPatternBuilder
     * @return instance of IPatternBuilder
     */
    private static IPatternBuilder getBuilder(Class<? extends IPatternBuilder> builder) {
        try {
            return builder.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BuilderInitializerException("unable to initialize builder of type: " + builder.getName(), e);
        }
    }

    /**
     * Builds grouping key for the given instance of IFilter
     *
     * @param filter instance of IFilter
     * @return grouping key as string
     */
    private static String buildGroupKey(IFilter filter) {
        return filter.getOrder() + "-" + filter.getVisibleCharacters();
    }

    /**
     * Convert the given list into a sorted String array. Sorting does't have
     * any impact on functionality. bellow are the reasons when we are
     * performing
     * <pre>
     *  - keep unit tests and integration test assertions pass in different environment
     *  - easy to find field names in regex pattern.
     *
     * @param list list of string
     * @return an array of string.
     */
    private static String[] toArray(List<String> list) {
        Collections.sort(list);
        return list.toArray(new String[]{});
    }
}