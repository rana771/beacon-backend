package com.athena.mis.kendo.grid

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mohammad Ali Azam on 9/22/2014.
 */
class FilterParamsUtil {

    /**
     *  Following function is not yet completely defensive
     */
    public static Map<Long, FilterOption> parseFilterParams(GrailsParameterMap params) {

        def indexerPattern = ~/(\[\d+\])(\[.*\])/;
        def pattern =  ~/filter\[filters\].*/;
        Map<Long, FilterOption> filterOptionMap = new HashMap<Long, FilterOption>();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String name = entry.key;
            if (pattern.matcher(name).matches()) {
               String prop = name.replaceFirst(/filter\[filters\]/, '');
               def matcher = indexerPattern.matcher(prop);
               if (matcher.matches()) {
                   String index = matcher.group(1).replaceAll(~/\[|\]/, '');
                   String property = matcher.group(2).replaceAll(~/\[|\]/, '');

                   FilterOption filter = filterOptionMap.get(index.toLong());
                   if (!filter) {
                     filter = new FilterOption();
                     filterOptionMap.put(index.toLong(), filter);
                   }

                   try {
                       FilterOption.getField(property).set(filter, entry.value);
                   } catch (Exception ignored) {
                       ignored.printStackTrace();
                   }
               }
            }
         };

        return filterOptionMap;

    }
}
