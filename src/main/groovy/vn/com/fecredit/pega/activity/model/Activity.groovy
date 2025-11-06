package vn.com.fecredit.pega.activity

import groovy.transform.CompileStatic

/**
 * Activity is a container for steps and metadata.
 */
@CompileStatic
class Activity {

    String name

    List<Step> steps = []

    Map<String, Object> meta = [:]

    void addStep(Step s) {
        steps << s
    }

    Step findStepById(int id) {
        return steps.find { Step st -> st.id == id }
    }

}
