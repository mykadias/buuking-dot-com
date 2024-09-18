package br.com.myka.buuking.converter;

/**
 * Converter interface
 *
 * @param <S> as Source
 * @param <T> as Target
 */
public interface ConverterSourceToTarget<S, T> {
    T convert(S source);
}
