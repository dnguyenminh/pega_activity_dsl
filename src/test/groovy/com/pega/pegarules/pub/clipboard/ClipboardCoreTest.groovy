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
        new Page([key: "value"]) instanceof Page
        new Page(["a", "b"]) instanceof Page
        new Page("testPage", [key: "value"], ClipboardPropertyType.PAGE) instanceof Page
    }

    def "SingleValue constructor sets name and value"() {
        when:
        def sv = new SingleValue("testName", "testValue", ClipboardPropertyType.STRING)
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
        def sv = new SingleValue("nullName", null, ClipboardPropertyType.STRING)
        then:
        sv.name == "nullName"
        sv.getPropertyValue() == null
    }

    def "ValueList constructor with null and empty list"() {
        expect:
        new ValueList(null).getPropertyValue() == null
        new ValueList([]).getPropertyValue() == []
    }
}
