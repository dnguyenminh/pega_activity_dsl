package vn.com.fecredit.pega.activity

import groovy.transform.CompileStatic

/**
 * ExpressionEvaluator parses and evaluates simple boolean and numeric expressions.
 * It provides a tokenizer, converts tokens to RPN (Reverse Polish Notation),
 * and evaluates the RPN against a provided context map. GroovyShell is used as a
 * primary evaluation mechanism with a safe fallback to the internal RPN evaluator.
 */
@CompileStatic
class ExpressionEvaluator {

    private static final char L_PAREN = '('
    private static final char R_PAREN = ')'
    private static final char DOT = '.'
    private static final char UNDERSCORE = '_'
    private static final String ASSOC_L = 'L'
    private static final String ASSOC_R = 'R'
    private static final String KEY_PREC = 'prec'
    private static final String KEY_ASSOC = 'assoc'
    private static final String FALSE_STR = 'false'
    private static final int ZERO = 0
    private static final int ONE = 1
    private static final int MINUS_ONE = -1

    private static final SimpleLogger LOGGER = new SimpleLogger()

    enum TokenType {

        NUMBER,
        STRING,
        IDENT,
        OP,
        LPAREN,
        RPAREN

    }

    static class Token {

        TokenType type
        String value

        Token(TokenType t, String v) {
            type = t
            value = v
        }

        String toString() {
            return "${type}:${value}"
        }

    }

    // Debug disabled
    static final boolean DEBUG = false

    static List<Token> tokenize(String s) {
        List<Token> tokens = []
        if (s == null) {
            return tokens
        }
        int i = ZERO
        while (i < s.length()) {
            char c = s.charAt(i)
            if (Character.isWhitespace(c)) {
                i++
                continue
            }
            // operators and punctuation
            if (c == L_PAREN) {
                tokens << new Token(TokenType.LPAREN, String.valueOf(L_PAREN))
                i++
                continue
            }
            if (c == R_PAREN) {
                tokens << new Token(TokenType.RPAREN, String.valueOf(R_PAREN))
                i++
                continue
            }
            // multi-char operators
            if (i + 1 < s.length()) {
                String two = s.substring(i, i + 2)
                if (two == '&&' || two == '||' || two == '==' || two == '!=' || two == '>=' || two == '<=') {
                    tokens << new Token(TokenType.OP, two)
                    i += 2
                    continue
                }
            }
            // single-char operators
            if ('!><'.contains(String.valueOf(c))) {
                tokens << new Token(TokenType.OP, String.valueOf(c))
                i++
                continue
            }
            // string literal
            if (c == '"' || c == "'") {
                char quote = c
                int j = i + 1
                StringBuilder sb = new StringBuilder()
                while (j < s.length()) {
                    char cc = s.charAt(j)
                    if (cc == quote) {
                        break
                    }
                    if (cc == '\\' && j + 1 < s.length()) {
                        sb.append(s.charAt(j + 1))
                        j += 2
                        continue
                    }
                    sb.append(cc)
                    j++
                }
                tokens << new Token(TokenType.STRING, sb.toString())
                i = Math.min(s.length(), j + 1)
                continue
            }
            // number
            if (Character.isDigit(c)) {
                int j = i
                while (j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == DOT)) {
                    j++
                }
                tokens << new Token(TokenType.NUMBER, s.substring(i, j))
                i = j
                continue
            }
            // identifier (allow dots for property paths)
            if (Character.isLetter(c) || c == UNDERSCORE) {
                int j = i
                while (j < s.length()) {
                    char cc = s.charAt(j)
                    if (Character.isLetterOrDigit(cc) || cc == UNDERSCORE || cc == DOT) {
                        j++
                    } else {
                        break
                    }
                }
                tokens << new Token(TokenType.IDENT, s.substring(i, j))
                i = j
                continue
            }
            // unknown char -> treat as operator
            tokens << new Token(TokenType.OP, String.valueOf(c))
            i++
        }
        return tokens
    }

    static final Map<String, Object> OP_EQ = [(KEY_PREC): 3, (KEY_ASSOC): ASSOC_L]
    static final Map<String, Object> OP_CMP = [(KEY_PREC): 4, (KEY_ASSOC): ASSOC_L]
    static final Map<String, Object> OP_NOT = [(KEY_PREC): 5, (KEY_ASSOC): ASSOC_R]
    static Map<String, Object> ops = [
            '||': [(KEY_PREC): 1, (KEY_ASSOC): ASSOC_L],
            '&&': [(KEY_PREC): 2, (KEY_ASSOC): ASSOC_L],
            '==': OP_EQ, '!=': OP_EQ,
            '>': OP_CMP, '<': OP_CMP, '>=': OP_CMP, '<=': OP_CMP,
            '!': OP_NOT
    ]

    static List<Token> toRPN(List<Token> tokens) {
        List<Token> out = []
        List<Token> stack = []
        for (Token t in tokens) {
            switch (t.type) {
                case TokenType.NUMBER:
                case TokenType.STRING:
                case TokenType.IDENT:
                    out << t
                    break
                case TokenType.OP:
                    String op = t.value
                    while (stack) {
                        Token top = stack[MINUS_ONE]
                        if (top.type != TokenType.OP) {
                            break
                        }
                        String topOp = top.value
                        Map<String, Object> topInfo = (Map<String, Object>) ops[topOp]
                        Map<String, Object> curInfo = (Map<String, Object>) ops[op]
                        if (curInfo == null) {
                            break
                        }
                        Integer curPrec = (Integer) curInfo[KEY_PREC]
                        Integer topPrec = (Integer) topInfo[KEY_PREC]
                        String curAssoc = (String) curInfo[KEY_ASSOC]
                        boolean leftAssoc = (curAssoc == ASSOC_L && curPrec <= topPrec)
                        boolean rightAssoc = (curAssoc == ASSOC_R && curPrec < topPrec)
                        if (leftAssoc || rightAssoc) {
                            out << stack.pop()
                        } else {
                            break
                        }
                    }
                    stack << t
                    break
                case TokenType.LPAREN:
                    stack << t
                    break
                case TokenType.RPAREN:
                    while (stack && stack[MINUS_ONE].type != TokenType.LPAREN) {
                        out << stack.pop()
                    }
                    if (stack && stack[MINUS_ONE].type == TokenType.LPAREN) {
                        stack.pop()
                    }
                    break
            }
        }
        while (stack) {
            out << stack.pop()
        }
        return out
    }

    static Object evalRPN(List<Token> rpn, Map ctx) {
        List<Object> st = []
        for (int i = ZERO; i < rpn.size(); i++) {
            Token t = rpn.get(i)
            switch (t.type) {
                case TokenType.NUMBER:
                    st << (t.value.contains(String.valueOf(DOT)) ? new BigDecimal(t.value) : Long.parseLong(t.value))
                    break
                case TokenType.STRING:
                    st << t.value
                    break
                case TokenType.IDENT:
                    Object v = PropertyUtils.get(ctx, t.value)
                    if (v == null) {
                        if (t.value.equalsIgnoreCase('true')) {
                            v = true
                        } else if (t.value.equalsIgnoreCase(FALSE_STR)) {
                            v = false
                        }
                    }
                    st << v
                    break
                case TokenType.OP:
                    String op = t.value
                    if (op == '!') {
                        Object a = st.pop()
                        st << (!truthy(a))
                        continue
                    }
                    Object b = st.pop()
                    Object a = st.pop()
                    switch (op) {
                        case '||':
                            st << ((truthy(a) || truthy(b))); break
                        case '&&':
                            st << ((truthy(a) && truthy(b))); break
                        case '==':
                            st << (equalsVal(a, b)); break
                        case '!=':
                            st << (!equalsVal(a, b)); break
                        case '>':
                            st << (compare(a, b) > ZERO); break
                        case '<':
                            st << (compare(a, b) < ZERO); break
                        case '>=':
                            st << (compare(a, b) >= ZERO); break
                        case '<=':
                            st << (compare(a, b) <= ZERO); break
                        default:
                            throw new IllegalArgumentException("Unsupported op: ${op}")
                    }
                    break
            }
        }
        return st ? st[MINUS_ONE] : null
    }

    static boolean equalsVal(Object a, Object b) {
        if (a == null && b == null) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (Number.isInstance(a) && Number.isInstance(b)) {
            try {
                BigDecimal ad = new BigDecimal(String.valueOf(a))
                BigDecimal bd = new BigDecimal(String.valueOf(b))
                return ad == bd
            } catch (GroovyRuntimeException e) {
                if (DEBUG) {
                    LOGGER.debug(e.toString())
                }
                return String.valueOf(a) == String.valueOf(b)
            }
        }
        if (Number.isInstance(a) && String.isInstance(b)) {
            try {
                return new BigDecimal(String.valueOf(a)) == new BigDecimal((String) b)
            } catch (GroovyRuntimeException e) {
                if (DEBUG) {
                    LOGGER.debug(e.toString())
                }
            }
        }
        if (Number.isInstance(b) && String.isInstance(a)) {
            try {
                return new BigDecimal((String) a) == new BigDecimal(String.valueOf(b))
            } catch (GroovyRuntimeException e) {
                if (DEBUG) {
                    LOGGER.debug(e.toString())
                }
            }
        }
        return String.valueOf(a) == String.valueOf(b)
    }

    static int compare(Object a, Object b) {
        if (a == null && b == null) {
            return ZERO
        }
        if (a == null) {
            return MINUS_ONE
        }
        if (b == null) {
            return ONE
        }
        if (Number.isInstance(a) && Number.isInstance(b)) {
            BigDecimal ad = new BigDecimal(a.toString())
            BigDecimal bd = new BigDecimal(b.toString())
            return ad <=> bd
        }
        try {
            BigDecimal ad = new BigDecimal(String.valueOf(a))
            BigDecimal bd = new BigDecimal(String.valueOf(b))
            return ad <=> bd
        } catch (GroovyRuntimeException e) {
            return String.valueOf(a) <=> String.valueOf(b)
        }
    }

    static boolean truthy(Object x) {
        if (x == null) {
            return false
        }
        if (Boolean.isInstance(x)) {
            return (Boolean) x
        }
        if (Number.isInstance(x)) {
            try {
                return new BigDecimal(String.valueOf(x)) != BigDecimal.ZERO
            } catch (GroovyRuntimeException e) {
                if (DEBUG) {
                    LOGGER.debug(e.toString())
                }
                return ((Number) x).doubleValue() != ZERO
            }
        }
        if (String.isInstance(x)) {
            return ((String) x).length() > ZERO
        }
        if (Collection.isInstance(x)) {
            return !((Collection) x).empty
        }
        return true
    }

    static boolean evaluate(String expr, Map ctx) {
        if (expr == null) {
            return false
        }
        // Use GroovyShell with a binding that exposes ctx entries as variables
        try {
            Binding binding = new Binding()
            if (ctx != null) {
                ctx.each { Object k, Object v ->
                        // only set valid variable names;
                        // if key contains invalid chars, still set as-is for map access via 'ctx'
                        if (String.isInstance(k)) {
                            String key = (String) k
                            if (key.matches('[A-Za-z_][A-Za-z0-9_]*')) {
                                binding.setVariable(key, v)
                            }
                        }
                }
                // also expose full ctx as 'ctx' variable for explicit access
                binding.setVariable('ctx', ctx)
            }
            GroovyShell shell = new GroovyShell(binding)
            Object raw = shell.evaluate(expr)
            return truthy(raw)
        } catch (GroovyRuntimeException e) {
            // fallback to RPN evaluator if Groovy evaluation fails
            try {
                List<Token> tokens = tokenize(expr)
                if (!tokens) {
                    return false
                }
                List<Token> rpn = toRPN(tokens)
                Object res = evalRPN(rpn, ctx)
                return truthy(res)
            } catch (GroovyRuntimeException ex) {
                if (DEBUG) {
                    LOGGER.debug(ex.toString())
                }
                return false
            }
        }
    }

}
