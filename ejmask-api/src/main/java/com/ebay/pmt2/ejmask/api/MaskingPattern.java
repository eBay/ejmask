package com.ebay.pmt2.ejmask.api;

import java.util.regex.Pattern;

/**
 * Encapsulation for masking pattern
 *
 * @author prakv
 */
public class MaskingPattern implements Comparable<MaskingPattern> {

    private final int order;
    private final Pattern pattern;
    private final String replacement;

    /**
     * Create new instance of MaskPattern
     *
     * @param order       as order of this filter in chain
     * @param regex       as value to be replaced
     * @param replacement value replaced with
     */
    public MaskingPattern(int order, String regex, String replacement) {
        this.order = order;
        this.pattern = Pattern.compile(regex);
        this.replacement = replacement;
    }

    /**
     * Replace sensitive data with mask
     *
     * @param data as string with sensitive data
     * @return clean data
     */
    public String replaceAll(String data) {
        return this.pattern.matcher(data).replaceAll(this.replacement);
    }

    /**
     * Compare the given object this for sorting
     *
     * @param that the other pattern
     * @return 1 of this is having higher order and -1 else
     */
    @Override
    public int compareTo(MaskingPattern that) {
        if (that.order == this.order) {
            if (that.pattern.pattern().length() == this.pattern.pattern().length()) {
                return 0;
            }
            //one with more filters can go to the top
            return (that.pattern.pattern().length() > this.pattern.pattern().length()) ? -1 : 1;
        }
        return (that.order > this.order) ? -1 : 1;
    }

    /**
     * Returns the string representation of this pattern.
     *
     * @return The string representation of this pattern
     */
    @Override
    public String toString() {
        return "order=" + this.order + ";pattern=" + this.pattern.pattern() + ";replacement=" + this.replacement;
    }
}
