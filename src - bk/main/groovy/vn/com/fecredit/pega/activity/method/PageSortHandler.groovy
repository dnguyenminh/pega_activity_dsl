package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.model.Step
import vn.com.fecredit.pega.activity.StepHandler
import vn.com.fecredit.pega.activity.ExpressionEvaluator

import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

/**
 * PageSortHandler - sorts a page list stored at a target path.
 *
 * Parameters (in s.params):
 * - target: path to the list to sort (required)
 * - by:     property path to sort on (optional). If omitted, list elements themselves are compared.
 * - order:  "asc" (default) or "desc"
 * - algorithm: "bubble" to force bubble-sort (for educational/pure-activity parity); otherwise uses JVM sort
 *
 * This handler performs an in-place sort using the ExpressionEvaluator.compare helper
 * for value comparison (supports numbers, strings, nulls).
 */
class PageSortHandler implements StepHandler {

    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String targetKey = s.params['target'] as String
        String targetPath = resolvePath(targetKey, s)
        String by = s.params['by'] as String
        String order = (s.params['order'] as String) ?: 'asc'
        String algorithm = (s.params['algorithm'] as String) ?: ''
        boolean descending = order?.toLowerCase() == 'desc'

        Object raw = PropertyUtils.get(ctx, targetPath)
        if (!(raw instanceof List)) {
            logger.warn("Page-Sort target ${targetPath} is not a list; skipping")
            return
        }

        List list = (List) raw
        if (list.size() <= 1) {
            logger.debug("Page-Sort target ${targetPath} has ${list.size()} items; nothing to sort")
            return
        }

        if (algorithm?.toLowerCase() == 'bubble') {
            // implement bubble sort (in-place)
            int n = list.size()
            for (int i = 0; i < n - 1; i++) {
                boolean swapped = false
                for (int j = 0; j < n - i - 1; j++) {
                    Object va = getValueForSort(list[j], by)
                    Object vb = getValueForSort(list[j + 1], by)
                    int cmp = ExpressionEvaluator.compare(va, vb)
                    boolean shouldSwap = descending ? (cmp < 0) : (cmp > 0)
                    if (shouldSwap) {
                        def tmp = list[j]
                        list[j] = list[j + 1]
                        list[j + 1] = tmp
                        swapped = true
                    }
                }
                if (!swapped) {
                    break
                }
            }
        } else {
            // delegate to JVM sort (stable behavior not strictly guaranteed but sufficient)
            list.sort { Object a, Object b ->
                Object va = getValueForSort(a, by)
                Object vb = getValueForSort(b, by)
                int cmp = ExpressionEvaluator.compare(va, vb)
                return descending ? -cmp : cmp
            }
        }

        // store back (in case path resolves to nested map structure)
        PropertyUtils.set(ctx, targetPath, list)
        logger.debug("Page-Sort sorted ${targetPath} by ${by ?: '[self]'} order=${order} algorithm=${algorithm ?: 'jvm'} (items=${list.size()})")
    }

    private Object getValueForSort(Object item, String by) {
        if (!by) {
            return item
        }
        if (item instanceof Map) {
            return PropertyUtils.get((Map) item, by)
        }
        // fallback: try PropertyUtils against a map-like object by wrapping
        try {
            return PropertyUtils.get([it: item], "it.${by}")
        } catch (Exception e) {
            return null
        }
    }
}