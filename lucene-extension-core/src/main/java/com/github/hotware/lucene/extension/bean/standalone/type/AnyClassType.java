/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.bean.standalone.type;

/**
 * Types that can handle all classes. All fields annotated with such a type will
 * be passed through as the "raw" Object and not iterated upon.
 * 
 * <br><br>
 * 
 * Single-layered Sets/Lists are already supported in {
 * {@link com.github.hotware.lucene.extension.bean.standalone.converter.BeanConverterImpl} and use
 * {@link com.github.hotware.lucene.extension.bean.standalone.type.Type} for internal conversion.
 * So don't use this interface for this.
 * 
 * <br><br>
 * 
 * This can also be used for rudimentary hierarchical indexing of beans
 * 
 * @author Martin Braun
 */
public interface AnyClassType extends Type {

}
