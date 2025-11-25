import spock.lang.Specification
import com.pega.pegarules.pub.clipboard.*

class ClipboardCoreTest extends Specification {

    def "ClipboardPropertyType enum contains expected values"() {
        expect:
        Arrays.asList(ClipboardPropertyType.values()).containsAll([
            ClipboardPropertyType.STRING,
            ClipboardPropertyType.INTEGER,
            ClipboardPropertyType.DECIMAL,
            ClipboardPropertyType.BOOLEAN,
            ClipboardPropertyType.DATE,
            ClipboardPropertyType.PAGE,
            ClipboardPropertyType.PAGELIST,
            ClipboardPropertyType.JAVA_OBJECT_LIST,
            ClipboardPropertyType.JAVA_OBJECT_GROUP,
            ClipboardPropertyType.JAVA_PROPERTY,
            ClipboardPropertyType.JAVA_PROPERTY_LIST
        ])
    }

    def "CodePegaList constructor initializes pxObjClass and pxResults"() {
        when:
        def list = new CodePegaList()
        then:
        list.getPropertyObject('pxObjClass') != null
        list.getPropertyObject('pxResults') != null
    }

    def "CodePegaList getPxResultsList returns a List"() {
        given:
        def list = new CodePegaList([[:], [:]])
        expect:
        list.getPxResultsList() instanceof List
    }

    def "Page constructors work with different input types"() {
        expect:
        new Page() instanceof Page
        new Page(new SimpleClipboardPage([key: "value"])) instanceof Page
        
        new Page("testPage", new SimpleClipboardPage([key: "value"]), ClipboardPropertyType.PAGE) instanceof Page
    }

    def "SingleValue constructor sets name and value"() {
        when:
        def sv = new SingleValue("testName", "testValue", ClipboardProperty.TYPE_TEXT)
        then:
        sv.name == "testName"
        sv.getPropertyValue() == "testValue"
        sv.getType() == ClipboardProperty.TYPE_TEXT
    }

    def "ValueList constructor sets values"() {
        when:
        def vl = new ValueList(["a", "b", "c"])
        then:
        vl.getPropertyValue() == ["a", "b", "c"]
    }
    def "CodePegaList handles null and empty input gracefully"() {
        expect:
        new CodePegaList(null).getPxResultsList() != null
        new CodePegaList([]).getPxResultsList() == []
    }

    def "Page constructor with ClipboardPage copies properties"() {
        given:
        def src = new Page([foo: "bar"])
        def copy = new Page(src)
        expect:
        copy.getProperty("foo").getStringValue() == "bar"
    }

    def "SingleValue constructor with null value"() {
        when:
        def sv = new SingleValue("nullName", null, ClipboardProperty.TYPE_TEXT)
        then:
        sv.name == "nullName"
        sv.getPropertyValue() == null
    }

    def "ValueList constructor with null and empty list"() {
        expect:
        new ValueList(null).getPropertyValue() == null
        new ValueList([]).getPropertyValue() == []
    }

    def "SimpleClipboardProperty constructor with name and value sets properties"() {
        when:
        def scp = new SimpleClipboardProperty("testName", "testValue")
        then:
        scp.name == "testName"
        scp.getPropertyValue() == "testValue"
        scp.type == ClipboardProperty.TYPE_TEXT
    }

    def "SimpleClipboardProperty add(Object) covers null value initialization"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = null // Ensure value is null initially

        when:
        scp.add("firstValue")

        then:
        scp.getPropertyValue() == ["firstValue"]
        println "DEBUG: scp.getType() after add = ${scp.getType()}, expected = ${ClipboardProperty.TYPE_UNKNOWN}"
        scp.getType() == ClipboardProperty.TYPE_UNKNOWN // Type should now be UNKNOWN as it's a List
    }

    def "SimpleClipboardProperty add(Object) covers adding to an existing List"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = ["initialValue"]

        when:
        scp.add("newValue")

        then:
        scp.getPropertyValue() == ["initialValue", "newValue"]
        scp.getType() == ClipboardProperty.TYPE_UNKNOWN
    }

    def "SimpleClipboardProperty add(Object) covers adding to a non-list value"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = "singleValue"

        when:
        scp.add("anotherValue")

        then:
        scp.getPropertyValue() == ["singleValue", "anotherValue"]
        scp.getType() == ClipboardProperty.TYPE_UNKNOWN
    }

    def "SimpleClipboardProperty add(int, Object) covers non-list value initialization"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = "initialValue"

        when:
        scp.add(0, "newValue")

        then:
        scp.getPropertyValue() == ["newValue", "initialValue"]
        scp.getType() == ClipboardProperty.TYPE_UNKNOWN
    }

    def "SimpleClipboardProperty add(int, Object) covers adding to an existing List"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = ["value1", "value3"]

        when:
        scp.add(1, "value2")

        then:
        scp.getPropertyValue() == ["value1", "value2", "value3"]
        scp.getType() == ClipboardProperty.TYPE_UNKNOWN
    }

    def "SimpleClipboardProperty clearValue() sets value to null"() {
        given:
        def scp = new SimpleClipboardProperty("testValue")

        when:
        scp.clearValue()

        then:
        scp.getPropertyValue() == null
        scp.getType() == ClipboardProperty.TYPE_UNKNOWN // Type should be UNKNOWN for null
    }

    def "SimpleClipboardProperty contains(Object) covers List containing value"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = ["value1", "value2"]

        expect:
        scp.contains("value1") == true
        scp.contains("value3") == false
    }

    def "SimpleClipboardProperty contains(Object) covers Map containing key or value"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = [key1: "value1", key2: "value2"]

        expect:
        scp.contains("key1") == true
        scp.contains("value1") == true
        scp.contains("key3") == false
        scp.contains("value3") == false
    }

    def "SimpleClipboardProperty contains(Object) covers single value comparison"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = "singleValue"

        expect:
        scp.contains("singleValue") == true
        scp.contains("anotherValue") == false
        scp.contains(null) == false
    }

    def "SimpleClipboardProperty doBackwardChain() is called"() {
        given:
        def scp = new SimpleClipboardProperty()

        when:
        scp.doBackwardChain()

        then:
        // No explicit assertion needed as it's an empty method, just ensure it can be called without error
        noExceptionThrown()
    }

    def "SimpleClipboardProperty get(int) covers List value"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = ["item1", "item2"]

        when:
        def result = scp.get(0)

        then:
        result instanceof SimpleClipboardProperty
        result.getPropertyValue() == "item1"
    }

    def "SimpleClipboardProperty get(int) covers non-List value"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = "singleValue"

        when:
        def result = scp.get(0)

        then:
        result == null
    }

    def "SimpleClipboardProperty get(String) covers Map value"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = [key1: "value1", key2: "value2"]

        when:
        def result = scp.get("key1")

        then:
        result instanceof SimpleClipboardProperty
        result.getPropertyValue() == "value1"
    }

    def "SimpleClipboardProperty get(String) covers non-Map value"() {
        given:
        def scp = new SimpleClipboardProperty()
        scp.value = "singleValue"

        when:
        def result = scp.get("someKey")

        then:
        result == null
    }

    def "SimpleClipboardProperty getAbsoluteReference() returns null"() {
        given:
        def scp = new SimpleClipboardProperty()

        expect:
        scp.getAbsoluteReference() == null
    }
}
