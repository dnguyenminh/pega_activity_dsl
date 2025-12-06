import groovy.lang.Binding
import spock.lang.Specification

class PegaDslDataTransformForwardersSmokeTest extends Specification {

    def "script constructors and main execute without work"() {
        given:
        def binding = new Binding(foo: 'bar')

        when:
        def scriptWithBinding = new PegaDslDataTransformForwarders(binding)
        def defaultScript = new PegaDslDataTransformForwarders()

        then:
        scriptWithBinding.run() == null
        defaultScript.run() == null

        when:
        PegaDslDataTransformForwarders.main(new String[0])

        then:
        notThrown(Exception)
    }
}
