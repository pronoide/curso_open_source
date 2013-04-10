package spring.examples.spel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import spring.examples.spel.model.User;

public class TestParser {

    public final static ExpressionParser PARSER = new SpelExpressionParser();

    @Test
    public void testStringLiteral() {
        Expression exp = PARSER.parseExpression("'foo'");
        String value = exp.getValue(String.class);
        assertEquals("foo", value);
    }
    
    @Test
    public void testPrimitives() {
        assertTrue(0 == PARSER.parseExpression("0").getValue(byte.class));
        assertTrue(0 == PARSER.parseExpression("0").getValue(short.class));
        assertTrue(0 == PARSER.parseExpression("0").getValue(int.class));
        assertTrue(0L == PARSER.parseExpression("0L").getValue(long.class));
        assertTrue(0.1F == PARSER.parseExpression("0.1F").getValue(float.class));
        assertTrue(0.1D == PARSER.parseExpression("0.1D").getValue(double.class));
        assertTrue(PARSER.parseExpression("true").getValue(boolean.class));
        assertTrue('c' == PARSER.parseExpression("'c'").getValue(char.class));
    }
    
    @Test
    public void testStateRegistration() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("foo", "bar");
        String bar = PARSER.parseExpression("#foo").getValue(context, String.class);
        assertEquals("bar", bar);
    }
    
    @Test
    public void testBehaviourRegistration() throws SecurityException, NoSuchMethodException {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("foo", "bar");
        context.registerFunction("quote", StringUtils.class.getDeclaredMethod("quote", String.class));
        String bar = PARSER.parseExpression("#quote(#foo)").getValue(context, String.class);
        assertEquals("'bar'", bar);
    }
    
    @Test
    public void testRegisterFunction2() throws SecurityException, NoSuchMethodException {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(0, 1));
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.registerFunction("max", Collections.class.getDeclaredMethod("max", new Class[] { Collection.class }));
        context.setRootObject(list);
        Integer max = PARSER.parseExpression("#max(#root)").getValue(context, Integer.class);
        assertSame(1, max);
    }
    
    @Test
    public void testConstructorInvocation() {
        Expression exp = PARSER.parseExpression("new String('foo')");
        String value = exp.getValue(String.class);
        assertEquals("foo", value);
    }
    
    @Test
    public void testMethodInvocation() {
        Expression exp = PARSER.parseExpression("'foo'.concat('!')");
        String value = exp.getValue(String.class);
        assertEquals("foo!", value);
    }
    
    @Test
    public void testStaticInvocation() {
        Expression exp = PARSER.parseExpression("T(Math).random() * 100.0");
        double value = exp.getValue(double.class);
        assertNotNull(value);
    }
    
    @Test
    public void testJavaBeanProperty() {
        Expression exp = PARSER.parseExpression("'foo'.bytes");
        byte[] bytes = exp.getValue(byte[].class);
        assertTrue(Arrays.equals("foo".getBytes(), bytes));
    }
    
    @Test
    public void testNestedProperties() {
        Expression exp = PARSER.parseExpression("'foo'.bytes.length");
        int length = exp.getValue(int.class);
        assertSame("foo".getBytes().length, length);
    }
    
    //Unchanging root object 
    //When using the parser with an object one has the option of either supplying 
    //that object as the root object to the context or to register it as variable 
    //or to supply it on every invocation of the parseExpression method. 
    //Here the first of those is demonstrated. the root object is not changing with
    //every invocation of the parser so it can be supplied once and parsing of expressions can be performed multiple times.
    @Test
    public void testQueryUnchangingRootObjectProperty() {
        Expression exp = PARSER.parseExpression("name");
        EvaluationContext context = new StandardEvaluationContext(new User("foo"));
        String name = exp.getValue(context, String.class);
        assertEquals("foo", name);
    }
       
   
    //Changing root object
    //In the opposite scenario if the root object is expected to be changing between expression parse invocations 
    //then it can be supplied to the parser when parsing the expression itself. This is the concurrent option.
    @Test
    public void testQueryChangingRootObjectProperty() {
        Expression exp = PARSER.parseExpression("name");
        String name = exp.getValue(new User("foo"), String.class);
        assertEquals("foo", name);
    }
    
    //Changing root object with cached context
    //The need for caching the evaluation context arises from the fact that the construction of the 
    //context is actually quite expensive. As a result here the context is created only once with a 
    //root object and is reused for parsing various expressions.
    @Test
    public void testQueryChangingRootObjectPropertyWithCachedContext() {
        Expression exp = PARSER.parseExpression("name");
        EvaluationContext context = new StandardEvaluationContext(new User("foo"));
        assertEquals("foo", exp.getValue(context, String.class));
        String bar = exp.getValue(context, new User("bar"), String.class);
        String baz = exp.getValue(context, new User("baz"), String.class);
        assertEquals("bar", bar);
        assertEquals("baz", baz);
    }
    
    
    //MUTATORS
    
    //Here a list element is flipped from true to false.Note that false is recognised as a boolean and converted as such by SpEL 
    //and also that array/list elements can be accessed using indices just like in the language itself.
    
    @Test
    public void mutateTypedProperty() {
        List<Boolean> list = new ArrayList<Boolean>(Arrays.asList(true));
        EvaluationContext context = new StandardEvaluationContext(list);
        PARSER.parseExpression("#root[0]").setValue(context, "false");
        assertFalse(list.iterator().next());
    }
    
    @Test
    public void testAssignmentOnRootObjectDirectly() {
        Calendar calendar = Calendar.getInstance();
        PARSER.parseExpression("timeInMillis").setValue(calendar, 0);
        assertTrue(0 == calendar.getTimeInMillis());
    }
    
    @Test
    public void testAssignmentOnContext() {
        Calendar calendar = Calendar.getInstance();
        EvaluationContext context = new StandardEvaluationContext(calendar);
        PARSER.parseExpression("timeInMillis").setValue(context, 0);
        assertTrue(0 == calendar.getTimeInMillis());
    }
    
    @Test
    public void testAssignmentInExpression() {
        Calendar calendar = Calendar.getInstance();
        long value = PARSER.parseExpression("timeInMillis = 0").getValue(calendar, long.class);
        assertTrue(0 == value);
    }
    
    //OPERATORS

    // Relational operators
    @Test
    public void testRelationalOperators() {
        assertTrue(PARSER.parseExpression("2==2").getValue(boolean.class));
        assertTrue(PARSER.parseExpression("2<3").getValue(boolean.class));
        assertTrue(PARSER.parseExpression("3>2").getValue(boolean.class));
        assertTrue(PARSER.parseExpression("0!=1").getValue(boolean.class));
    }
    
    //Logical operators
    @Test
    public void testLogicalOperators() {
        assertTrue(PARSER.parseExpression("true and true").getValue(boolean.class));
        assertTrue(PARSER.parseExpression("true or true").getValue(boolean.class));
        assertTrue(PARSER.parseExpression("!false").getValue(boolean.class));
        assertTrue(PARSER.parseExpression("not false").getValue(boolean.class));
        assertTrue(PARSER.parseExpression("true and not false").getValue(boolean.class));
    }
    
    //Mathematical operators
    @Test
    public void testMathematicalOperators() {
        assertSame(2, PARSER.parseExpression("1+1").getValue(int.class));
        assertSame(0, PARSER.parseExpression("1-1").getValue(int.class));
        assertSame(1, PARSER.parseExpression("1/1").getValue(int.class));
        assertSame(1, PARSER.parseExpression("1*1").getValue(int.class));
        assertSame(1, PARSER.parseExpression("1^1").getValue(int.class));
        assertTrue(1D == PARSER.parseExpression("1e0").getValue(double.class));
        assertEquals("foobar", PARSER.parseExpression("'foo'+'bar'").getValue(String.class));
    }
    
    @Test
    public void testTernaryElvisAndSafeNavigationOperators() {
        assertEquals("foo", PARSER.parseExpression("true ? 'foo' : 'bar'").getValue(String.class));
        assertEquals("null", PARSER.parseExpression("null?:'null'").getValue(String.class));
        assertEquals(null, PARSER.parseExpression("null?.foo").getValue(String.class));
    }
    
    //UTILITY
    
    @Test
    public void testGetSystemProperty() {
        System.setProperty("foo", "bar");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("systemProperties", System.getProperties());
        Expression exp = PARSER.parseExpression("#systemProperties['foo']");
        String value = exp.getValue(context, String.class);
        assertEquals("bar", value);
    }
    
    //SpEL has an understanding of dates and is able to add dates together as well using the ‘+’ operator.
    @Test
    public void testDateParsing() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 11, 25, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTime(), PARSER.parseExpression("'2010/12/25'").getValue(Date.class));
    }
    
    //The instanceof ‘keyword’ is supported exactly as in Java but the right hand side argument must be provided 
    //as an instance of Class by using the T() qualification.
    @Test
    public void testInstanceOfCheck() {
        assertTrue(PARSER.parseExpression("0 instanceof T(Integer)").getValue(boolean.class));
    }
    
    
    @Test
    public void testRegularExpressionMatch() {
        assertTrue(PARSER.parseExpression("-0.1e-4 matches '^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$'").getValue(
                boolean.class));
    }
    
    //the variable named #this refers to the current list element accessible within that particular iteration over the list.
    @Test
    public void testListSelection() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("list", list);
        List<?> evenNumberList = PARSER.parseExpression("#list.?[#this%2==0]").getValue(context, List.class);
        assertEquals(Arrays.asList(0, 2, 4, 6, 8), evenNumberList);
    }
    
    @Test
    public void testMapSelectionSimple() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo", "bar");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("map", map);
        Expression exp = PARSER.parseExpression("#map['foo']");
        String value = exp.getValue(context, String.class);
        assertEquals("bar", value);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testSelectionFromMapAdvanced() {
        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        /*
         * NOTE: select all map entries with value less than 3
         */
        Map<Integer, Integer> all = (Map<Integer, Integer>) PARSER.parseExpression("#root.?[value<3]").getValue(map);
        assertTrue(all.size() == 2);
        Iterator<Integer> iterator = all.keySet().iterator();
        assertTrue(iterator.next() == 1);
        assertTrue(iterator.next() == 2);
        /*
         * NOTE: select first entry with value less than 3
         */
        Map<Integer, Integer> first = (Map<Integer, Integer>) PARSER.parseExpression("#root.^[value<3]").getValue(map);
        assertTrue(first.keySet().iterator().next() == 1);
        /*
         * NOTE: select last entry with value less than 3
         */
        Map<Integer, Integer> last = (Map<Integer, Integer>) PARSER.parseExpression("#root.$[value<3]").getValue(map);
        // assertTrue(last.keySet().iterator().next() == 2);
        assertTrue(last.size() == 1);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testCollectionProjection() {
        List<User> list = new ArrayList<User>(Arrays.asList(new User("foo"), new User("bar"), new User("baz")));
        List<String> names = (List<String>) PARSER.parseExpression("#root.![name]").getValue(list);
        assertEquals(Arrays.asList("foo", "bar", "baz"), names);
    }
    
    
    //<property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/<
    //<property name="defaultLocale" value="#{ systemProperties['user.region'] }"/<
    //<property name="myOtherBeanProperty" value="#{ myOtherBean.property }"/>
    
    //@Value("#{ systemProperties['userName'] }")
    
    
    //• Exposed by default
    //• "systemProperties", "systemEnvironment"
    //• access to all Spring-defined beans by name
    
    //Web-specific attributes:
    // • "contextParameters"
    // • "contextAttributes"
    // • "request"
    // • "session"
    // • JSF objects in a JSF request context
}