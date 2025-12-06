import com.pega.pegarules.pub.clipboard.*
import java.util.Set

class LocalFlakyMap extends LinkedHashMap {
    boolean first = true
    @Override
    Set entrySet() {
        if (first) {
            first = false
            throw new RuntimeException('boom')
        }
        super.entrySet()
    }
}

def m = new LocalFlakyMap()
m.alpha = 'A'

def method = AbstractClipboardPage.getDeclaredMethod('_toSimpleClipboardPageSafe', Object)
method.accessible = true

def host = new SimpleClipboardPage()
def result = method.invoke(host, m)
println result
println result?.getAt('alpha')
