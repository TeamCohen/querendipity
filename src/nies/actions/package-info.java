/**
 * <body>
 * Struts action classes as named in src/struts.xml; also includes validation 
 * specs for some actions.
 * 
 * <p>Most Action classes should subclass {@link nies.actions.NiesSupport} so 
 * that their
 * display code (JSPs) has access to the global text entities in 
 * <code>NiesSupport.properties</code>.  This allows us to change commonly-used
 * strings in one place (<code>NiesSupport.properties</code>) instead of having
 * to change them in every file where they are used.</p>
 * 
 * <p>Validation specs are named using the formula 
 * <code><i>classname</i>-<i>actionName</i>-validation.xml</code>. See 
 * {@link nies.validation} for custom validators and <a
 * href="http://struts.apache.org/1.2.4/userGuide/dev_validator.html">
 * The Struts Validator Guide</a> for built-in validators.</p>
 * </body>
 */
package nies.actions;