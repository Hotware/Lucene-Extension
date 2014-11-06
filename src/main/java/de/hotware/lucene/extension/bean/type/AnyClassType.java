package de.hotware.lucene.extension.bean.type;

/**
 * Types that can handle all classes. All fields annotated with such a type will
 * be passed through as the "raw" Object and not iterated upon.
 * 
 * <br />
 * 
 * Single-layered Sets/Lists are already supported in {
 * {@link de.hotware.lucene.extension.bean.BeanConverterImpl} and use
 * {@link de.hotware.lucene.extension.bean.type.Type} for internal conversion.
 * So don't use this interface for this.
 * 
 * <br />
 * 
 * This can also be used for rudimentary hierarchical indexing of beans
 * 
 * @author Martin
 */
public interface AnyClassType extends Type {

}
