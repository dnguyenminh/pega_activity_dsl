package vn.com.fecredit.pega.activity

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SimpleLogger {
    enum Level { DEBUG, INFO, WARN, ERROR }
    Level level = Level.DEBUG

    void log(Level lvl, String msg, Throwable t = null) {
        if (lvl.ordinal() < level.ordinal()) return
        def ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        def out = "[${ts}] ${lvl} - ${msg}"
        if (lvl == Level.ERROR) System.err.println(out) else println(out)
        if (t) {
            t.printStackTrace(System.err)
        }
    }

    void debug(String m) { log(Level.DEBUG, m) }
    void info(String m) { log(Level.INFO, m) }
    void warn(String m) { log(Level.WARN, m) }
    void error(String m, Throwable t = null) { log(Level.ERROR, m, t) }
}
