package vn.com.fecredit.pega.activity.model

class Step {
    int id
    String method
    Map params = [:]
    String description

    // Pega-like fields
    String stepPage       // e.g., Step Page name (primary page context)
    String pageClass      // optional class for the step page
    def whenCondition    // Closure or String expression used by a When
    boolean returns = false
    String returnPage
    boolean required = false
    String stepType       // optional type/category

    // Loop / Jump support (Pega Step features)
    String label               // optional label for this step (jump target)
    boolean loop = false      // if true, repeat the step (requires loopCondition or loopCount to be meaningful)
    def loopCondition         // Closure | String | Boolean to evaluate for repeating
    Integer loopCount         // number of iterations (if set)
    String loopProperty
    String loopPage
    List<Step> steps

    String jumpTo             // label or step id to jump to after this step
    def jumpCondition         // optional condition to control the jump (Closure | String | Boolean)

    // Precondition support (from Pega Step Precondition)
    def precondition          // Closure | String | Boolean evaluated before the step to decide whether to run it
    boolean preconditionNegate = false // if true, invert the precondition result

    // Convenience setters for DSL usage
    void param(String k, Object v) { params[k] = v }
    void params(Map m) { params.putAll(m) }

    String toString() {
        return "Step(id=${id}, method=${method}, stepPage=${stepPage}, params=${params}, label=${label})"
    }
}
