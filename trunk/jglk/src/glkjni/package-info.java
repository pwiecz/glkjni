/**
 * The interfaces required by the GlkJNI C library
 * (Glk API version 0.7.0).
 * <p>
 * A Java frontend must include a {@code glkjni} package which contains:
 * <ul>
 * <li>a concrete class {@code GlkFactory}, and</li>
 * <li>interfaces, concrete classes, or abstract classes
 *     {@link glkjni.Glk}, {@link glkjni.GlkWindow}. and
 *     {@link glkjni.GlkSChannel}.
 * </ul>
 * <p>
 * This version of the {@code glkjni} package has suitable definitions
 * for the last three; {@code GlkFactory} must be defined in each
 * frontend. The {@link org.brickshadow.jglk jglk package} also provides
 * other useful classes and interfaces, including convenient
 * {@link org.brickshadow.jglk.window subclasses of {@code GlkWindow}} for
 * different window types.
 * <p>
 * See {@link glkjni.ExampleGlkFactory} for the methods which GlkJNI
 * requires from a {@code GlkFactory} class; if you want to write a
 * frontend without using the {@code jglk} package, see
 * {@link glkjni.ExampleGlkWindow} for the methods required of the
 * {@code GlkWindow} interface.
 * 
 * <h2>API Notes</h2>
 * 
 * <h3>C functions vs. Java methods</h3>
 * 
 * Glk functions that are completely handed by GlkJNI are not included
 * in these interfaces.
 * <p>
 * Implementations of these interfaces can assume that method parameters
 * are valid and meaningful as defined by the Glk API specification;
 * if a C Glk function is called with invalid or ignorable values,
 * GlkJNI will not bother to call its Java counterpart.
 * 
 * <h3>{@code glui32} vs. {@code int}
 * 
 * Faced with the choice of using signed 32-bit {@code int}s or signed
 * 64-bit {@code long}s to pass {@code glui32} values between C and Java,
 * I thought that it would be easier for writers of Java frontends to
 * only have to deal with 32-bit values.
 * <p>
 * GlkJNI passes {@glui32} values to frontend methods as Java
 * @code int}s (except for flag parameters, which are passed as
 * {@code boolean}s). To make everything work properly:
 * <ul>
 * <li>Java code should not perform any arithmetic except tests for
 *     equality and bit-mask operations on values that are interpreted
 *     as constants.</li>
 * <li>GlkJNI does not forward calls to Java if a parameter that should
 *     not be negative (such as cursor positions or resource numbers) is
 *     greater than {@code 2 ^ 31}.
 * <p>
 * {@code int} values returned from frontend methods are
 * converted back to {@glui32}s in C, so the usual warnings apply about
 * negative numbers being interpreted as large positive numbers.
 * 
 * @version 0.6
 */
package glkjni;